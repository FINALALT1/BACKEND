package kr.co.moneybridge.controller;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeRequest;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.dto.user.UserRequest;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.service.BackOfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class BackOfficeController {
    private final BackOfficeService backOfficeService;

    // FAQ 등록
    @MyLog
    @SwaggerResponses.AddFAQ
    @PostMapping("/admin/faq")
    public ResponseDTO addFAQ(@RequestBody @Valid BackOfficeRequest.FAQInDTO faqInDTO, Errors errors) {
        backOfficeService.addFAQ(faqInDTO);
        return new ResponseDTO<>();
    }

    // 공지사항 등록
    @MyLog
    @SwaggerResponses.AddNotice
    @PostMapping("/admin/notice")
    public ResponseDTO addNotice(@RequestBody @Valid BackOfficeRequest.NoticeInDTO noticeInDTO, Errors errors) {
        backOfficeService.addNotice(noticeInDTO);
        return new ResponseDTO<>();
    }

    // 지점 등록
    @MyLog
    @SwaggerResponses.AddBranch
    @PostMapping("/admin/branch")
    public ResponseDTO addBranch(@RequestBody @Valid BackOfficeRequest.BranchInDTO branchInDTO, Errors errors) {
        backOfficeService.addBranch(branchInDTO);
        return new ResponseDTO<>();
    }

    // 대댓글 강제 삭제
    @MyLog
    @SwaggerResponses.DeleteRereply
    @DeleteMapping("/admin/rereply/{id}")
    public ResponseDTO deleteReReply(@PathVariable Long id) {
        backOfficeService.deleteReReply(id);
        return new ResponseDTO<>();
    }

    // 댓글 강제 삭제
    @MyLog
    @SwaggerResponses.DeleteReply
    @DeleteMapping("/admin/reply/{id}")
    public ResponseDTO deleteReply(@PathVariable Long id) {
        backOfficeService.deleteReply(id);
        return new ResponseDTO<>();
    }

    // 콘텐츠 강제 삭제
    @MyLog
    @SwaggerResponses.DeleteBoard
    @DeleteMapping("/admin/board/{id}")
    public ResponseDTO deleteBoard(@PathVariable Long id) {
        backOfficeService.deleteBoard(id);
        return new ResponseDTO<>();
    }

    // 상담 현황 페이지 전체 가져오기
    @MyLog
    @SwaggerResponses.GetReservations
    @GetMapping("/admin/reservations")
    public ResponseDTO<BackOfficeResponse.ReservationOutDTO> getReservations(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "id"));
        BackOfficeResponse.ReservationOutDTO reservationOutDTO = backOfficeService.getReservations(pageable);
        return new ResponseDTO<>(reservationOutDTO);
    }

    // 해당 투자자 강제 탈퇴
    @MyLog
    @SwaggerResponses.ForceWithdrawUser
    @DeleteMapping("/admin/user/{id}")
    public ResponseDTO forceWithdrawUser(@PathVariable Long id) {
        backOfficeService.forceWithdraw(id, Role.USER);
        return new ResponseDTO<>();
    }

    // 해당 PB 강제 탈퇴
    @MyLog
    @SwaggerResponses.ForceWithdrawPB
    @DeleteMapping("/admin/pb/{id}")
    public ResponseDTO forceWithdrawPB(@PathVariable Long id) {
        backOfficeService.forceWithdraw(id, Role.PB);
        return new ResponseDTO<>();
    }

    // 해당 투자자를 관리자로 등록/취소
    @MyLog
    @SwaggerResponses.AuthorizeAdmin
    @PostMapping("/admin/user/{id}")
    public ResponseDTO authorizeAdmin(@PathVariable Long id, @RequestParam Boolean admin) {
        backOfficeService.authorizeAdmin(id, admin);
        return new ResponseDTO<>();
    }

    // 회원 관리 페이지 전체 가져오기
    @MyLog
    @SwaggerResponses.GetMembers
    @GetMapping("/admin/members")
    public ResponseDTO<BackOfficeResponse.MemberOutDTO> getMembers(@RequestParam(defaultValue = "0") int userPage,
                                                                   @RequestParam(defaultValue = "0") int pbPage) {
        Pageable userPageable = PageRequest.of(userPage, 10, Sort.by(Sort.Direction.ASC, "id"));
        Pageable pbPageable = PageRequest.of(pbPage, 10, Sort.by(Sort.Direction.ASC, "id"));
        BackOfficeResponse.MemberOutDTO memberOutDTO = backOfficeService.getMembers(userPageable, pbPageable);
        return new ResponseDTO<>(memberOutDTO);
    }

    // 해당 PB 승인/승인 거부
    @MyLog
    @SwaggerResponses.ApprovePB
    @PostMapping("/admin/pb/{id}")
    public ResponseDTO approvePB(@PathVariable Long id, @RequestParam Boolean approve) {
        backOfficeService.approvePB(id, approve);
        return new ResponseDTO<>();
    }

    // PB 회원 가입 요청 승인 페이지 전체 가져오기
    @MyLog
    @SwaggerResponses.GetPBPending
    @GetMapping("/admin/pbs")
    public ResponseDTO<BackOfficeResponse.PBPendingOutDTO> getPBPending(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "id"));
        BackOfficeResponse.PBPendingOutDTO pbPendingPageDTO = backOfficeService.getPBPending(pageable);
        return new ResponseDTO<>(pbPendingPageDTO);
    }

    // 공지사항 목록 가져오기
    @MyLog
    @SwaggerResponses.GetNotice
    @GetMapping("/notices")
    public ResponseDTO<PageDTO<BackOfficeResponse.NoticeDTO>> getNotice(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<BackOfficeResponse.NoticeDTO> faqDTO = backOfficeService.getNotice(pageable);
        return new ResponseDTO<>(faqDTO);
    }

    // 자주 묻는 질문 목록 가져오기
    @MyLog
    @SwaggerResponses.GetFAQ
    @GetMapping("/FAQ")
    public ResponseDTO<PageDTO<BackOfficeResponse.FAQDTO>> getFAQ(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<BackOfficeResponse.FAQDTO> faqDTO = backOfficeService.getFAQ(pageable);
        return new ResponseDTO<>(faqDTO);
    }
}
