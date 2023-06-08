package kr.co.moneybridge.service;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.core.exception.Exception500;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.board.BoardRequest;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    //컨텐츠검색
    public PageDTO<BoardResponse.BoardPageDTO> getBoardsWithTitle(String title, Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findByTitle(title, BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> content = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(content, boardPG);
    }

    //최신컨텐츠순으로 가져오기
    public PageDTO<BoardResponse.BoardPageDTO> getBoardWithNew(Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findAll(BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> content = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(content, boardPG);
    }

    //핫한컨테츠순으로 가져오기
    public PageDTO<BoardResponse.BoardPageDTO> getBoardWithHot(Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findAll(BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> content = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(content, boardPG);
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

        List<BoardResponse.ReplyOutDTO> replyList = replyRepository.findRepliesByBoardId(id, true);
        boardDetailDTO.setReply(replyList);

        return boardDetailDTO;
    }



    //북마크 저장하기
    public void bookmarkBoard(Long boardId, Long userId) {

        Optional<BoardBookmark> boardBookmarkOP = boardBookmarkRepository.findWithUserAndBoard(userId, boardId);

        if (boardBookmarkOP.isPresent()) {
            throw new Exception400("boardBookmark", "이미 북마크한 컨텐츠입니다");
        }

        try {
            Board board = boardRepository.findById(boardId).get();
            User user = userRepository.findById(userId).get();
            BoardBookmark boardBookmark = BoardBookmark.builder()
                    .user(user)
                    .board(board)
                    .createdAt(LocalDateTime.now())
                    .status(true)
                    .build();
            boardBookmarkRepository.save(boardBookmark);
        } catch (Exception e) {
            throw new Exception500("북마크 실패" + e.getMessage());
        }
    }

    //북마크 취소하기
    @Transactional
    public void DeleteBookmarkBoard(Long boardId, Long userId) {

        BoardBookmark boardBookmark = boardBookmarkRepository.findWithUserAndBoard(userId, boardId).orElseThrow(
                () -> new Exception400("boardBookmark", "북마크되지않은 컨텐츠입니다"));

        boardBookmark.resign();
    }

    //컨텐츠 저장하기
    @Transactional
    public Long saveBoard(BoardRequest.BoardInDTO boardInDTO, MyUserDetails myUserDetails, BoardStatus boardStatus) {

        PB pb = pbRepository.findById(myUserDetails.getMember().getId()).orElseThrow(
                () -> new Exception404("존재하지 않는 PB 입니다"));

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
}
