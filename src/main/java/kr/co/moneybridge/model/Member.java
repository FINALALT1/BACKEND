package kr.co.moneybridge.model;

import kr.co.moneybridge.model.user.UserPropensity;

public interface Member {
    Long getId();
    Role getRole();
    String getPassword();
    String getEmail();
    String getName();
    String getPhoneNumber();
    UserPropensity getPropensity();
    void updatePassword(String password);
    void updateName(String name);
    void updatePhoneNumber(String phoneNumber);
}
