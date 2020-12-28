package com;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        File[] files = new File("src/main/resources/battles/").listFiles();
        if (files == null) {
            System.err.println("cannot list files");
            return;
        }

        JSONParser parser = new JSONParser();
        List<BuffAndCast> buffsAndCasts = new ArrayList<>();
        try (FileReader reader = new FileReader("src/main/resources/checklist/checklist.json"))
        {
            Object obj = parser.parse(reader);
            JSONObject checklist = (JSONObject) obj;
            JSONArray buffs = (JSONArray) checklist.get("buff");
            buffs.forEach(buff -> addBuffAndCast(buffsAndCasts, (JSONObject) buff));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<>();

        for (File file : files) {
            System.out.println("解析" + file.getName());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String log;
            while ((log = reader.readLine()) != null) {
                fetchLog(resultMap, buffsAndCasts, log);
            }
            reader.close();

            // 计算本次出勤
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
        Iterator<Map.Entry<String, Map<String, Integer>>> it = resultMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<String, Integer>> entry = it.next();
            Integer c = entry.getValue().get("出勤");
            if (c == null || c == 0) {
                it.remove();
            }
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

        // 按消耗药水价格降序排列
        resultMap = sortByPrice(resultMap);

        // 输出CSV到控制台
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
        String logType = getLogType(log);
        for (BuffAndCast buffAndCast : buffsAndCasts) {
            if (!logType.equals(buffAndCast.getLogType())) {
                continue;
            }
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

    private static void addBuffAndCast(List<BuffAndCast> bufflist, JSONObject buff) {
        bufflist.add(new BuffAndCast((String) buff.get("id"),
                                     (String) buff.get("name"),
                                     ((Long) buff.get("price")).intValue(), 
                                     (String) buff.get("log_type")));

    }
}
