package kr.co.moneybridge.core;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.dummy.DummyEntity;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyDateUtil;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDateTime;

public class WithMockPBFactory extends MockDummyEntity implements WithSecurityContextFactory<WithMockPB> {
    @Override
    public SecurityContext createSecurityContext(WithMockPB mockPB) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        PB pb = PB.builder()
                .id(mockPB.id())
                .name(mockPB.name())
                .password("password1234")
                .email(mockPB.name() + "@nate.com")
                .phoneNumber("01012345678")
                .branch(newMockBranch(1L, newMockCompany(1L, "미래에셋증권"), 0))
                .profile("profile.png")
                .businessCard("card.png")
                .career(10)
                .speciality1(PBSpeciality.BOND)
                .intro("김pb 입니다")
                .msg("한줄메시지..")
                .reservationInfo("10분 미리 도착해주세요")
                .consultStart(MyDateUtil.StringToLocalTime("09:00"))
                .consultEnd(MyDateUtil.StringToLocalTime("18:00"))
                .consultNotice("월요일 불가능합니다")
                .role(mockPB.role())
                .status(PBStatus.ACTIVE)
                .build();
        MyUserDetails userDetails = new MyUserDetails(pb);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}