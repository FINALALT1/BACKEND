package kr.co.moneybridge.core.auth.session;

import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final MyMemberUtil myMemberUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Role role = Role.valueOf(username.split("-")[0]);
        String email = username.substring(role.toString().length() + 1);

        Member member = null;
        try{
            member = myMemberUtil.findByEmail(email, role);
        }catch (Exception e){
            log.error("회원 인증 실패 : " + e.getMessage());
            throw new InternalAuthenticationServiceException("인증 실패: " + e.getMessage());
        }

        return new MyUserDetails(member);
    }
}
