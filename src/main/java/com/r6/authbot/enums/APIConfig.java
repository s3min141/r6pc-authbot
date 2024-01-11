package com.r6.authbot.enums;

public enum APIConfig {
    BASE_UBISERVICES("https://public-ubiservices.ubi.com"),
    UBISOFT_APPID("e3d5ea9e-50bd-43b7-88bf-39794f4e3d40"),
    API_ACCOUNT_USERNAME(),
    API_ACCOUNT_PASSWORD(),
    API_SESSION_TICKET(),
    API_SESSION_ID();

    private String strVal;

    private APIConfig() {

    }

    private APIConfig(String strVal) {
        this.strVal = strVal;
    }

    public void set(String strVal) {
        this.strVal = strVal;
    }

    public String get() {
        return strVal;
    }
}
