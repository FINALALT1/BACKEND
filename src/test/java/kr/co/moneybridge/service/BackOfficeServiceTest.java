package kr.co.moneybridge.service;

import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.FrequentQuestionRepository;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.backoffice.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BackOfficeServiceTest extends MockDummyEntity {

    @InjectMocks
    BackOfficeService backOfficeService;
    @Mock
    FrequentQuestionRepository frequentQuestionRepository;
    @Mock
    NoticeRepository noticeRepository;

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