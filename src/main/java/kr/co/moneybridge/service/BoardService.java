package kr.co.moneybridge.service;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.*;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.board.BoardRequest;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.pb.PBSpeciality;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserPropensity;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final UserRepository userRepository;
    private final PBRepository pbRepository;
    private final ReplyRepository replyRepository;
    private final ReReplyRepository reReplyRepository;

    //컨텐츠검색
    public PageDTO<BoardResponse.BoardPageDTO> getBoardsWithTitle(String title, Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findByTitle(title, BoardStatus.ACTIVE, pageable);
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

    //최신컨텐츠2개 + 핫한컨텐츠2개 가져오기
    public List<BoardResponse.BoardPageDTO> getNewHotContents() {

        List<BoardResponse.BoardPageDTO> boardList = new ArrayList<>();
        PageRequest pageRequestNew = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        PageRequest pageRequestHot = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "clickCount"));

        boardList.addAll(boardRepository.findTop2ByNew(BoardStatus.ACTIVE, pageRequestNew));
        boardList.addAll(boardRepository.findTop2ByHot(BoardStatus.ACTIVE, pageRequestHot));
        return boardList;
    }

    //컨텐츠 상세 가져오기
    @Transactional
    public BoardResponse.BoardDetailDTO getBoardDetail(Long id) {

        BoardResponse.BoardDetailDTO boardDetailDTO = boardRepository.findBoardWithPBReply(id, BoardStatus.ACTIVE).orElseThrow(
                () -> new Exception404("존재하지 않는 컨텐츠입니다.")
        );

        try {
            Board board = boardRepository.findById(boardDetailDTO.getId()).get();
            board.increaseCount();  //호출 시 클릭수 증가 로직
        } catch (Exception e) {
            throw new Exception500("클릭수 증가 에러");
        }

        boardDetailDTO.setReply(getReplies(id));

        return boardDetailDTO;
    }

    //댓글 가져오기
    public List<BoardResponse.ReplyOutDTO> getReplies(Long id) {

        List<BoardResponse.ReplyOutDTO> replyList = new ArrayList<>();
        List<BoardResponse.ReplyOutDTO> userReplyList = replyRepository.findUserRepliesByBoardId(id);
        List<BoardResponse.ReplyOutDTO> PBReplyList = replyRepository.findPBRepliesByBoardId(id);
        replyList.addAll(userReplyList);
        replyList.addAll(PBReplyList);

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
        reReplyList.addAll(userReReplyList);
        reReplyList.addAll(pbReReplyList);

        return reReplyList;
    }

    //북마크 저장하기
    @Transactional
    public void bookmarkBoard(Long boardId, MyUserDetails myUserDetails) {

        Member member = myUserDetails.getMember();
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new Exception404("해당 컨텐츠는 존재하지 않습니다."));

        if (member.getRole().equals(Role.USER)) {
            if (boardBookmarkRepository.findWithUserAndBoard(member.getId(), boardId).isPresent()) {
                throw new Exception404("이미 북마크한 컨텐츠입니다.");
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
            if (boardBookmarkRepository.findWithPBAndBoard(member.getId(), boardId).isPresent()) {
                throw new Exception400("content", "이미 북마크한 컨텐츠입니다.");
            }
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
            throw new Exception401("권한이 없습니다.");
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
    public Long saveBoard(BoardRequest.BoardInDTO boardInDTO, MyUserDetails myUserDetails, BoardStatus boardStatus) {

        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                () -> new Exception404("존재하지 않는 PB 입니다"));

        if (boardInDTO.getThumbnail() == null || boardInDTO.getThumbnail().isEmpty()) {
            boardInDTO.setThumbnail("https://pb-business-card.s3.ap-northeast-2.amazonaws.com/sample.svg"); //기본사진
        }
        try {
            Long id = boardRepository.save(Board.builder()
                    .pb(pb)
                    .title(boardInDTO.getTitle())
                    .thumbnail(boardInDTO.getThumbnail())
                    .content(boardInDTO.getContent())
                    .tag1(boardInDTO.getTag1())
                    .tag2(boardInDTO.getTag2())
                    .clickCount(0L)
                    .status(boardStatus)
                    .build()).getId();

            return id;
        } catch (Exception e) {
            throw new Exception500("컨텐츠 저장 실패 : " + e.getMessage());
        }
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
    public void putBoard(MyUserDetails myUserDetails, BoardRequest.BoardInDTO boardInDTO, Long boardId) {

        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 PB 입니다"));
        Board board = boardRepository.findByIdAndPbId(boardId, pb.getId()).orElseThrow(() -> new Exception404("존재하지 않는 컨텐츠입니다"));

        if (boardInDTO.getThumbnail() == null || boardInDTO.getThumbnail().isEmpty()) {
            boardInDTO.setThumbnail("https://pb-business-card.s3.ap-northeast-2.amazonaws.com/sample.svg");
        }

        try {
            board.modifyBoard(boardInDTO);
        } catch (Exception e) {
            throw new Exception500("컨텐츠 업데이트 실패");
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
                dto.setIsBookmark(true);
            }
            return new PageDTO<>(list, boardPG);

        } else if (member.getRole().equals(Role.PB)) {
            PB pb = pbRepository.findById(member.getId()).orElseThrow(() -> new Exception404("존재하지 않는 PB 입니다."));
            Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findBookmarkBoardsWithPbId(pb.getId(), pageable);
            List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());
            for (BoardResponse.BoardPageDTO dto : list) {
                dto.setIsBookmark(true);
            }
            return new PageDTO<>(list, boardPG);

        } else {
            throw new Exception403("권한이 없습니다.");
        }
    }


    //맞춤컨텐츠 2개 가져오기
    public List<BoardResponse.BoardPageDTO> getRecommendedBoards(MyUserDetails myUserDetails) {

        User user = userRepository.findById(myUserDetails.getMember().getId()).orElseThrow(() -> new Exception404("존재하지 않는 유저입니다."));
        UserPropensity propensity = user.getPropensity();
        List<BoardResponse.BoardPageDTO> boardList;

        if (propensity == null) {
            throw new Exception404("투자성향 분석이 되지않았습니다.");
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
            dto.setIsBookmark(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRole(dto.getId(),BookmarkerRole.USER));
        }

        return boardList;
    }

    //컨텐츠 가져오기(성향X)
    public List<BoardResponse.BoardPageDTO> getTwoBoards() {

        Pageable pageable= PageRequest.of(0, 2, Sort.by("id").descending());
        List<BoardResponse.BoardPageDTO> boardList = boardRepository.findTwoBoards(pageable);

        return boardList;
    }

    //해당 PB의 컨텐츠목록 가져오기
    public PageDTO<BoardResponse.BoardPageDTO> getPBBoards(MyUserDetails myUserDetails, Long pbId, Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findByPBId(pbId, pageable);
        BookmarkerRole bookmarkerRole;

        if (myUserDetails.getMember().getRole().equals(Role.USER)) {
            bookmarkerRole = BookmarkerRole.USER;
        } else {
            bookmarkerRole = BookmarkerRole.PB;
        }

        for (BoardResponse.BoardPageDTO dto : boardPG) {
            dto.setIsBookmark(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRole(myUserDetails.getMember().getId(), bookmarkerRole));
        }

        List<BoardResponse.BoardPageDTO> list = boardPG.getContent().stream().collect(Collectors.toList());

        return new PageDTO<>(list, boardPG);
    }
}
