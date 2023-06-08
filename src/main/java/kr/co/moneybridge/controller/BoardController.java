package kr.co.moneybridge.controller;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.board.BoardRequest;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.board.BoardStatus;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.service.BoardService;
import kr.co.moneybridge.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BoardController {
    private final BoardService boardService;
    private final ReplyService replyService;
    private final BoardRepository boardRepository;

    @GetMapping("/lounge/boards")
    public ResponseEntity<?> getBoardsWithTitle(@RequestParam(value = "title") String title) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBoardsWithTitle(title, pageable);
        ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/boards")
    public ResponseEntity<?> getBoardsByNew() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBoardWithNew(pageable);
        ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/boards/hot")
    public ResponseEntity<?> getBoardsByHot() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "clickCount"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBoardWithHot(pageable);
        ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/lounge/board")
    public ResponseEntity<?> getNewHotBoards() {
        List<BoardResponse.BoardPageDTO> boardList = boardService.getNewHotContents();
        ResponseDTO<List<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(boardList);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/board/{id}")
    public ResponseEntity<?> getBoardDetail(@PathVariable Long id) {

        BoardResponse.BoardDetailDTO boardDetailDTO = boardService.getBoardDetail(id);
        ResponseDTO<BoardResponse.BoardDetailDTO> responseDTO = new ResponseDTO<>(boardDetailDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/user/bookmark/board/{id}")
    public ResponseEntity<?> addBoardBookmark(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Long userId = myUserDetails.getMember().getId();
        boardService.bookmarkBoard(id, userId);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @DeleteMapping("/user/bookmark/board/{id}")
    public ResponseEntity<?> deleteBoardBookmark(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Long userId = myUserDetails.getMember().getId();
        boardService.DeleteBookmarkBoard(id, userId);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @PostMapping("/user/board/{id}/reply")
    public ResponseEntity<?> postReply(@PathVariable Long id,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails,
                                       @RequestBody ReplyRequest.ReplyInDTO replyInDTO) {

        Long userId = myUserDetails.getMember().getId();
        replyService.postReply(replyInDTO, userId, id);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @PostMapping("/pb/board")
    public ResponseEntity<?> saveBoard(@RequestBody @Valid BoardRequest.BoardInDTO boardInDTO,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {

        if (boardInDTO.getContent().isEmpty()) throw new Exception400("content", "컨텐츠 내용 없음");
        if (boardInDTO.getThumbnail().isEmpty()) throw new Exception400("thumbnail", "썸네일 없음");
        if (boardInDTO.getTag1().isEmpty()) throw new Exception400("tag", "태그 없음");
        if (boardInDTO.getTag2().isEmpty()) throw new Exception400("tag", "태그 없음");

        boardService.saveBoard(boardInDTO, myUserDetails, BoardStatus.ACTIVE);
    }

    @PostMapping("/pb/board/temp")
    public ResponseEntity<?> saveTempBoard(@RequestBody @Valid BoardRequest.BoardInDTO boardInDTO,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {

        boardService.saveBoard(boardInDTO, myUserDetails, BoardStatus.ACTIVE);
    }
}
