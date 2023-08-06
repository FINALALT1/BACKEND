package kr.co.moneybridge.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeRequest;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
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

    // 지점 상세 주소 앞에 쉼표 붙이기 수정용
//    @MyLog
//    @PatchMapping("/fix/branch")
//    public ResponseDTO fixBranch() {
//        backOfficeService.fixBranch();
//        return new ResponseDTO();
//    }

    @MyLog
    @ApiOperation(value = "지점 수정하기")
    @SwaggerResponses.DefaultApiResponses
    @PatchMapping("/admin/branch/{id}")
    public ResponseDTO updateBranch(@PathVariable Long id, @RequestBody @Valid BackOfficeRequest.UpdateBranchDTO updateBranchDTO, Errors errors) {
        if(updateBranchDTO.getSpecificAddress() != null && !updateBranchDTO.getSpecificAddress().isEmpty()
                && (updateBranchDTO.getAddress() == null || (updateBranchDTO.getAddress() != null && updateBranchDTO.getAddress().isEmpty()))){
            throw new Exception400("address", "specificAddress는 address와 함께 입력해야 합니다");
        }
        backOfficeService.updateBranch(id, updateBranchDTO);
        return new ResponseDTO();
    }

    @MyLog
    @ApiOperation(value = "지점 삭제")
    @SwaggerResponses.ApiResponsesWithout400
    @DeleteMapping("/admin/branch/{id}")
    public ResponseDTO deleteBranch(@PathVariable Long id) {
        backOfficeService.deleteBranch(id);

        return new ResponseDTO();
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

    // 상담 내역의 각 건수 및 승인 대기 중인 PB 수 가져오기
    @MyLog
    @SwaggerResponses.GetReservationsCount
    @GetMapping("/admin/reservations/count")
    public ResponseDTO<BackOfficeResponse.ReservationTotalCountDTO> getReservationsCount() {
        BackOfficeResponse.ReservationTotalCountDTO reservationTotalCountDTO = backOfficeService.getReservationsCount();
        return new ResponseDTO<>(reservationTotalCountDTO);
    }

    // 상담 내역 리스트 가져오기
    @MyLog
    @SwaggerResponses.GetReservations
    @GetMapping("/admin/reservations")
    public ResponseDTO<PageDTO<BackOfficeResponse.ReservationTotalDTO>> getReservations(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BackOfficeResponse.ReservationTotalDTO> pageDTO = backOfficeService.getReservations(pageable);
        return new ResponseDTO<>(pageDTO);
    }

    @MyLog
    @ApiOperation(value = "상담 삭제하기")
    @SwaggerResponses.ApiResponsesWithout400
    @DeleteMapping("/admin/reservation/{id}")
    public ResponseDTO deleteReservation(@PathVariable Long id) {
        backOfficeService.deleteReservation(id);

        return new ResponseDTO<>();
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

    // 회원수 가져오기
    @MyLog
    @SwaggerResponses.GetMembersCount
    @GetMapping("/admin/members/count")
    public ResponseDTO<BackOfficeResponse.CountDTO> getMembersCount() {
        BackOfficeResponse.CountDTO countDTO = backOfficeService.getMembersCount();
        return new ResponseDTO<>(countDTO);
    }

    // 투자자 리스트 가져오기
    @MyLog
    @SwaggerResponses.GetUsers
    @ApiImplicitParam(name = "page", example = "0", value = "현재 페이지 번호")
    @GetMapping("/admin/users")
    public ResponseDTO<PageDTO<BackOfficeResponse.UserOutDTO>> getUsers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BackOfficeResponse.UserOutDTO> pageDTO = backOfficeService.getUsers(pageable);
        return new ResponseDTO<>(pageDTO);
    }

    @MyLog
    @SwaggerResponses.GetPBs
    @ApiImplicitParam(name = "page", example = "0", value = "현재 페이지 번호")
    @GetMapping("/admin/pbs")
    public ResponseDTO<PageDTO<BackOfficeResponse.PBOutDTO>> getPBs(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BackOfficeResponse.PBOutDTO> pageDTO = backOfficeService.getPBs(pageable);
        return new ResponseDTO<>(pageDTO);
    }

    // 해당 PB 승인/승인 거부
    @MyLog
    @SwaggerResponses.ApprovePB
    @PostMapping("/admin/pb/{id}")
    public ResponseDTO approvePB(@PathVariable Long id, @RequestParam Boolean approve) {
        backOfficeService.approvePB(id, approve);
        return new ResponseDTO<>();
    }

    // PB 회원 가입 승인 대기 리스트 가져오기
    @MyLog
    @SwaggerResponses.GetPBPending
    @GetMapping("/admin/pendings")
    public ResponseDTO<PageDTO<BackOfficeResponse.PBPendingDTO>> getPBPending(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BackOfficeResponse.PBPendingDTO> pageDTO = backOfficeService.getPBPending(pageable);
        return new ResponseDTO<>(pageDTO);
    }

    // 공지사항 목록 가져오기
    @MyLog
    @SwaggerResponses.GetNotice
    @GetMapping("/notices")
    public ResponseDTO<PageDTO<BackOfficeResponse.NoticeDTO>> getNotices(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BackOfficeResponse.NoticeDTO> noticesDTO = backOfficeService.getNotices(pageable);
        return new ResponseDTO<>(noticesDTO);
    }

    @MyLog
    @ApiOperation(value = "공지사항 상세 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/notice/{id}")
    public ResponseDTO<BackOfficeResponse.NoticeDTO> getNotice(@PathVariable Long id) {
        BackOfficeResponse.NoticeDTO noticeDTO = backOfficeService.getNotice(id);

        return new ResponseDTO<>(noticeDTO);
    }

    // 공지사항 등록
    @MyLog
    @SwaggerResponses.AddNotice
    @PostMapping("/admin/notice")
    public ResponseDTO addNotice(@RequestBody @Valid BackOfficeRequest.AddNoticeDTO addNoticeDTO, Errors errors) {
        backOfficeService.addNotice(addNoticeDTO);
        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "공지사항 수정하기")
    @SwaggerResponses.DefaultApiResponses
    @PatchMapping("/admin/notice/{id}")
    public ResponseDTO updateNotice(@PathVariable Long id, @RequestBody @Valid BackOfficeRequest.UpdateNoticeDTO updateNoticeDTO, Errors errors) {
        backOfficeService.updateNotice(id, updateNoticeDTO);

        return new ResponseDTO();
    }

    @MyLog
    @ApiOperation(value = "공지사항 삭제하기")
    @SwaggerResponses.ApiResponsesWithout400
    @DeleteMapping("/admin/notice/{id}")
    public ResponseDTO deleteNotice(@PathVariable Long id) {
        backOfficeService.deleteNotice(id);

        return new ResponseDTO();
    }

    // FAQ 목록 가져오기
    @MyLog
    @SwaggerResponses.GetFAQs
    @GetMapping("/faqs")
    public ResponseDTO<PageDTO<BackOfficeResponse.FAQDTO>> getFAQs(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<BackOfficeResponse.FAQDTO> faqsDTO = backOfficeService.getFAQs(pageable);
        return new ResponseDTO<>(faqsDTO);
    }

    @MyLog
    @ApiOperation(value = "FAQ 상세 조회")
    @SwaggerResponses.ApiResponsesWithout400
    @GetMapping("/faq/{id}")
    public ResponseDTO<BackOfficeResponse.FAQDTO> getFAQ(@PathVariable Long id) {
        BackOfficeResponse.FAQDTO faqDTO = backOfficeService.getFAQ(id);

        return new ResponseDTO<>(faqDTO);
    }

    // FAQ 등록
    @MyLog
    @SwaggerResponses.AddFAQ
    @PostMapping("/admin/faq")
    public ResponseDTO addFAQ(@RequestBody @Valid BackOfficeRequest.AddFAQDTO addFAQDTO, Errors errors) {
        backOfficeService.addFAQ(addFAQDTO);
        return new ResponseDTO<>();
    }

    @MyLog
    @ApiOperation(value = "FAQ 수정하기")
    @SwaggerResponses.DefaultApiResponses
    @PatchMapping("/admin/faq/{id}")
    public ResponseDTO updateFAQ(@PathVariable Long id, @RequestBody @Valid BackOfficeRequest.UpdateFAQDTO updateFAQDTO, Errors errors) {
        backOfficeService.updateFAQ(id, updateFAQDTO);

        return new ResponseDTO();
    }

    @MyLog
    @ApiOperation(value = "FAQ 삭제하기")
    @SwaggerResponses.ApiResponsesWithout400
    @DeleteMapping("/admin/faq/{id}")
    public ResponseDTO deleteFAQ(@PathVariable Long id) {
        backOfficeService.deleteFAQ(id);

        return new ResponseDTO();
    }
}
