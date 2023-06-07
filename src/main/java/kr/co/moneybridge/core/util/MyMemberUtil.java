package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.AdminRepository;
import kr.co.moneybridge.model.pb.PBRepository;
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

    public Member findById(Long id, Role role) {
        Member member = null;
        if(role.equals(Role.ROLE_USER)){
            member = userRepository.findById(id)
                    .orElseThrow(() -> new Exception500("해당하는 사용자가 없습니다"));
        } else if(role.equals(Role.ROLE_PB)){
            member = pbRepository.findById(id)
                    .orElseThrow(() -> new Exception500("해당하는 사용자가 없습니다"));
        } else if(role.equals(Role.ROLE_ADMIN)){
            member = adminRepository.findById(id)
                    .orElseThrow(() -> new Exception500("해당하는 사용자가 없습니다"));
        }
        return member;
    }
}
