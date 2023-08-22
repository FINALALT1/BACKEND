package kr.co.moneybridge.core.util;

public enum Template {
    ADD_RESERVATION("template_001"),
    CANCEL_RESERVATION_BY_PB("template_002"),
    CANCEL_RESERVATION_BY_USER("template_003"),
    CONFIRM_RESERVATION("template_004"),
    NEW_CONTENT("template_005"),
    CANCEL_RESERVATION_BY_PB_V2("template_006"),
    CANCEL_RESERVATION_BY_USER_V2("template_007"),
    NEW_REVIEW("template_008");

    private String code;

    Template(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
