package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyErrorLog;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.pb.PBRequest;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PBService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final BranchRepository branchRepository;
    private final PBRepository pbRepository;
    private final PBAgreementRepository pbAgreementRepository;
    private final UserRepository userRepository;

    @MyLog
    @MyErrorLog
    @Transactional
    public PBResponse.JoinOutDTO joinPB(MultipartFile businessCard, PBRequest.JoinInDTO joinInDTO){
        Optional<PB> pbOP = pbRepository.findByEmail(joinInDTO.getEmail());
        if(pbOP.isPresent()){
            if(pbOP.get().getStatus().equals(PBStatus.INACTIVE)){
                throw new Exception400("email", "탈퇴한 PB 계정입니다"); // 일단 회원가입 못하게 막음. 추후에 어떻게 할지 정하기
            }
            if(pbOP.get().getStatus().equals(PBStatus.PENDING)){
                throw new Exception400("email", "회원가입 후 승인을 기다리고 있는 PB 계정입니다");
            }
            throw new Exception400("email", "이미 PB로 회원가입된 이메일입니다");
        }
        String encPassword = passwordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);

        Branch branchPS = branchRepository.findById(joinInDTO.getBranchId()).orElseThrow(
                () -> new Exception400("branchId", "해당하는 지점이 존재하지 않습니다")
        );
        if (businessCard == null || businessCard.isEmpty()) {
            throw new Exception400("businessCard", "명함 사진이 없습니다");
        }
        // 압축해서 S3에 사진 저장하는 부분 추가 필요함
        String fileName = businessCard.getOriginalFilename();
        try {
            PB pbPS = pbRepository.save(joinInDTO.toEntity(branchPS, fileName));
            List<PBRequest.AgreementDTO> agreements = joinInDTO.getAgreements();
            if(agreements != null){
                agreements.stream().forEach(agreement ->
                        pbAgreementRepository.save(agreement.toEntity(pbPS)));
            }
            return new PBResponse.JoinOutDTO(pbPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }
    }

    //북마크한 pb 목록 가져오기
    public PageDTO<PBResponse.PBPageDTO> getBookmarkPBs(MyUserDetails myUserDetails, Pageable pageable) {

        User user = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 회원입니다."));

        try {
            Page<PBResponse.PBPageDTO> pbPG = pbRepository.findByUserId(user.getId(), pageable);

            for (PBResponse.PBPageDTO pbPageDTO : pbPG) {
                Long pbId = pbPageDTO.getId();
                pbPageDTO.setReserveCount(pbRepository.countReservationsByPbId(pbId));
                pbPageDTO.setReviewCount(pbRepository.countReviewsByPbId(pbId));
            }

            List<PBResponse.PBPageDTO> list = pbPG.getContent().stream().collect(Collectors.toList());
            return new PageDTO<>(list, pbPG);
        } catch (Exception e) {
            throw new Exception500("PB 목록 조회 에러");
        }
    }
}
