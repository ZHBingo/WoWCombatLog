package com;

import org.apache.commons.lang3.StringUtils;

public class Action {
    private String player;
    private String target;
    private String spellID;
    private String spellName;


    public Action(String log) {
        String al = StringUtils.split(log, " ")[2];
        String[] als = al.split(",");
        if ("SPELL_CAST_SUCCESS".equals(als[0])) {
            this.player = StringUtils.remove(StringUtils.remove(als[2], "\""), "-怒炉");
            this.target =
            this.spellID = als[9];
            this.spellName = StringUtils.remove(als[10], "\"");
        }
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getSpellID() {
        return spellID;
    }

    public void setSpellID(String spellID) {
        this.spellID = spellID;
    }

    public String getSpellName() {
        return spellName;
    }

    public void setSpellName(String spellName) {
        this.spellName = spellName;
    }
}
