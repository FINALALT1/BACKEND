package kr.co.moneybridge.controller;

import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.board.Board;
import kr.co.moneybridge.model.board.BoardRepository;
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
}
