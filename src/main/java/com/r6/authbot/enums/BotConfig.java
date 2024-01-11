package com.r6.authbot.enums;

public enum BotConfig {
    PROPERTIES_FILE_NAME("application.properties"),
    DB_URL(),
    DB_USERNAME(),
    DB_PASSWORD(),
    BOT_TOKEN(),
    AUTH_CHANNEL_ID(),
    LEADERBOARD_CHANNEL_ID(),
    SPECIAL_ROLE_ID(),
    MIN_RANKER_MMR(4700);

    private String strVal;
    private Integer intVal;

    private BotConfig() {

    }

    private BotConfig(String strVal) {
        this.strVal = strVal;
    }

    private BotConfig(Integer intVal) {
        this.intVal = intVal;
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
}
