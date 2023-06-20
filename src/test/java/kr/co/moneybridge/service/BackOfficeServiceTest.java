package kr.co.moneybridge.service;

import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MyMemberUtil;
import kr.co.moneybridge.core.util.MyMsgUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.FrequentQuestionRepository;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.backoffice.NoticeRepository;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BackOfficeServiceTest extends MockDummyEntity {

    @InjectMocks
    BackOfficeService backOfficeService;
    @Mock
    FrequentQuestionRepository frequentQuestionRepository;
    @Mock
    NoticeRepository noticeRepository;
    @Mock
    PBRepository pbRepository;
    @Mock
    MyMsgUtil myMsgUtil;
    @Mock
    JavaMailSender javaMailSender;
    @Mock
    MyMemberUtil myMemberUtil;
    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("해당 투자자 강제 탈퇴")
    void forceWithdraw_user() {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "user");

        // when
        backOfficeService.forceWithdraw(id, Role.USER);

        // then
        verify(myMemberUtil, times(1)).deleteById(id, Role.USER);
    }

    @Test
    @DisplayName("해당 PB 강제 탈퇴")
    void forceWithdraw_pb() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);

        // when
        backOfficeService.forceWithdraw(id, Role.PB);

        // then
        verify(myMemberUtil, times(1)).deleteById(id, Role.PB);
    }

    @Test
    @DisplayName("해당 투자자를 관리자로 등록 취소")
    void deAuthorizeAdmin() {
        // given
        Long id = 1L;
        User user = newMockUserADMIN(1L, "user");
        Boolean admin = false;

        // stub
        when(userRepository.findById( any())).thenReturn(Optional.of(user));

        // when
        backOfficeService.authorizeAdmin(id, admin);

        // then
        verify(userRepository, times(1)).findById(id);
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("해당 투자자를 관리자로 등록")
    void authorizeAdmin() {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "user");
        Boolean admin = true;

        // stub
        when(userRepository.findById( any())).thenReturn(Optional.of(user));
//        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        backOfficeService.authorizeAdmin(id, admin);

        // then
        verify(userRepository, times(1)).findById(id);
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("회원 관리 페이지 전체 가져오기")
    void getMembers() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Page<PB> pbPG = new PageImpl<>(Arrays.asList(pb));
        User user = newMockUser(1L, "user");
        Page<User> userPG = new PageImpl<>(Arrays.asList(user));

        // stub
        when(userRepository.findAll(pageable)).thenReturn(userPG);
        when(pbRepository.findAllByStatus(any(), any())).thenReturn(pbPG);

        // when
        BackOfficeResponse.MemberOutDTO memberOutDTO = backOfficeService.getMembers(pageable);

        // then
        assertThat(memberOutDTO.getMemberCount().getTotal()).isEqualTo(2);
        assertThat(memberOutDTO.getMemberCount().getUser()).isEqualTo(1);
        assertThat(memberOutDTO.getUserPage().getList().get(0).getId()).isEqualTo(1);
        assertThat(memberOutDTO.getUserPage().getList().get(0).getEmail()).isEqualTo("user@nate.com");
        assertThat(memberOutDTO.getUserPage().getList().get(0).getName()).isEqualTo("user");
        assertThat(memberOutDTO.getUserPage().getList().get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(memberOutDTO.getUserPage().getList().get(0).getIsAdmin()).isEqualTo(false);
        assertThat(memberOutDTO.getUserPage().getTotalElements()).isEqualTo(1);
        assertThat(memberOutDTO.getUserPage().getTotalPages()).isEqualTo(1);
        assertThat(memberOutDTO.getUserPage().getCurPage()).isEqualTo(0);
        assertThat(memberOutDTO.getUserPage().getFirst()).isEqualTo(true);
        assertThat(memberOutDTO.getUserPage().getLast()).isEqualTo(true);
        assertThat(memberOutDTO.getUserPage().getEmpty()).isEqualTo(false);
        assertThat(memberOutDTO.getPbPage().getList().get(0).getId()).isEqualTo(1);
        assertThat(memberOutDTO.getPbPage().getList().get(0).getEmail()).isEqualTo("pblee@nate.com");
        assertThat(memberOutDTO.getPbPage().getList().get(0).getName()).isEqualTo("pblee");
        assertThat(memberOutDTO.getPbPage().getList().get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(memberOutDTO.getPbPage().getTotalElements()).isEqualTo(1);
        assertThat(memberOutDTO.getPbPage().getTotalPages()).isEqualTo(1);
        assertThat(memberOutDTO.getPbPage().getCurPage()).isEqualTo(0);
        assertThat(memberOutDTO.getPbPage().getFirst()).isEqualTo(true);
        assertThat(memberOutDTO.getPbPage().getLast()).isEqualTo(true);
        assertThat(memberOutDTO.getPbPage().getEmpty()).isEqualTo(false);
        Mockito.verify(userRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(pbRepository, Mockito.times(1)).findAllByStatus(any(), any());
    }

    @Test
    @DisplayName("해당 PB 승인 거부")
    void approve_no_PB() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPBWithStatus(id, "pblee", branch, PBStatus.PENDING);
        Boolean approve = false;

        // stub
        when(pbRepository.findById(any())).thenReturn(Optional.of(pb));
        when(myMsgUtil.createMessage(anyString(), any(), any())).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        backOfficeService.approvePB(id, approve);

        // then
        verify(pbRepository, times(1)).findById(id);
        verify(myMsgUtil, times(1)).createMessage(any(), any(), any());
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        verify(myMemberUtil, times(1)).deleteById(any(), any());
    }

    @Test
    @DisplayName("해당 PB 승인")
    void approvePB() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPBWithStatus(id, "pblee", branch, PBStatus.PENDING);
        Boolean approve = true;

        // stub
        when(pbRepository.findById(any())).thenReturn(Optional.of(pb));
        when(myMsgUtil.createMessage(anyString(), any(), any())).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        backOfficeService.approvePB(id, approve);

        // then
        verify(pbRepository, times(1)).findById(id);
        verify(myMsgUtil, times(1)).createMessage(any(), any(), any());
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        assertThat(pb.getStatus()).isEqualTo(PBStatus.ACTIVE);
    }

    @Test
    @DisplayName("PB 회원 가입 요청 승인 페이지 전체 가져오기")
    void getPBPending() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Page<PB> pbPG = new PageImpl<>(Arrays.asList(pb));

        // stub
        when(pbRepository.findAllByStatus(any(), any())).thenReturn(pbPG);

        // when
        BackOfficeResponse.PBPendingOutDTO pbPendingPageDTO = backOfficeService.getPBPending(pageable);

        // then
        assertThat(pbPendingPageDTO.getCount()).isEqualTo(1);
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getId()).isEqualTo(1L);
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getEmail()).isEqualTo("pblee@nate.com");
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getName()).isEqualTo("pblee");
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getBranchName()).isEqualTo("미래에셋증권 여의도점");
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getCareer()).isEqualTo(10);
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getSpeciality1()).isEqualTo(PBSpeciality.BOND);
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getSpeciality2()).isNull();
        assertThat(pbPendingPageDTO.getPage().getList().get(0).getBusinessCard()).isEqualTo("card.png");
        assertThat(pbPendingPageDTO.getPage().getTotalElements()).isEqualTo(1);
        assertThat(pbPendingPageDTO.getPage().getTotalPages()).isEqualTo(1);
        assertThat(pbPendingPageDTO.getPage().getCurPage()).isEqualTo(0);
        assertThat(pbPendingPageDTO.getPage().getFirst()).isEqualTo(true);
        assertThat(pbPendingPageDTO.getPage().getLast()).isEqualTo(true);
        assertThat(pbPendingPageDTO.getPage().getEmpty()).isEqualTo(false);
        Mockito.verify(pbRepository, Mockito.times(1)).findAllByStatus(any(), any());
    }

    @Test
    @DisplayName("공지사항 목록 가져오기")
    void getNotice() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Notice> noticePG = new PageImpl<>(Arrays.asList(newMockNotice(1L)));

        // stub
        when(noticeRepository.findAll(pageable)).thenReturn(noticePG);

        // when
        PageDTO<BackOfficeResponse.NoticeDTO> noticeDTO = backOfficeService.getNotice(pageable);

        // then
        assertThat(noticeDTO.getList().get(0).getId()).isEqualTo(1L);
        assertThat(noticeDTO.getList().get(0).getTitle()).isEqualTo("서버 점검 안내");
        assertThat(noticeDTO.getList().get(0).getContent()).isEqualTo("보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                "이로 인해 불편을 끼쳐 드려 죄송합니다.");
        assertThat(noticeDTO.getList().get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(noticeDTO.getTotalElements()).isEqualTo(1);
        assertThat(noticeDTO.getTotalPages()).isEqualTo(1);
        assertThat(noticeDTO.getCurPage()).isEqualTo(0);
        assertThat(noticeDTO.getFirst()).isEqualTo(true);
        assertThat(noticeDTO.getLast()).isEqualTo(true);
        assertThat(noticeDTO.getEmpty()).isEqualTo(false);
        Mockito.verify(noticeRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("자주 묻는 질문 목록 가져오기")
    void getFAQ() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<FrequentQuestion> faqPG = new PageImpl<>(Arrays.asList(newMockFrequentQuestion(1L)));

        // stub
        when(frequentQuestionRepository.findAll(pageable)).thenReturn(faqPG);

        // when
        PageDTO<BackOfficeResponse.FAQDTO> faqDTO = backOfficeService.getFAQ(pageable);

        // then
        assertThat(faqDTO.getList().get(0).getId()).isEqualTo(1L);
        assertThat(faqDTO.getList().get(0).getLabel()).isEqualTo("회원");
        assertThat(faqDTO.getList().get(0).getTitle()).isEqualTo("이메일이 주소가 변경되었어요.");
        assertThat(faqDTO.getList().get(0).getContent()).isEqualTo("가입 이메일은 회원 식별 고유 키로 " +
                "가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다.");
        assertThat(faqDTO.getTotalElements()).isEqualTo(1);
        assertThat(faqDTO.getTotalPages()).isEqualTo(1);
        assertThat(faqDTO.getCurPage()).isEqualTo(0);
        assertThat(faqDTO.getFirst()).isEqualTo(true);
        assertThat(faqDTO.getLast()).isEqualTo(true);
        assertThat(faqDTO.getEmpty()).isEqualTo(false);
        Mockito.verify(frequentQuestionRepository, Mockito.times(1)).findAll(pageable);
    }
}