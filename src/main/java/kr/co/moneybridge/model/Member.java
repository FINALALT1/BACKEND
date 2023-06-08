package kr.co.moneybridge.model;

public interface Member {
    Long getId();
    Role getRole();
    String getPassword();
    String getEmail();
    void withdraw();
}
