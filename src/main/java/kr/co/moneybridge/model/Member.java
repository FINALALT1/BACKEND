package kr.co.moneybridge.model;

import kr.co.moneybridge.model.user.UserPropensity;

import java.time.LocalDateTime;

public interface Member {
    Long getId();
    Role getRole();
    String getPassword();
    String getEmail();
    String getName();
    String getPhoneNumber();
    LocalDateTime getCreatedAt();
    UserPropensity getPropensity();
    void updatePassword(String password);
    void updateName(String name);
    void updatePhoneNumber(String phoneNumber);
}
