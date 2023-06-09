package kr.co.moneybridge.core;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDateTime;

public class WithMockUserFactory implements WithSecurityContextFactory<WithMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = User.builder()
                .id(mockUser.id())
                .name(mockUser.name())
                .password("password1234")
                .email(mockUser.name() + "@nate.com")
                .phoneNumber("01012345678")
                .role(mockUser.role())
                .profile("profile.png")
                .createdAt(LocalDateTime.now())
                .build();
        MyUserDetails userDetails = new MyUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}