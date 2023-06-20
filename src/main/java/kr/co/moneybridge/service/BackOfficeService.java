package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.MyMsgUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.FrequentQuestionRepository;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.backoffice.NoticeRepository;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.pb.PBStatus;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BackOfficeService {
    private final FrequentQuestionRepository frequentQuestionRepository;
    private final NoticeRepository noticeRepository;
    private final PBRepository pbRepository;
    private final MyMemberUtil myMemberUtil;
    private final JavaMailSender javaMailSender;
    private final MyMsgUtil myMsgUtil;
    private final UserRepository userRepository;

    @MyLog
    @Transactional
    public void forceWithdraw(Long memberId, Role role) {
        myMemberUtil.deleteById(memberId, role);
    }

    @MyLog
    @Transactional
    public void authorizeAdmin(Long userId, Boolean admin) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );
        userPS.authorize(admin);
    }

    @MyLog
    public BackOfficeResponse.MemberOutDTO getMembers(Pageable pageable) {
        Page<User> userPG = userRepository.findAll(pageable);
        Page<PB> pbPG = pbRepository.findAllByStatus(PBStatus.ACTIVE, pageable);
        List<BackOfficeResponse.UserDTO> userList = userPG.getContent().stream().map(user ->
                new BackOfficeResponse.UserDTO(user)).collect(Collectors.toList());
        List<BackOfficeResponse.PBDTO> pbList = pbPG.getContent().stream().map(pb ->
                new BackOfficeResponse.PBDTO(pb)).collect(Collectors.toList());
        return new BackOfficeResponse.MemberOutDTO(new BackOfficeResponse.CountDTO(
                userPG.getContent().size() + pbPG.getContent().size(),
                userPG.getContent().size(), pbPG.getContent().size()),
                new PageDTO<>(userList, userPG, User.class),
                new PageDTO<>(pbList, pbPG, PB.class));
    }

    @MyLog
    @Transactional
    public void approvePB(Long pbId, Boolean approve) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );
        if (!pbPS.getStatus().equals(PBStatus.PENDING)) {
            throw new Exception400("pbId", "이미 승인 완료된 PB입니다.");
        }
        String subject = myMsgUtil.getSubjectApprove();
        String msg = myMsgUtil.getMsgApprove();
        if(approve == false){
            myMemberUtil.deleteById(pbId, Role.PB); // 탈퇴와 동일하게 삭제
            subject = myMsgUtil.getSubjectReject();
            msg = myMsgUtil.getMsgReject();
        }
        pbPS.approved();
        // 이메일 알림
        try{
            MimeMessage message = myMsgUtil.createMessage(pbPS.getEmail(), subject, msg);
            javaMailSender.send(message);
        }catch (Exception e){
            throw new Exception500("이메일 알림 전송 실패 " + e.getMessage());
        }
    }

    @MyLog
    public BackOfficeResponse.PBPendingOutDTO getPBPending(Pageable pageable) {
        Page<PB> pbPG = pbRepository.findAllByStatus(PBStatus.PENDING, pageable);
        List<BackOfficeResponse.PBPendingDTO> list = pbPG.getContent().stream().map(pb ->
                new BackOfficeResponse.PBPendingDTO(pb, pb.getBranch().getName())).collect(Collectors.toList());
        return new BackOfficeResponse.PBPendingOutDTO(pbPG.getContent().size(), new PageDTO<>(list, pbPG, PB.class));
    }

    @MyLog
    public PageDTO<BackOfficeResponse.NoticeDTO> getNotice(Pageable pageable) {
        Page<Notice> noticePG = noticeRepository.findAll(pageable);
        List<BackOfficeResponse.NoticeDTO> list = noticePG.getContent().stream().map(notice ->
                new BackOfficeResponse.NoticeDTO(notice)).collect(Collectors.toList());
        return new PageDTO<>(list, noticePG, Notice.class);
    }

    @MyLog
    public PageDTO<BackOfficeResponse.FAQDTO> getFAQ(Pageable pageable) {
        Page<FrequentQuestion> faqPG = frequentQuestionRepository.findAll(pageable);
        List<BackOfficeResponse.FAQDTO> list = faqPG.getContent().stream().map(faq ->
                new BackOfficeResponse.FAQDTO(faq)).collect(Collectors.toList());
        return new PageDTO<>(list, faqPG, FrequentQuestion.class);
    }
}
