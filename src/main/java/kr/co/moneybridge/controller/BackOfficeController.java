package kr.co.moneybridge.controller;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.service.BackOfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BackOfficeController {
    private final BackOfficeService backOfficeService;

    // PB회원 가입 요청 승인 페이지 전체 가져오기
    @MyLog
    @GetMapping("/admin/pbs")
    public ResponseDTO<BackOfficeResponse.PBPendingOutDTO> getPBPending() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        BackOfficeResponse.PBPendingOutDTO pbPendingPageDTO = backOfficeService.getPBPending(pageable);
        return new ResponseDTO<>(pbPendingPageDTO);
    }

    // 공지사항 목록 가져오기
    @MyLog
    @SwaggerResponses.GetNotice
    @GetMapping("/notices")
    public ResponseDTO<PageDTO<BackOfficeResponse.NoticeDTO>> getNotice() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<BackOfficeResponse.NoticeDTO> faqDTO = backOfficeService.getNotice(pageable);
        return new ResponseDTO<>(faqDTO);
    }

    // 자주 묻는 질문 목록 가져오기
    @MyLog
    @SwaggerResponses.GetFAQ
    @GetMapping("/FAQ")
    public ResponseDTO<PageDTO<BackOfficeResponse.FAQDTO>> getFAQ() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<BackOfficeResponse.FAQDTO> faqDTO = backOfficeService.getFAQ(pageable);
        return new ResponseDTO<>(faqDTO);
    }
}
