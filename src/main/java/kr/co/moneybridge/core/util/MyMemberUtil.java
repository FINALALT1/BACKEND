package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.Admin;
import kr.co.moneybridge.model.backoffice.AdminRepository;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyMemberUtil {
    private final UserRepository userRepository;
    private final PBRepository pbRepository;
    private final AdminRepository adminRepository;

    public static Member buildMember(Long id, Role role){
        Member member = null;
        if(role.equals(Role.USER)){
            User user = User.builder().id(id).role(role).build();
            member = user;
        } else if(role.equals(Role.PB)){
            PB pb = PB.builder().id(id).role(role).build();
            member = pb;
        } else if(role.equals(Role.ADMIN)){
            Admin admin = Admin.builder().id(id).role(role).build();
            member = admin;
        }
        return member;
    }

    public Member findByEmailAndStatus(String email, Role role) {
        Member member = null;
        if(role.equals(Role.USER)){
            User userPS = userRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당하는 투자자 계정이 없습니다"));
            member = userPS;
        } else if(role.equals(Role.PB)){
            PB pbPS = pbRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당하는 PB 계정이 없습니다"));
            if(pbPS.getStatus().equals(PBStatus.PENDING)){
                throw new Exception404("아직 승인되지 않은 PB 계정입니다");
            }
            member = pbPS;
        } else if(role.equals(Role.ADMIN)){
            Admin adminPS = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception404("해당하는 관리자 계정이 없습니다"));
            member = adminPS;
        }
        return member;
    }

    public Member findByIdAndStatus(Long id, Role role) {
        Member member = null;
        if(role.equals(Role.USER)){
            User userPS = userRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당하는 투자자 계정이 없습니다"));
            member = userPS;
        } else if(role.equals(Role.PB)){
            PB pbPS = pbRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당하는 PB 계정이 없습니다"));
            if(pbPS.getStatus().equals(PBStatus.PENDING)){
                throw new Exception404("아직 승인되지 않은 PB 계정입니다");
            }
            member = pbPS;
        } else if(role.equals(Role.ADMIN)){
            Admin adminPS = adminRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당하는 관리자 계정이 없습니다"));
            member = adminPS;
        }
        return member;
    }
}
