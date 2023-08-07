package kr.co.moneybridge.service;

import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.MemberUtil;
import kr.co.moneybridge.core.util.MsgUtil;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.FrequentQuestionRepository;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.backoffice.NoticeRepository;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    MsgUtil msgUtil;
    @Mock
    JavaMailSender javaMailSender;
    @Mock
    MemberUtil memberUtil;
    @Mock
    UserRepository userRepository;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    StyleRepository styleRepository;
    @Mock
    BoardRepository boardRepository;
    @Mock
    ReplyRepository replyRepository;
    @Mock
    ReReplyRepository reReplyRepository;
    @Mock
    BoardBookmarkRepository boardBookmarkRepository;

    @Test
    @DisplayName("대댓글 강제 삭제")
    void deleteReReply() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Board board = newMockBoard(1L, "title", pb);
        Reply reply = newMockPBReply(id, board, pb);
        ReReply reReply = newMockPBReReply(1L, reply, pb);

        // when
        backOfficeService.deleteReReply(id);

        // then
        verify(reReplyRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("댓글 강제 삭제")
    void deleteReply() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Board board = newMockBoard(1L, "title", pb);
        Reply reply = newMockPBReply(id, board, pb);
        ReReply reReply = newMockPBReReply(1L, reply, pb);

        // when
        backOfficeService.deleteReply(id);

        // then
        verify(reReplyRepository, times(1)).deleteByReplyId(id);
        verify(replyRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("콘텐츠 강제 삭제")
    void deleteBoard() {
        // given
        Long id = 1L;
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Board board = newMockBoard(id, "title", pb);
        Reply reply = newMockPBReply(1L, board, pb);
        ReReply reReply = newMockPBReReply(1L, reply, pb);
        User user = newMockUser(1L, "user");
        BoardBookmark boardBookmark = newMockBoardBookmark(1L, user, board);

        //stub
        when(replyRepository.findAllByBoardId(id)).thenReturn(Arrays.asList(reply));

        // when
        backOfficeService.deleteBoard(id);

        // then
        verify(boardRepository, times(1)).findThumbnailByBoardId(id);
        verify(boardBookmarkRepository, times(1)).deleteByBoardId(id);
        verify(replyRepository, times(1)).findAllByBoardId(id);
        verify(replyRepository, times(1)).findAllByBoardId(id);
        verify(reReplyRepository, times(1)).deleteByReplyId(any());
        verify(replyRepository, times(1)).deleteByBoardId(id);
        verify(boardRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("회원 관리 페이지 전체 가져오기")
    void getReservations() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        User user = newMockUser(1L, "user");
        Reservation reservation = newMockCallReservation(1L, user, pb, ReservationProcess.COMPLETE);
        Page<Reservation> reservationPG = new PageImpl<>(Arrays.asList(reservation));
        Optional<Review> reviewOP = Optional.of(newMockReview(1L, reservation));

        // stub
        when(reservationRepository.findAll(pageable)).thenReturn(reservationPG);
        when(reviewRepository.findByReservationId(any())).thenReturn(reviewOP);
        when(styleRepository.findAllByReviewId(any())).thenReturn(new ArrayList<>());

        // when
        PageDTO<BackOfficeResponse.ReservationTotalDTO> pageDTO = backOfficeService.getReservations(pageable);

        // then
        assertThat(pageDTO.getList().get(0).getId()).isEqualTo(1L);
        assertThat(pageDTO.getList().get(0).getProcess()).isEqualTo(ReservationProcess.COMPLETE);
        assertThat(pageDTO.getList().get(0).getStatus()).isEqualTo(ReservationStatus.ACTIVE);
        assertThat(pageDTO.getList().get(0).getTime()).isEqualTo(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분")));
        assertThat(pageDTO.getList().get(0).getType()).isEqualTo(ReservationType.CALL);
        assertThat(pageDTO.getList().get(0).getLocationName()).isEqualTo("kb증권 강남중앙점");
        assertThat(pageDTO.getList().get(0).getGoal()).isEqualTo(ReservationGoal.PROFIT);
        assertThat(pageDTO.getList().get(0).getQuestion()).isEqualTo("질문입니다...");
        assertThat(pageDTO.getList().get(0).getUser().getId()).isEqualTo(1L);
        assertThat(pageDTO.getList().get(0).getPb().getId()).isEqualTo(1L);
        assertThat(pageDTO.getList().get(0).getReview().getContent()).isEqualTo("content 입니다");
        assertThat(pageDTO.getList().get(0).getReview().getAdherence()).isEqualTo(ReviewAdherence.EXCELLENT);
        assertThat(pageDTO.getList().get(0).getReview().getStyles()).isEmpty(); // 확인
        assertThat(pageDTO.getTotalElements()).isEqualTo(1);
        assertThat(pageDTO.getTotalPages()).isEqualTo(1);
        assertThat(pageDTO.getCurPage()).isEqualTo(0);
        assertThat(pageDTO.getFirst()).isEqualTo(true);
        assertThat(pageDTO.getLast()).isEqualTo(true);
        assertThat(pageDTO.getEmpty()).isEqualTo(false);
        Mockito.verify(reservationRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(reviewRepository, Mockito.times(1)).findByReservationId(any());
        Mockito.verify(styleRepository, Mockito.times(1)).findAllByReviewId(any());
    }

    @Test
    @DisplayName("해당 투자자 강제 탈퇴")
    void forceWithdraw_user() {
        // given
        Long id = 1L;
        User user = newMockUser(1L, "user");

        // when
        backOfficeService.forceWithdraw(id, Role.USER);

        // then
        verify(memberUtil, times(1)).deleteById(id, Role.USER);
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
        verify(memberUtil, times(1)).deleteById(id, Role.PB);
    }

    @Test
    @DisplayName("해당 투자자를 관리자로 등록 취소")
    void deAuthorizeAdmin() {
        // given
        Long id = 1L;
        User user = newMockUserADMIN(1L, "user");
        Boolean admin = false;

        // stub
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

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
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
//        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        backOfficeService.authorizeAdmin(id, admin);

        // then
        verify(userRepository, times(1)).findById(id);
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("회원(PB) 리스트 가져오기")
    void getPBs() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Company company = newMockCompany(1L, "미래에셋증권");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "pblee", branch);
        Page<BackOfficeResponse.PBOutDTO> pbPG = new PageImpl<>(new ArrayList<>(Arrays.asList(
                new BackOfficeResponse.PBOutDTO(pb, branch)
        )));

        // stub
        when(pbRepository.findPagesByStatus(any(), any())).thenReturn(pbPG);

        // when
        PageDTO<BackOfficeResponse.PBOutDTO> pageDTO = backOfficeService.getPBs(pageable);

        // then
        assertThat(pageDTO.getList().get(0).getId()).isEqualTo(1);
        assertThat(pageDTO.getList().get(0).getEmail()).isEqualTo("pblee@nate.com");
        assertThat(pageDTO.getList().get(0).getName()).isEqualTo("pblee");
        assertThat(pageDTO.getList().get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(pageDTO.getList().get(0).getBranchName()).isEqualTo("미래에셋증권 여의도점");
        assertThat(pageDTO.getTotalElements()).isEqualTo(1);
        assertThat(pageDTO.getTotalPages()).isEqualTo(1);
        assertThat(pageDTO.getCurPage()).isEqualTo(0);
        assertThat(pageDTO.getFirst()).isEqualTo(true);
        assertThat(pageDTO.getLast()).isEqualTo(true);
        assertThat(pageDTO.getEmpty()).isEqualTo(false);
        Mockito.verify(pbRepository, Mockito.times(1)).findPagesByStatus(any(), any());
    }

    @Test
    @DisplayName("회원(투자자) 리스트 가져오기")
    void getUsers() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        User user = newMockUser(1L, "user");
        Page<User> userPG = new PageImpl<>(Arrays.asList(user));
        String type = "user";

        // stub
        when(userRepository.findAll(pageable)).thenReturn(userPG);

        // when
        PageDTO<BackOfficeResponse.UserOutDTO> pageDTO = backOfficeService.getUsers(pageable);

        // then
        assertThat(pageDTO.getList().get(0).getId()).isEqualTo(1);
        assertThat(pageDTO.getList().get(0).getEmail()).isEqualTo("user@nate.com");
        assertThat(pageDTO.getList().get(0).getName()).isEqualTo("user");
        assertThat(pageDTO.getList().get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(pageDTO.getList().get(0).getIsAdmin()).isEqualTo(false);
        assertThat(pageDTO.getTotalElements()).isEqualTo(1);
        assertThat(pageDTO.getTotalPages()).isEqualTo(1);
        assertThat(pageDTO.getCurPage()).isEqualTo(0);
        assertThat(pageDTO.getFirst()).isEqualTo(true);
        assertThat(pageDTO.getLast()).isEqualTo(true);
        assertThat(pageDTO.getEmpty()).isEqualTo(false);
        Mockito.verify(userRepository, Mockito.times(1)).findAll(pageable);
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
        when(msgUtil.createMessage(anyString(), any(), any())).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        backOfficeService.approvePB(id, approve);

        // then
        verify(pbRepository, times(1)).findById(id);
        verify(msgUtil, times(1)).createMessage(any(), any(), any());
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
        verify(memberUtil, times(1)).deleteById(any(), any());
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
        when(msgUtil.createMessage(anyString(), any(), any())).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        backOfficeService.approvePB(id, approve);

        // then
        verify(pbRepository, times(1)).findById(id);
        verify(msgUtil, times(1)).createMessage(any(), any(), any());
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
        PageDTO<BackOfficeResponse.PBPendingDTO> pageDTO = backOfficeService.getPBPending(pageable);

        // then
        assertThat(pageDTO.getList().get(0).getId()).isEqualTo(1L);
        assertThat(pageDTO.getList().get(0).getEmail()).isEqualTo("pblee@nate.com");
        assertThat(pageDTO.getList().get(0).getName()).isEqualTo("pblee");
        assertThat(pageDTO.getList().get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(pageDTO.getList().get(0).getBranchName()).isEqualTo("미래에셋증권 여의도점");
        assertThat(pageDTO.getList().get(0).getCareer()).isEqualTo(10);
        assertThat(pageDTO.getList().get(0).getSpeciality1()).isEqualTo(PBSpeciality.BOND);
        assertThat(pageDTO.getList().get(0).getSpeciality2()).isNull();
        assertThat(pageDTO.getList().get(0).getBusinessCard()).isEqualTo("card.png");
        assertThat(pageDTO.getTotalElements()).isEqualTo(1);
        assertThat(pageDTO.getTotalPages()).isEqualTo(1);
        assertThat(pageDTO.getCurPage()).isEqualTo(0);
        assertThat(pageDTO.getFirst()).isEqualTo(true);
        assertThat(pageDTO.getLast()).isEqualTo(true);
        assertThat(pageDTO.getEmpty()).isEqualTo(false);
        Mockito.verify(pbRepository, Mockito.times(1)).findAllByStatus(any(), any());
    }

    @Test
    @DisplayName("공지사항 목록 가져오기")
    void getNotices() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Notice> noticePG = new PageImpl<>(Arrays.asList(newMockNotice(1L)));

        // stub
        when(noticeRepository.findAll(pageable)).thenReturn(noticePG);

        // when
        PageDTO<BackOfficeResponse.NoticeDTO> noticeDTO = backOfficeService.getNotices(pageable);

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
    void get_notice_test() {
        // given
        Long noticeId = 1L;
        Notice notice = newMockNotice(noticeId);

        // stub
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));

        // when
        BackOfficeResponse.NoticeDTO response = backOfficeService.getNotice(noticeId);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("서버 점검 안내");
        assertThat(response.getContent()).isEqualTo("보다 나은 환경을 제공하기 위해 개발진에서 발견한 문제 복구 및 업데이트 점검을 진행할 예정입니다.\n" +
                "업데이트 점검을 진행하는 동안에는 접속할 수 없으니 불필요한 손해가 발생치 않도록 주의해 주세요.\n" +
                "이로 인해 불편을 끼쳐 드려 죄송합니다.");
        Mockito.verify(noticeRepository, Mockito.times(1)).findById(noticeId);
    }

    @Test
    @DisplayName("자주 묻는 질문 목록 가져오기")
    void getFAQs() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<FrequentQuestion> faqPG = new PageImpl<>(Arrays.asList(newMockFrequentQuestion(1L)));

        // stub
        when(frequentQuestionRepository.findAll(pageable)).thenReturn(faqPG);

        // when
        PageDTO<BackOfficeResponse.FAQDTO> faqDTO = backOfficeService.getFAQs(pageable);

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

    @Test
    void get_faq_test() {
        // given
        Long faqId = 1L;
        FrequentQuestion faq = newMockFrequentQuestion(faqId);

        // stub
        when(frequentQuestionRepository.findById(faqId)).thenReturn(Optional.of(faq));

        // when
        BackOfficeResponse.FAQDTO response = backOfficeService.getFAQ(faqId);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getLabel()).isEqualTo("회원");
        assertThat(response.getTitle()).isEqualTo("이메일이 주소가 변경되었어요.");
        assertThat(response.getContent()).isEqualTo("가입 이메일은 회원 식별 고유 키로 " +
                "가입 후 변경이 불가능 하므로 개인 이메일로 가입하기를 권유드립니다.");
        Mockito.verify(frequentQuestionRepository, Mockito.times(1)).findById(faqId);
    }

//    @Test
//    void update_notice_test() {
//        // given
//        Long noticeId = 1L;
//        Notice notice = newMockNotice(noticeId);
//        BackOfficeRequest.UpdateNoticeDTO updateNoticeDTO = new BackOfficeRequest.UpdateNoticeDTO();
//        updateNoticeDTO.setTitle("제목 수정");
//        updateNoticeDTO.setContent("내용 수정");
//
//        // stub
//        Mockito.when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));
//
//        // when
//        Notice response = backOfficeService.updateNotice(noticeId, updateNoticeDTO);
//
//        // then
//        assertThat(response.getTitle()).isEqualTo("제목 수정");
//        assertThat(response.getContent()).isEqualTo("내용 수정");
//    }

    @Test
    void delete_notice_test() {
        // given
        Long noticeId = 1L;
        Notice notice = newMockNotice(noticeId);

        // stub
        Mockito.when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));
        Mockito.doNothing().when(noticeRepository).deleteById(anyLong());

        // when
        backOfficeService.deleteNotice(noticeId);

        // then
        Mockito.verify(noticeRepository, Mockito.times(1)).deleteById(noticeId);
    }

//    @Test
//    void update_faq_test() {
//        // given
//        Long faqId = 1L;
//        FrequentQuestion faq = newMockFrequentQuestion(faqId);
//        BackOfficeRequest.UpdateFAQDTO updateFAQDTO = new BackOfficeRequest.UpdateFAQDTO();
//        updateFAQDTO.setLabel("라벨 수정");
//        updateFAQDTO.setTitle("제목 수정");
//        updateFAQDTO.setContent("내용 수정");
//
//        // stub
//        Mockito.when(frequentQuestionRepository.findById(anyLong())).thenReturn(Optional.of(faq));
//
//        // when
//        FrequentQuestion response = backOfficeService.updateFAQ(faqId, updateFAQDTO);
//
//        // then
//        assertThat(response.getLabel()).isEqualTo("라벨 수정");
//        assertThat(response.getTitle()).isEqualTo("제목 수정");
//        assertThat(response.getContent()).isEqualTo("내용 수정");
//    }

    @Test
    void delete_faq_test() {
        // given
        Long faqId = 1L;
        FrequentQuestion faq = newMockFrequentQuestion(faqId);

        // stub
        Mockito.when(frequentQuestionRepository.findById(anyLong())).thenReturn(Optional.of(faq));
        Mockito.doNothing().when(frequentQuestionRepository).deleteById(anyLong());

        // when
        backOfficeService.deleteFAQ(faqId);

        // then
        Mockito.verify(frequentQuestionRepository, Mockito.times(1)).deleteById(faqId);
    }
}