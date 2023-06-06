package kr.co.moneybridge.service;

import com.nimbusds.jose.util.Pair;
import kr.co.moneybridge.core.annotation.MyErrorLog;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.jwt.MyJwtProvider;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception401;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.dto.user.UserResponse;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserAgreementRepository;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserAgreementRepository userAgreementRepository;

    private final MyJwtProvider myJwtProvider;

    @MyLog
    @MyErrorLog
    @Transactional
    public UserResponse.JoinUserOutDTO 회원가입(UserRequest.JoinUserInDTO joinUserInDTO){
        Optional<User> userOP =userRepository.findByEmail(joinUserInDTO.getEmail());
        if(userOP.isPresent()){
            throw new Exception400("email", "이미 등록된 이메일입니다");
        }
        if(!joinUserInDTO.getPassword().equals(joinUserInDTO.getCheckPassword())){
            throw new Exception400("checkPassword", "비밀번호와 비밀번호 재입력이 다릅니다");
        }
        String encPassword = passwordEncoder.encode(joinUserInDTO.getPassword()); // 60Byte
        joinUserInDTO.setPassword(encPassword);

        try {
            User userPS = userRepository.save(joinUserInDTO.toEntity());
            List<UserRequest.AgreementDTO> agreements = joinUserInDTO.getAgreements();
            if(agreements != null){
                agreements.stream().forEach(agreement ->
                        userAgreementRepository.save(agreement.toEntity(userPS)));
            }
            return new UserResponse.JoinUserOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : "+e.getMessage());
        }
    }

    @MyLog
    @MyErrorLog
    public Pair<String, String> 토큰발급(String email, String password) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            //로그인 성공하면 액세스 토큰, 리프레시 토큰 발급.
            String accessjwt = myJwtProvider.createAccess(myUserDetails.getUser());
            String refreshjwt = myJwtProvider.createRefresh(myUserDetails.getUser());
            return Pair.of(accessjwt, refreshjwt);
        }catch (Exception e){
            throw new Exception500("토큰발급 실패");
        }
    }

    @MyLog
    @MyErrorLog
    public UserResponse.LoginOutDTO 로그인(UserRequest.LoginInDTO loginInDTO) {
        User userPS = userRepository.findByEmail(loginInDTO.getEmail()).orElseThrow(
                () -> new Exception404("사용자가 존재하지 않습니다")
        );
        return new UserResponse.LoginOutDTO(userPS);
    }

    @MyLog
    public UserResponse.DetailOutDTO 회원상세보기(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception404("해당 유저를 찾을 수 없습니다")

        );
        return new UserResponse.DetailOutDTO(userPS);
    }
}
