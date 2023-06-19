package kr.co.moneybridge.core;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.model.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDateTime;

public class WithMockAdminFactory extends MockDummyEntity implements WithSecurityContextFactory<WithMockAdmin> {
    @Override
    public SecurityContext createSecurityContext(WithMockAdmin mockAdmin) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User admin = User.builder()
                .id(mockAdmin.id())
                .name(mockAdmin.name())
                .password("password1234")
                .email(mockAdmin.name() + "@nate.com")
                .phoneNumber("01012345678")
                .role(mockAdmin.role())
                .profile("profile.png")
                .hasDoneReview(false)
                .hasDoneReservation(false)
                .hasDoneBoardBookmark(false)
                .createdAt(LocalDateTime.now())
                .build();
        MyUserDetails userDetails = new MyUserDetails(admin);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}