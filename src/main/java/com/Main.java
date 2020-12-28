package com;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        File[] files = new File("D:\\Workspaces\\MyWorkspace\\WoWCombatLog\\battles").listFiles();
        if (files == null) {
            System.err.println("cannot list files");
            return;
        }

        List<BuffAndCast> buffsAndCasts = new ArrayList<>();
        buffsAndCasts.add(new BuffAndCast("11334", "强效敏捷", 2));
        buffsAndCasts.add(new BuffAndCast("11405", "巨人药剂", 3));
        buffsAndCasts.add(new BuffAndCast("17038", "冬泉火酒", 14));
        buffsAndCasts.add(new BuffAndCast("17538", "猫鼬药剂", 25));
        buffsAndCasts.add(new BuffAndCast("17539", "强效奥法药剂", 23));
        buffsAndCasts.add(new BuffAndCast("17531", "恢复法力", 9));
        buffsAndCasts.add(new BuffAndCast("26276", "强效火力", 17));
        buffsAndCasts.add(new BuffAndCast("17544", "防护冰霜", 15));
        buffsAndCasts.add(new BuffAndCast("17548", "防护暗影", 36));
        buffsAndCasts.add(new BuffAndCast("11474", "暗影强化", 14));
        buffsAndCasts.add(new BuffAndCast("27869", "黑暗符文", 48));


        Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<>();

        for (File file : files) {
            System.out.println("解析" + file.getName());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String log;
            while ((log = reader.readLine()) != null) {
                String logType = getLogType(log);
                if (logType.equals("SPELL_CAST_SUCCESS")) {
                    fetchLog(resultMap, buffsAndCasts, log);
                }
            }
            reader.close();

            // 计算出勤
            for (String key : resultMap.keySet()) {
                Integer c = resultMap.get(key).remove("MARK");
                if (c != null && c > 20) { // wcl有20次以上（不含）成功施法，算作出勤一次
                    int attend = resultMap.get(key).computeIfAbsent("出勤", k -> 0);
                    attend++;
                    resultMap.get(key).put("出勤", attend);
                }
            }
        }

        // 删除出勤为0的人员
        List<String> removeKey = new ArrayList<>();
        for (String key : resultMap.keySet()) {
            Integer c = resultMap.get(key).get("出勤");
            if (c == null || c == 0) { // wcl有20次以上（不含）成功施法，算作出勤一次
                removeKey.add(key);
            }
        }
        for (String key : removeKey) {
            resultMap.remove(key);
        }

        // 计算价格
        int total = 0;
        for (String key : resultMap.keySet()) {
            int subTotal = 0;
            for (BuffAndCast buff : buffsAndCasts) {
                Integer c = resultMap.get(key).getOrDefault(buff.getName(), 0);
                int p = c * buff.getPrice();
                subTotal += p;
                resultMap.get(key).put(buff.getPriceName(), p);
            }
            total += subTotal;
            resultMap.get(key).put("总计", subTotal);
        }

        resultMap = sortByPrice(resultMap);

        // output
        System.out.println("CSV OUTPUT----------------");
        List<String> temp = new ArrayList<>();
        temp.add("角色");
        for (BuffAndCast b : buffsAndCasts) {
            temp.add(b.getName());
        }
        for (BuffAndCast b : buffsAndCasts) {
            temp.add(b.getPriceName());
        }
        temp.add("总计");
        temp.add("出勤（总" + files.length + "）");
        System.out.println(StringUtils.join(temp, ","));

        for (String key : resultMap.keySet()) {
            temp = new ArrayList<>();
            temp.add(key);
            Map<String, Integer> map = resultMap.get(key);
            for (BuffAndCast b : buffsAndCasts) {
                temp.add(String.valueOf(map.getOrDefault(b.getName(), 0)));
            }
            for (BuffAndCast b : buffsAndCasts) {
                temp.add(String.valueOf(map.getOrDefault(b.getPriceName(), 0)));
            }
            temp.add(String.valueOf(map.getOrDefault("总计", 0)));
            temp.add(String.valueOf(map.getOrDefault("出勤", 0)));
            System.out.println(StringUtils.join(temp, ","));
        }
        System.out.println(StringUtils.repeat(",", temp.size() - 2) + total);
        System.out.println("CSV OUTPUT----------------");
    }

    public static void fetchLog(Map<String, Map<String, Integer>> resultMap, List<BuffAndCast> buffsAndCasts, String log) {
        Action action = new Action(log);
        for (BuffAndCast buffAndCast : buffsAndCasts) {
            Map<String, Integer> map = resultMap.computeIfAbsent(action.getPlayer(), k -> new HashMap<>());
            int count = map.computeIfAbsent("MARK", k -> 0);
            map.put("MARK", count + 1);
            if (buffAndCast.getId().equals(action.getSpellID()) && buffAndCast.getName().equals(action.getSpellName())) {
                count = map.computeIfAbsent(buffAndCast.getName(), k -> 0);
                map.put(buffAndCast.getName(), count + 1);
            }
        }
    }

    public static Map<String, Map<String, Integer>> sortByPrice(Map<String, Map<String, Integer>> map) {
        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        map.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().getOrDefault("总计", 0).compareTo(p1.getValue().getOrDefault("总计", 0)))
                .collect(Collectors.toList()).forEach(ele -> result.put(ele.getKey(), ele.getValue()));
        return result;
    }


    public static String getLogType(String log) {
        String[] ls = StringUtils.split(log, " ")[2].split(",");
        if (ls.length == 28 && ls[1].startsWith("Player")) {
            return ls[0];
        } else {
            return "-1";
        }
    }
}
