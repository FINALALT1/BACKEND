package kr.co.moneybridge.service;

import kr.co.moneybridge.core.annotation.Log;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.core.util.BizMessageUtil;
import kr.co.moneybridge.core.util.S3Util;
import kr.co.moneybridge.core.util.Template;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.board.BoardRequest;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserBookmarkRepository;
import kr.co.moneybridge.model.user.UserPropensity;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {
    private String defaultThumbnail = "https://moneybridge.s3.ap-northeast-2.amazonaws.com/default/post.png";
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final UserBookmarkRepository userBookmarkRepository;
    private final UserRepository userRepository;
    private final PBRepository pbRepository;
    private final ReplyRepository replyRepository;
    private final ReReplyRepository reReplyRepository;
    private final S3Util s3Util;
    private final BizMessageUtil biz;
    private final Environment environment;

    //컨텐츠검색(PB명 + 컨텐츠제목)
    public PageDTO<BoardResponse.BoardPageDTO> getBoardsWithTitle(String search, Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findBySearch(search, BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(list, boardPG);
    }

    //PB 명으로 컨텐츠 검색
    public PageDTO<BoardResponse.BoardPageDTO> getBoardsWithPbName(String name, Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findByPbName(name, BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(list, boardPG);
    }

    //최신컨텐츠순으로 가져오기
    @Log
    public PageDTO<BoardResponse.BoardPageDTO> getBoardWithNew(Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findAll(BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(list, boardPG);
    }

    //핫한컨테츠순으로 가져오기
    public PageDTO<BoardResponse.BoardPageDTO> getBoardWithHot(Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findAll(BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(list, boardPG);
    }

    //최신컨텐츠2개 + 핫한컨텐츠2개 가져오기(비로그인)
    public List<BoardResponse.BoardPageDTO> getNewHotContents() {

        List<BoardResponse.BoardPageDTO> boardList = new ArrayList<>();
        PageRequest pageRequestNew = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        PageRequest pageRequestHot = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "clickCount"));

        boardList.addAll(boardRepository.findTop2ByNew(BoardStatus.ACTIVE, pageRequestNew));
        boardList.addAll(boardRepository.findTop2ByHot(BoardStatus.ACTIVE, pageRequestHot));
        return boardList;
    }

    //최신컨텐츠2개 + 핫한컨텐츠2개 가져오기(로그인)
    public List<BoardResponse.BoardPageDTO> getLogInNewHotContents(MyUserDetails myUserDetails) {

        Member member = myUserDetails.getMember();
        List<BoardResponse.BoardPageDTO> boardList = new ArrayList<>();
        PageRequest pageRequestNew = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        PageRequest pageRequestHot = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "clickCount"));

        boardList.addAll(boardRepository.findTop2ByNew(BoardStatus.ACTIVE, pageRequestNew));
        boardList.addAll(boardRepository.findTop2ByHot(BoardStatus.ACTIVE, pageRequestHot));

        if (member.getRole().equals(Role.USER)) {
            for (BoardResponse.BoardPageDTO dto : boardList) {
                dto.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(member.getId(), BookmarkerRole.USER, dto.getId()));
            }
        } else if (member.getRole().equals(Role.PB)) {
            for (BoardResponse.BoardPageDTO dto : boardList) {
                dto.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(member.getId(), BookmarkerRole.PB, dto.getId()));
            }
        } else {
            for (BoardResponse.BoardPageDTO dto : boardList) {
                dto.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(member.getId(), BookmarkerRole.ADMIN, dto.getId()));
            }
        }

        return boardList;
    }

    //컨텐츠 상세 가져오기
    @Transactional
    public BoardResponse.BoardDetailDTO getBoardDetail(MyUserDetails myUserDetails, Long id) {

        BoardResponse.BoardDetailDTO boardDetailDTO = boardRepository.findBoardWithPBReply(id, BoardStatus.ACTIVE).orElseThrow(
                () -> new Exception404("존재하지 않는 컨텐츠입니다.")
        );

        try {
            Board board = boardRepository.findById(boardDetailDTO.getId()).get();
            board.increaseViewCount();
            board.increaseClickCount();  //호출 시 클릭수 증가 로직
        } catch (Exception e) {
            throw new Exception500("조회수, 클릭수 증가 에러");
        }

        Member member = myUserDetails.getMember();
        if (member.getRole().equals(Role.USER)) {
            boardDetailDTO.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(member.getId(), BookmarkerRole.USER, id));
        } else if (member.getRole().equals(Role.PB)) {
            boardDetailDTO.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(member.getId(), BookmarkerRole.PB, id));
        } else {
            boardDetailDTO.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(member.getId(), BookmarkerRole.ADMIN, id));
        }
        boardDetailDTO.setViewCount(boardDetailDTO.getViewCount() + 1);
        boardDetailDTO.setReply(getReplies(id));

        return boardDetailDTO;
    }

    //비회원 컨텐츠 상세보기
    @Transactional
    public BoardResponse.BoardDetailByAnonDTO getBoardDetailByAnon(Long id) {

        Board boardPS = boardRepository.findById(id).orElseThrow(() -> new Exception404("존재하지 않는 컨텐츠입니다."));

        boardPS.increaseViewCount();
        boardPS.increaseClickCount();

        BoardResponse.BoardDetailByAnonDTO boardDetailByAnonDTO = null;
        try {
            boardDetailByAnonDTO = boardRepository.findBoardWithPB(id, BoardStatus.ACTIVE);
        } catch (Exception e) {
            throw new Exception500("컨텐츠 조회 실패 : " + e.getMessage());
        }
        boardDetailByAnonDTO.setViewCount(boardDetailByAnonDTO.getViewCount() + 1);
        boardDetailByAnonDTO.setReply(getReplies(id));

        return boardDetailByAnonDTO;
    }

    //댓글 가져오기
    public List<BoardResponse.ReplyOutDTO> getReplies(Long id) {

        List<BoardResponse.ReplyOutDTO> replyList = new ArrayList<>();
        List<BoardResponse.ReplyOutDTO> userReplyList = replyRepository.findUserRepliesByBoardId(id);
        List<BoardResponse.ReplyOutDTO> pbReplyList = replyRepository.findPBRepliesByBoardId(id);
        List<BoardResponse.ReplyOutDTO> adminReplyList = replyRepository.findAdminRepliesByBoardId(id);

        replyList.addAll(userReplyList);
        replyList.addAll(pbReplyList);
        replyList.addAll(adminReplyList);
        // 댓글 최신순 정렬
        Collections.sort(replyList, new Comparator<BoardResponse.ReplyOutDTO>() {
            @Override
            public int compare(BoardResponse.ReplyOutDTO o1, BoardResponse.ReplyOutDTO o2) {
                if (o1.getCreatedAt().isBefore(o2.getCreatedAt())) {
                    return 1;
                } else if (o1.getCreatedAt().isAfter(o2.getCreatedAt())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (BoardResponse.ReplyOutDTO replyOutDTO : replyList) {
            replyOutDTO.setReReply(getReReplies(replyOutDTO.getId()));
        }

        return replyList;
    }

    //대댓글 가져오기
    public List<BoardResponse.ReReplyOutDTO> getReReplies(Long replyId) {

        List<BoardResponse.ReReplyOutDTO> reReplyList = new ArrayList<>();
        List<BoardResponse.ReReplyOutDTO> userReReplyList = reReplyRepository.findUserReReplyByReplyId(replyId);
        List<BoardResponse.ReReplyOutDTO> pbReReplyList = reReplyRepository.findPBReReplyByReplyId(replyId);
        List<BoardResponse.ReReplyOutDTO> adminReReplyList = reReplyRepository.findAdminReReplyByReplyId(replyId);
        reReplyList.addAll(userReReplyList);
        reReplyList.addAll(pbReReplyList);
        reReplyList.addAll(adminReReplyList);
        // 대댓글 최신순 정렬
        Collections.sort(reReplyList, new Comparator<BoardResponse.ReReplyOutDTO>() {
            @Override
            public int compare(BoardResponse.ReReplyOutDTO o1, BoardResponse.ReReplyOutDTO o2) {
                if (o1.getCreatedAt().isBefore(o2.getCreatedAt())) {
                    return 1;
                } else if (o1.getCreatedAt().isAfter(o2.getCreatedAt())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return reReplyList;
    }

    //북마크 저장하기
    @Transactional
    public void bookmarkBoard(Long boardId, MyUserDetails myUserDetails) {

        Member member = myUserDetails.getMember();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception404("해당 컨텐츠는 존재하지 않습니다."));

        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
            if (user.getHasDoneBoardBookmark().equals(false)) {
                user.updateHasDoneBoardBookmark(true);
            }
            try {
                BoardBookmark boardBookmarkUser = BoardBookmark.builder()
                        .bookmarkerId(member.getId())
                        .bookmarkerRole(BookmarkerRole.USER)
                        .board(board)
                        .build();
                boardBookmarkRepository.save(boardBookmarkUser);
            } catch (Exception e) {
                throw new Exception500("북마크 실패: " + e.getMessage());
            }
        } else if (member.getRole().equals(Role.PB)) {
            try {
                BoardBookmark boardBookmarkPB = BoardBookmark.builder()
                        .bookmarkerId(member.getId())
                        .bookmarkerRole(BookmarkerRole.PB)
                        .board(board)
                        .build();
                boardBookmarkRepository.save(boardBookmarkPB);
            } catch (Exception e) {
                throw new Exception500("북마크 실패: " + e.getMessage());
            }
        } else {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
            if (user.getHasDoneBoardBookmark().equals(false)) {
                user.updateHasDoneBoardBookmark(true);
            }
            try {
                BoardBookmark boardBookmarkAdmin = BoardBookmark.builder()
                        .bookmarkerId(member.getId())
                        .bookmarkerRole(BookmarkerRole.ADMIN)
                        .board(board)
                        .build();
                boardBookmarkRepository.save(boardBookmarkAdmin);
            } catch (Exception e) {
                throw new Exception500("북마크 실패: " + e.getMessage());
            }
        }
    }

    //북마크 취소하기
    @Transactional
    public void deleteBookmarkBoard(Long boardId, Long memberId) {

        BoardBookmark boardBookmark = boardBookmarkRepository.findByMemberAndBoardId(memberId, boardId).orElseThrow(
                () -> new Exception404("북마크 되지 않은 컨텐츠입니다"));

        try {
            boardBookmarkRepository.deleteById(boardBookmark.getId());
        } catch (Exception e) {
            throw new Exception500("북마크 취소 실패");
        }
    }

    //컨텐츠 저장하기
    @Transactional
    public Long saveBoard(MultipartFile thumbnailFile, BoardRequest.BoardInDTO boardInDTO, MyUserDetails myUserDetails, BoardStatus boardStatus) {

        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                () -> new Exception404("존재하지 않는 PB 입니다"));

        Board board = Board.builder()
                .pb(pb)
                .title(boardInDTO.getTitle())
                .thumbnail(defaultThumbnail)
                .content(boardInDTO.getContent())
                .tag1(boardInDTO.getTag1())
                .tag2(boardInDTO.getTag2())
                .viewCount(0L)
                .clickCount(0L)
                .status(boardStatus)
                .build();

        Long id = null;
        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            try {
                id = boardRepository.save(board).getId();
            } catch (Exception e) {
                throw new Exception500("컨텐츠 저장 실패 : " + e.getMessage());
            }
        } else {
            //s3 업로드 로직
            try {
                String thumbnail = s3Util.upload(thumbnailFile, "thumbnail");
                board.updateThumbnail(thumbnail);
                id = boardRepository.save(board).getId();
            } catch (Exception e) {
                throw new Exception500("컨텐츠 저장 실패 : " + e.getMessage());
            }
        }

        // 해당 PB를 북마크한 투자자들에게 알림톡 발신
        if (environment.acceptsProfiles("prod")) {
            List<User> users = userBookmarkRepository.findByPBId(pb.getId());
            for (User user : users) {
                biz.sendWebLinkNotification(
                        user.getPhoneNumber(),
                        Template.NEW_CONTENT,
                        biz.getTempMsg005(
                                user.getName(),
                                pb.getName(),
                                board
                        )
                );
            }
        }

        return id;
    }

    //임시저장 컨텐츠들 가져오기
    public List<BoardResponse.BoardTempDTO> getTempBoards(MyUserDetails myUserDetails) {

        List<BoardResponse.BoardTempDTO> dtoList = new ArrayList<>();
        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                () -> new Exception404("존재하지 않는 PB 입니다"));

        List<Board> tempBoards = boardRepository.findBoardsByPbId(pb.getId(), BoardStatus.TEMP);
        for (Board board : tempBoards) {
            BoardResponse.BoardTempDTO dto = new BoardResponse.BoardTempDTO();
            dto.setId(board.getId());
            dto.setTitle(board.getTitle());
            dto.setContent(board.getContent());
            dto.setCreatedAt(board.getCreatedAt());
            dtoList.add(dto);
        }

        return dtoList;
    }

    //저장,임시저장 컨텐츠 가져오기(수정용)
    public BoardResponse.BoardOutDTO getBoard(MyUserDetails myUserDetails, Long boardId) {

        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 PB 입니다"));
        Board board = boardRepository.findByIdAndPbId(boardId, pb.getId()).orElseThrow(() -> new Exception404("존재하지 않는 컨텐츠입니다"));
        BoardResponse.BoardOutDTO boardOutDTO = new BoardResponse.BoardOutDTO();

        boardOutDTO.setTitle(board.getTitle());
        boardOutDTO.setContent(board.getContent());
        boardOutDTO.setTag1(board.getTag1());
        boardOutDTO.setTag2(board.getTag2());
        boardOutDTO.setThumbnail(board.getThumbnail());
        boardOutDTO.setStatus(board.getStatus());

        return boardOutDTO;
    }

    //컨텐츠 수정하기/임시저장컨텐츠 업로드하기
    @Transactional
    public void putBoard(MultipartFile thumbnailFile, MyUserDetails myUserDetails, BoardRequest.BoardUpdateDTO boardUpdateDTO, Long boardId) {

        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 PB 입니다"));
        Board board = boardRepository.findByIdAndPbId(boardId, pb.getId()).orElseThrow(() -> new Exception404("존재하지 않는 컨텐츠입니다"));

        try {
            //변경할 썸네일 사진 들어온 경우
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String thumbnail = s3Util.upload(thumbnailFile, "thumbnail");
                board.updateThumbnail(thumbnail);
                board.modifyBoard(boardUpdateDTO);
            } else {
                //기존 썸네일 삭제 요청온 경우
                if (boardUpdateDTO.getDeleteThumbnail()) {
                    s3Util.delete(board.getThumbnail());
                    board.updateThumbnail(defaultThumbnail);
                    board.modifyBoard(boardUpdateDTO);
                    //기존 썸네일 삭제 요청 안온경우
                } else {
                    board.modifyBoard(boardUpdateDTO);
                }
            }
        } catch (Exception e) {
            throw new Exception500("컨텐츠 업데이트 실패 : " + e.getMessage());
        }
    }

    //컨텐츠 삭제하기
    @Transactional
    public void deleteBoard(MyUserDetails myUserDetails, Long boardId) {

        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 PB 입니다"));
        Board board = boardRepository.findByIdAndPbId(boardId, pb.getId()).orElseThrow(() -> new Exception404("존재하지 않는 컨텐츠입니다"));

        try {
            replyRepository.deleteByBoardId(board.getId());
            boardBookmarkRepository.deleteByBoardId(board.getId());
            boardRepository.deleteById(board.getId());
        } catch (Exception e) {
            throw new Exception500("컨텐츠 삭제 실패");
        }
    }

    //북마크한 컨텐츠 목록 가져오기
    public PageDTO<BoardResponse.BoardPageDTO> getBookmarkBoards(MyUserDetails myUserDetails, Pageable pageable) {

        Member member = myUserDetails.getMember();
        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
            Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findBookmarkBoardsWithUserId(user.getId(), pageable);
            List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
            for (BoardResponse.BoardPageDTO dto : list) {
                dto.setIsBookmarked(true);
            }
            return new PageDTO<>(list, boardPG);

        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception404("존재하지 않는 PB 입니다."));
            Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findBookmarkBoardsWithPbId(pb.getId(), pageable);
            List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
            for (BoardResponse.BoardPageDTO dto : list) {
                dto.setIsBookmarked(true);
            }
            return new PageDTO<>(list, boardPG);

        } else {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
            Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findBookmarkBoardsWithAdminId(user.getId(), pageable);
            List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
            for (BoardResponse.BoardPageDTO dto : list) {
                dto.setIsBookmarked(true);
            }
            return new PageDTO<>(list, boardPG);

        }
    }


    //맞춤컨텐츠 2개 가져오기
    public List<BoardResponse.BoardPageDTO> getRecommendedBoards(MyUserDetails myUserDetails) {

        User user = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
        UserPropensity propensity = user.getPropensity();
        List<BoardResponse.BoardPageDTO> boardList;

        if (propensity == null) {
            boardList = boardRepository.findTwoBoards(PageRequest.of(0, 2, Sort.by("id").descending()));
        } else if (propensity.equals(UserPropensity.CONSERVATIVE)) {
            boardList = boardRepository.findRecommendedBoards(PageRequest.of(0, 2, Sort.by("id").descending()),
                    PBSpeciality.BOND,
                    PBSpeciality.US_STOCK,
                    PBSpeciality.KOREAN_STOCK,
                    PBSpeciality.FUND,
                    PBSpeciality.DERIVATIVE,
                    PBSpeciality.ETF,
                    PBSpeciality.WRAP);
        } else if (propensity.equals(UserPropensity.CAUTIOUS)) {
            boardList = boardRepository.findRecommendedBoards(PageRequest.of(0, 2, Sort.by("id").descending()),
                    PBSpeciality.BOND,
                    PBSpeciality.US_STOCK,
                    PBSpeciality.KOREAN_STOCK,
                    PBSpeciality.FUND,
                    PBSpeciality.ETF,
                    PBSpeciality.WRAP);
        } else {
            boardList = boardRepository.findRecommendedBoards(PageRequest.of(0, 2, Sort.by("id").descending()),
                    PBSpeciality.BOND,
                    PBSpeciality.FUND,
                    PBSpeciality.WRAP);
        }

        for (BoardResponse.BoardPageDTO dto : boardList) {
            dto.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(user.getId(), BookmarkerRole.USER, dto.getId()));
        }

        return boardList;
    }

    //컨텐츠 가져오기(성향X)
    public List<BoardResponse.BoardPageDTO> getTwoBoards() {

        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
        List<BoardResponse.BoardPageDTO> boardList = boardRepository.findTwoBoards(pageable);

        return boardList;
    }

    //해당 PB의 컨텐츠목록 가져오기
    public PageDTO<BoardResponse.BoardPageDTO> getPBBoards(MyUserDetails myUserDetails, Long pbId, Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findByPBId(pbId, pageable);
        BookmarkerRole bookmarkerRole;

        if (myUserDetails.getMember().getRole().equals(Role.USER)) {
            bookmarkerRole = BookmarkerRole.USER;
        } else if (myUserDetails.getMember().getRole().equals(Role.PB)) {
            bookmarkerRole = BookmarkerRole.PB;
        } else {
            bookmarkerRole = BookmarkerRole.ADMIN;
        }

        for (BoardResponse.BoardPageDTO dto : boardPG) {
            dto.setIsBookmarked(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(myUserDetails.getMember().getId(), bookmarkerRole, dto.getId()));
        }

        List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());

        return new PageDTO<>(list, boardPG);
    }

    //댓글 수정하기
    @Transactional
    public void updateReply(MyUserDetails myUserDetails, Long replyId, ReplyRequest.ReplyInDTO replyInDTO) {

        Member member = myUserDetails.getMember();
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new Exception404("해당 댓글 찾을 수 없습니다."));

        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            if (reply.getAuthorId().equals(user.getId()) && reply.getAuthorRole().equals(ReplyAuthorRole.USER)) {
                reply.updateContent(replyInDTO.getContent());
            } else {
                throw new Exception404("잘못된 요청입니다.");
            }
        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 PB 찾을 수 없습니다."));
            if (reply.getAuthorId().equals(pb.getId()) && reply.getAuthorRole().equals(ReplyAuthorRole.PB)) {
                reply.updateContent(replyInDTO.getContent());
            } else {
                throw new Exception404("잘못된 요청입니다.");
            }
        } else {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            if (reply.getAuthorId().equals(user.getId()) && reply.getAuthorRole().equals(ReplyAuthorRole.ADMIN)) {
                reply.updateContent(replyInDTO.getContent());
            } else {
                throw new Exception404("잘못된 요청입니다.");
            }
        }
    }

    //댓글 삭제하기
    @Transactional
    public void deleteReply(MyUserDetails myUserDetails, Long replyId) {

        Member member = myUserDetails.getMember();
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new Exception404("해당 댓글 찾을 수 없습니다."));

        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            if (reply.getAuthorId().equals(user.getId()) && reply.getAuthorRole().equals(ReplyAuthorRole.USER)) {
                replyRepository.delete(reply);
            } else {
                throw new Exception404("삭제 권한 없습니다.");
            }
        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 PB 찾을 수 없습니다."));
            if (reply.getAuthorId().equals(pb.getId()) && reply.getAuthorRole().equals(ReplyAuthorRole.PB)) {
                replyRepository.delete(reply);
            } else {
                throw new Exception404("삭제 권한 없습니다.");
            }
        } else {
            userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            replyRepository.delete(reply);
        }

    }

    //대댓글 수정하기
    @Transactional
    public void updateReReply(MyUserDetails myUserDetails, Long reReplyId, ReplyRequest.ReReplyInDTO reReplyInDTO) {

        Member member = myUserDetails.getMember();
        ReReply reReply = reReplyRepository.findById(reReplyId).orElseThrow(() -> new Exception404("해당 대댓글 찾을 수 없습니다."));

        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            if (reReply.getAuthorId().equals(user.getId()) && reReply.getAuthorRole().equals(ReplyAuthorRole.USER)) {
                reReply.updateReReply(reReplyInDTO.getContent());
            } else {
                throw new Exception404("잘못된 요청입니다.");
            }
        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 PB 찾을 수 없습니다."));
            if (reReply.getAuthorId().equals(pb.getId()) && reReply.getAuthorRole().equals(ReplyAuthorRole.PB)) {
                reReply.updateReReply(reReplyInDTO.getContent());
            } else {
                throw new Exception404("잘못된 요청입니다.");
            }
        } else {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            if (reReply.getAuthorId().equals(user.getId()) && reReply.getAuthorRole().equals(ReplyAuthorRole.ADMIN)) {
                reReply.updateReReply(reReplyInDTO.getContent());
            } else {
                throw new Exception404("잘못된 요청입니다.");
            }
        }
    }

    //대댓글 삭제하기
    @Transactional
    public void deleteReReply(MyUserDetails myUserDetails, Long reReplyId) {

        Member member = myUserDetails.getMember();
        ReReply reReply = reReplyRepository.findById(reReplyId).orElseThrow(() -> new Exception404("해당 댓글 찾을 수 없습니다."));

        if (member.getRole().equals(Role.USER)) {
            User user = userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            if (reReply.getAuthorId().equals(user.getId()) && reReply.getAuthorRole().equals(ReplyAuthorRole.USER)) {
                reReplyRepository.delete(reReply);
            } else {
                throw new Exception404("삭제 권한 없습니다.");
            }
        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 PB 찾을 수 없습니다."));
            if (reReply.getAuthorId().equals(pb.getId()) && reReply.getAuthorRole().equals(ReplyAuthorRole.PB)) {
                reReplyRepository.delete(reReply);
            } else {
                throw new Exception404("삭제 권한 없습니다.");
            }
        } else {
            userRepository.findById(member.getId()).orElseThrow(() -> new Exception404("해당 유저 찾을 수 없습니다."));
            reReplyRepository.delete(reReply);
        }
    }

    public BoardResponse.PhotoPathDTO addPhoto(MultipartFile photo) {
        String photoPath = null;
        if (photo != null && !photo.isEmpty()) {
            photoPath = s3Util.upload(photo, "photo");
        }

        return new BoardResponse.PhotoPathDTO(photoPath);
    }
}
