package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.Log;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.*;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.backOffice.BackOfficeRequest;
import kr.co.moneybridge.dto.backOffice.BackOfficeResponse;
import kr.co.moneybridge.dto.backOffice.FullAddress;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.backoffice.FrequentQuestion;
import kr.co.moneybridge.model.backoffice.FrequentQuestionRepository;
import kr.co.moneybridge.model.backoffice.Notice;
import kr.co.moneybridge.model.backoffice.NoticeRepository;
import kr.co.moneybridge.model.board.BoardBookmarkRepository;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.board.ReReplyRepository;
import kr.co.moneybridge.model.board.ReplyRepository;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.reservation.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
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
public class BackOfficeService {
    private final FrequentQuestionRepository frequentQuestionRepository;
    private final NoticeRepository noticeRepository;
    private final PBRepository pbRepository;
    private final MemberUtil memberUtil;
    private final JavaMailSender javaMailSender;
    private final MsgUtil msgUtil;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final StyleRepository styleRepository;
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final ReplyRepository replyRepository;
    private final ReReplyRepository reReplyRepository;
    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final S3Util s3Util;
    private final GeoCodingUtil geoCodingUtil;
    private final StibeeUtil stibeeUtil;

//    @MyLog
//    @Transactional
//    public void fixBranch(){
//        List<Branch> list = branchRepository.findAll();
//        list.stream().forEach(branch -> {
//            String originRoadAddress = branch.getRoadAddress();
//            FullAddress address = geoCodingUtil.getFullAddress(originRoadAddress);
//            String specificAddress = originRoadAddress.substring(address.getRoadAddress().length()).trim();
//            if(!specificAddress.isEmpty()) {
//                branch.updateRoadAddress(address.getRoadAddress() + ", " + specificAddress);
//                branch.updateStreetAddress(address.getStreetAddress() + ", " + specificAddress);
//            }
//        });
//    }

    @Log
    @Transactional
    public void addCompany(MultipartFile logo, BackOfficeRequest.CompanyInDTO companyInDTO) {
        // S3에 로고 이미지 저장
        String path = s3Util.upload(logo, "company-logo");
        try {
            companyRepository.save(companyInDTO.toEntity(path));
        } catch (Exception e) {
            throw new Exception500("증권사 등록 실패: " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void updateCompany(Long id, MultipartFile logo, String companyName) {
        Company companyPS = companyRepository.findById(id).orElseThrow(
                () -> new Exception404("존재하지 않는 증권사입니다.")
        );

        // 로고 이미지 수정
        if (logo != null && !logo.isEmpty()) {
            String path = s3Util.upload(logo, "company-logo");
            if (companyPS.getLogo() != null && !companyPS.getLogo().isBlank()) {
                s3Util.delete(companyPS.getLogo()); // s3에서 기존 로고 이미지 삭제
            }
            companyPS.updateLogo(path);
        }

        if (companyName != null && !companyName.isBlank()) {
            companyPS.updateName(companyName);
        }
    }

    @Log
    @Transactional
    public void deleteCompany(Long id) {
        companyRepository.findById(id).orElseThrow(
                () -> new Exception404("존재하지 않는 증권사입니다.")
        );

        try {
            branchRepository.deleteByCompanyId(id);
            companyRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception500("증권사 또는 소속 지점 삭제 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void addBranch(BackOfficeRequest.BranchInDTO branchInDTO) {
        Company company = companyRepository.findById(branchInDTO.getCompanyId()).orElseThrow(
                () -> new Exception400("companyId", "없는 증권회사의 id입니다")
        );

        try {
            // 온라인으로만 운영되는 증권사의 경우
            if (branchInDTO.getAddress() == null || branchInDTO.getAddress().isEmpty()) {
                branchRepository.save(branchInDTO.toDefaultEntity(company));
                return;
            }
            FullAddress address = geoCodingUtil.getFullAddress(branchInDTO.getAddress());
            branchRepository.save(branchInDTO.toEntity(company, address));
        } catch (Exception e) {
            throw new Exception500("지점 저장 실패 : " + e);
        }
    }

    @Log
    @Transactional
    public void updateBranch(Long branchId, BackOfficeRequest.UpdateBranchDTO updateBranchDTO) {
        Branch branchPS = branchRepository.findById(branchId).orElseThrow(
                () -> new Exception404("존재하지 않는 지점입니다.")
        );
        try {
            // 증권사랑 이름 변경
            if (updateBranchDTO.getCompanyId() != null) {
                Company company = companyRepository.findById(updateBranchDTO.getCompanyId()).orElseThrow(
                        () -> new Exception400("companyId", "없는 증권회사의 id입니다")
                );
                branchPS.updateCompany(company);
                if (updateBranchDTO.getName() == null ||
                        (updateBranchDTO.getName() != null && updateBranchDTO.getName().isEmpty())) {
                    branchPS.updateNameOfCompany(company.getName());
                } else {
                    branchPS.updateName(company.getName() + " " + updateBranchDTO.getName());
                }
            } else if (updateBranchDTO.getName() != null && !updateBranchDTO.getName().isEmpty()) {
                branchPS.updateNameOnly(updateBranchDTO.getName());
            }

            // 주소 변경
            if (updateBranchDTO.getAddress() != null && !updateBranchDTO.getAddress().isEmpty()) {
                FullAddress address = geoCodingUtil.getFullAddress(updateBranchDTO.getAddress());
                String specificAddress = updateBranchDTO.getSpecificAddress() == null ||
                        (updateBranchDTO.getSpecificAddress() != null && updateBranchDTO.getSpecificAddress().isEmpty()) ?
                        "" : updateBranchDTO.getSpecificAddress();

                branchPS.updateAddress(address, specificAddress);
            }

        } catch (Exception e) {
            throw new Exception500("지점 수정 실패 : " + e);
        }
    }

    @Log
    @Transactional
    public void deleteBranch(Long branchId) {
        int pbCount = pbRepository.countByBranchId(branchId);
        if (pbCount >= 1) {
            throw new Exception400("branchId", "삭제하려는 지점에 소속된 PB가 1명 이상 존재합니다.");
        }

        try {
            branchRepository.deleteById(branchId);
        } catch (Exception e) {
            throw new Exception500("공지사항 삭제 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void deleteReReply(Long id) {
        // reReply 삭제
        reReplyRepository.deleteById(id);
    }

    @Log
    @Transactional
    public void deleteReply(Long id) {
        // reply를 연관관계로 가지고 있는 reReply도 삭제
        reReplyRepository.deleteByReplyId(id);

        // reply 삭제
        replyRepository.deleteById(id);
    }

    @Log
    @Transactional
    public void deleteBoard(Long id) {
        // s3에서 컨텐츠 썸네일도 삭제
        deleteThumbnail(boardRepository.findThumbnailByBoardId(id));

        // board를 연관관계로 가지고 있는 boardBookmark삭제
        boardBookmarkRepository.deleteByBoardId(id);

        replyRepository.findAllByBoardId(id).stream().forEach(reply -> {
            // reply를 지우니, reply를 연관관계로 가지고 있는 reReply도 삭제
            reReplyRepository.deleteByReplyId(reply.getId());
        });
        // board를 연관관계로 가지고 있는 reply삭제
        replyRepository.deleteByBoardId(id);

        // board 삭제
        boardRepository.deleteById(id);
    }

    private void deleteThumbnail(Optional<String> thumbnail) {
        if (thumbnail.isPresent()) {
            s3Util.delete(thumbnail.get());
        }
    }

    @Log
    public BackOfficeResponse.ReservationTotalCountDTO getReservationsCount() {
        return new BackOfficeResponse.ReservationTotalCountDTO(
                reservationRepository.countByProcess(ReservationProcess.APPLY),
                reservationRepository.countByProcess(ReservationProcess.CONFIRM),
                reservationRepository.countByProcess(ReservationProcess.COMPLETE),
                reviewRepository.count(),
                pbRepository.countByStatus(PBStatus.PENDING));
    }

    @Log
    @Transactional
    public PageDTO<BackOfficeResponse.ReservationTotalDTO> getReservations(Pageable pageable) {
        Page<Reservation> reservationPG = reservationRepository.findAll(pageable);
        List<BackOfficeResponse.ReservationTotalDTO> list = reservationPG.getContent().stream().map(
                reservation -> {
                    BackOfficeResponse.ReviewTotalDTO reviewTotalDTO = null;
                    Optional<Review> reviewOP = reviewRepository.findByReservationId(reservation.getId());
                    if (!reviewOP.isEmpty()) {
                        reviewTotalDTO =
                                new BackOfficeResponse.ReviewTotalDTO(reviewOP.get(), styleRepository
                                        .findAllByReviewId(reviewOP.get().getId()).stream().map(style ->
                                                new ReservationResponse.StyleDTO(style.getStyle()))
                                        .collect(Collectors.toList()));
                    }
                    return new BackOfficeResponse.ReservationTotalDTO(reservation,
                            new BackOfficeResponse.UserDTO(reservation.getUser()),
                            new BackOfficeResponse.PBDTO(reservation.getPb()),
                            reviewTotalDTO);
                }).collect(Collectors.toList());
        return new PageDTO<>(list, reservationPG, Reservation.class);
    }

    @Log
    @Transactional
    public void forceWithdraw(Long memberId, Role role) {
        try {
            memberUtil.deleteById(memberId, role);
        } catch (Exception e) {

        }
    }

    @Log
    @Transactional
    public void authorizeAdmin(Long userId, Boolean admin) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception404("존재하지 않는 투자자입니다.")
        );
        userPS.authorize(admin);
    }

    @Log
    public BackOfficeResponse.CountDTO getMembersCount() {
        return new BackOfficeResponse.CountDTO(
                userRepository.count(), pbRepository.countByStatus(PBStatus.ACTIVE));
    }

    @Log
    public PageDTO<BackOfficeResponse.UserOutDTO> getUsers(String type, String keyword, Pageable pageable) {
        Page<User> userPG = null;
        if (!keyword.equals("")) {
            if (type.equals("email")) {
                userPG = userRepository.findAllByEmail(pageable, keyword);
            } else if (type.equals("phoneNumber")) {
                userPG = userRepository.findAllByPhoneNumber(pageable, keyword);
            } else if (type.equals("name")) {
                userPG = userRepository.findAllByName(pageable, keyword);
            }
        } else {
            userPG = userRepository.findAll(pageable);
        }

        List<BackOfficeResponse.UserOutDTO> list =
                userPG.getContent()
                        .stream()
                        .map(user -> new BackOfficeResponse.UserOutDTO(user))
                        .collect(Collectors.toList());
        return new PageDTO<>(list, userPG, User.class);
    }

    @Log
    public PageDTO<BackOfficeResponse.PBOutDTO> getPBs(String type, String keyword, Pageable pageable) {
        Page<BackOfficeResponse.PBOutDTO> pbOutPG = null;
        if (!keyword.equals("")) {
            if (type.equals("email")) {
                pbOutPG = pbRepository.findPagesByStatusAndEmail(keyword, PBStatus.ACTIVE, pageable);
            } else if (type.equals("phoneNumber")) {
                pbOutPG = pbRepository.findPagesByStatusAndPhoneNumber(keyword, PBStatus.ACTIVE, pageable);
            } else if (type.equals("name")) {
                pbOutPG = pbRepository.findPagesByStatusAndName(keyword, PBStatus.ACTIVE, pageable);
            }
        } else {
            pbOutPG = pbRepository.findPagesByStatus(PBStatus.ACTIVE, pageable);
        }

        List<BackOfficeResponse.PBOutDTO> list =
                pbOutPG.getContent()
                        .stream()
                        .collect(Collectors.toList());
        return new PageDTO<>(list, pbOutPG);
    }

    @Log
    @Transactional
    public void approvePB(Long pbId, BackOfficeRequest.ApproveDTO approveDTO) {
        PB pbPS = pbRepository.findById(pbId).orElseThrow(
                () -> new Exception404("존재하지 않는 PB입니다.")
        );
        if (!pbPS.getStatus().equals(PBStatus.PENDING)) {
            throw new Exception400("pbId", "이미 승인 완료된 PB입니다.");
        }
//        String subject = msgUtil.getSubjectApprove();
//        String msg = msgUtil.getMsgApprove();
        // 승인 거절
        if (!approveDTO.getApprove()) {
            // 승인 거절 안내 이메일 발송
            stibeeUtil.sendJoinRejectEmail(
                    pbPS.getEmail(),
                    approveDTO.getMsg() == null || approveDTO.getMsg().isBlank()
                            ? "자세한 승인 거절 사유에 대해서는 Money Bridge 측으로 문의해주세요."
                            : approveDTO.getMsg()
            );
            // 탈퇴와 동일하게 삭제
            memberUtil.deleteById(pbId, Role.PB);
//            subject = msgUtil.getSubjectReject();
//            msg = msgUtil.getMsgReject();
        } else { // 승인 완료
            stibeeUtil.subscribe(Role.PB.name(), pbPS.getEmail(), pbPS.getName());
            stibeeUtil.sendJoinApproveEmail(pbPS.getEmail());
            pbPS.approved();
        }
//        // 이메일 알림
//        try {
//            MimeMessage message = msgUtil.createMessage(pbPS.getEmail(), subject, msg);
//            javaMailSender.send(message);
//        } catch (Exception e) {
//            throw new Exception500("이메일 알림 전송 실패 " + e.getMessage());
//        }
    }

    @Log
    public PageDTO<BackOfficeResponse.PBPendingDTO> getPBPending(Pageable pageable) {
        Page<PB> pbPG = pbRepository.findAllByStatus(PBStatus.PENDING, pageable);
        List<BackOfficeResponse.PBPendingDTO> list = pbPG.getContent().stream().map(pb ->
                        new BackOfficeResponse.PBPendingDTO(pb, pb.getBranch().getName()))
                .collect(Collectors.toList());
        return new PageDTO<>(list, pbPG, PB.class);
    }


    @Log
    public PageDTO<BackOfficeResponse.NoticeDTO> getNotices(Pageable pageable) {
        Page<Notice> noticePG = noticeRepository.findAll(pageable);
        List<BackOfficeResponse.NoticeDTO> list = noticePG.getContent().stream().map(notice ->
                new BackOfficeResponse.NoticeDTO(notice)).collect(Collectors.toList());
        return new PageDTO<>(list, noticePG, Notice.class);
    }

    @Log
    public BackOfficeResponse.NoticeDTO getNotice(Long noticeId) {
        Notice noticePS = noticeRepository.findById(noticeId).orElseThrow(
                () -> new Exception404("존재하지 않는 공지사항입니다.")
        );

        return new BackOfficeResponse.NoticeDTO(noticePS);
    }

    @Log
    @Transactional
    public void addNotice(BackOfficeRequest.AddNoticeDTO addNoticeDTO) {
        try {
            noticeRepository.save(addNoticeDTO.toEntity());
        } catch (Exception e) {
            throw new Exception500("공지사항 저장 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void updateNotice(Long noticeId, BackOfficeRequest.UpdateNoticeDTO updateNoticeDTO) {
        Notice noticePS = noticeRepository.findById(noticeId).orElseThrow(
                () -> new Exception404("존재하지 않는 공지사항입니다.")
        );

        try {
            noticePS.updateTitle(updateNoticeDTO.getTitle());
            noticePS.updateContent(updateNoticeDTO.getContent());
        } catch (Exception e) {
            throw new Exception500("공지사항 수정 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeRepository.findById(noticeId).orElseThrow(
                () -> new Exception404("존재하지 않는 공지사항입니다.")
        );

        try {
            noticeRepository.deleteById(noticeId);
        } catch (Exception e) {
            throw new Exception500("공지사항 삭제 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.findById(id).orElseThrow(
                () -> new Exception404("존재하지 않는 상담입니다.")
        );

        try {
            reservationRepository.deleteById(id);

            Optional<Review> reviewOP = reviewRepository.findByReservationId(id);
            if (reviewOP.isPresent()) {
                reviewRepository.deleteById(reviewOP.get().getId());
                styleRepository.deleteByReviewId(reviewOP.get().getId());
            }
        } catch (Exception e) {
            throw new Exception500("상담 또는 상담 후기, 상담 스타일 삭제 실패 : " + e.getMessage());
        }
    }

    @Log
    public PageDTO<BackOfficeResponse.FAQDTO> getFAQs(Pageable pageable) {
        Page<FrequentQuestion> faqPG = frequentQuestionRepository.findAll(pageable);
        List<BackOfficeResponse.FAQDTO> list = faqPG.getContent().stream().map(faq ->
                new BackOfficeResponse.FAQDTO(faq)).collect(Collectors.toList());
        return new PageDTO<>(list, faqPG, FrequentQuestion.class);
    }

    @Log
    public BackOfficeResponse.FAQDTO getFAQ(Long faqId) {
        FrequentQuestion faqPS = frequentQuestionRepository.findById(faqId).orElseThrow(
                () -> new Exception404("존재하지 않는 FAQ입니다.")
        );

        return new BackOfficeResponse.FAQDTO(faqPS);
    }

    @Log
    @Transactional
    public void addFAQ(BackOfficeRequest.AddFAQDTO addFAQDTO) {
        try {
            frequentQuestionRepository.save(addFAQDTO.toEntity());
        } catch (Exception e) {
            throw new Exception500("FAQ 저장 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void updateFAQ(Long faqId, BackOfficeRequest.UpdateFAQDTO updateFAQDTO) {
        FrequentQuestion frequentQuestionPS = frequentQuestionRepository.findById(faqId).orElseThrow(
                () -> new Exception404("존재하지 않는 FAQ입니다.")
        );

        try {
            frequentQuestionPS.updateLabel(updateFAQDTO.getLabel());
            frequentQuestionPS.updateTitle(updateFAQDTO.getTitle());
            frequentQuestionPS.updateContent(updateFAQDTO.getContent());
        } catch (Exception e) {
            throw new Exception500("FAQ 수정 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void deleteFAQ(Long faqId) {
        frequentQuestionRepository.findById(faqId).orElseThrow(
                () -> new Exception404("존재하지 않는 FAQ입니다.")
        );

        try {
            frequentQuestionRepository.deleteById(faqId);
        } catch (Exception e) {
            throw new Exception500("FAQ 수정 실패 : " + e.getMessage());
        }
    }
}
