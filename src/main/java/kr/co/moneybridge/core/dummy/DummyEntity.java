package kr.co.moneybridge.core.dummy;

import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserPropensity;
import kr.co.moneybridge.model.user.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyEntity {
    public User newUser(String username){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username+"@nate.com")
                .phoneNumber("01012345678")
                .role(UserRole.USER)
                .status(true)
                .build();
    }
    public User newUserWithPropensity(String username){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username+"@nate.com")
                .phoneNumber("01012345678")
                .propensity(UserPropensity.AGGRESSIVE)
                .role(UserRole.USER)
                .status(true)
                .build();
    }

    public PB newPB(String username, Branch branch){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username+"@nate.com")
                .phoneNumber("01012345678")
                .branch(branch)
                .profile("profile.png")
                .businessCard("card.png")
                .career(10)
                .speciality1(PBSpeciality.BOND)
                .intro(username+" 입니다")
                .msg("한줄메시지..")
                .reservationInfo("10분 미리 도착해주세요")
                .consultStart("09:00")
                .consultEnd("18:00")
                .consultNotice("월요일 불가능합니다")
                .role(PBRole.PB)
                .status(PBStatus.ACTIVE)
                .build();
    }

    public PB newPBWithSpeciality(String username, Branch branch){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return PB.builder()
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username+"@nate.com")
                .phoneNumber("01012345678")
                .branch(branch)
                .profile("profile.png")
                .businessCard("card.png")
                .career(10)
                .speciality1(PBSpeciality.BOND)
                .speciality2(PBSpeciality.ETF)
                .intro(username+" 입니다")
                .msg("한줄메시지..")
                .reservationInfo("10분 미리 도착해주세요")
                .consultStart("09:00")
                .consultEnd("18:00")
                .consultNotice("월요일 불가능합니다")
                .role(PBRole.PB)
                .status(PBStatus.ACTIVE)
                .build();
    }

    public Board newBoard(String title, PB pb){
        return Board.builder()
                .pb(pb)
                .title(title)
                .thumbnail("thumbnail.png")
                .content("content 입니다")
                .tag1()
                .phoneNumber("01012345678")
                .role(UserRole.USER)
                .status(true)
                .build();
    }

    public User newMockUser(Long id, String username){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id(id)
                .name(username)
                .password(passwordEncoder.encode("password1234"))
                .email(username+"@nate.com")
                .phoneNumber("01012345678")
                .propensity(UserPropensity.AGGRESSIVE)
                .role(UserRole.USER)
                .status(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
