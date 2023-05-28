package kr.co.moneybridge.core.dummy;

import kr.co.moneybridge.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username+"@nate.com")
                .role("USER")
                .status(true)
                .build();
    }

    public User newMockUser(Long id, String username, String fullName){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .fullName(fullName)
                .email(username+"@nate.com")
                .role("USER")
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
