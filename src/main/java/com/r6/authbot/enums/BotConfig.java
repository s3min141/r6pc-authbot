package com.r6.authbot.enums;

import java.util.ArrayList;
import java.io.File;

public enum BotConfig {
    PROPERTIES_FILE_NAME("application.properties"),
    DB_URL(),
    DB_USERNAME(),
    DB_PASSWORD(),
    BOT_TOKEN(),
    AUTH_CHANNEL_ID(),
    LEADERBOARD_CHANNEL_ID(),
    LEADERBOARD_IMGS(),
    SPECIAL_ROLE_ID(),
    MIN_RANKER_MMR(4700);

    private String strVal;
    private Integer intVal;
    private ArrayList<File> arrayVal; 

    private BotConfig() {

    }

    private BotConfig(String strVal) {
        this.strVal = strVal;
    }

    private BotConfig(Integer intVal) {
        this.intVal = intVal;
    }

    private BotConfig(ArrayList<File> arrayVal) {
        this.arrayVal = arrayVal;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public String getStrVal() {
        return strVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setArrayVal(ArrayList<File> arrayVal) {
        this.arrayVal = arrayVal;
    }

    public ArrayList<File> getArrayVal() {
        return arrayVal;
    }
}
