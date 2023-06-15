package kr.co.moneybridge.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.moneybridge.core.annotation.SwaggerResponses;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.exception.Exception400;
import kr.co.moneybridge.core.exception.Exception404;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.board.BoardRequest;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.dto.board.ReplyRequest;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.BoardStatus;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.service.BoardService;
import kr.co.moneybridge.service.PBService;
import kr.co.moneybridge.service.ReplyService;
import kr.co.moneybridge.service.UserService;
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
    private final UserService userService;
    private final PBService pbService;

    @ApiOperation("컨텐츠 검색하기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "제목", required = false),
            @ApiImplicitParam(name = "name", value = "김피비", required = false)
    })
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/lounge/boards")
    public ResponseEntity<?> getBoardsWithTitle(@RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "name", required = false) String name) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO;

        if (title != null) {
            pageDTO = boardService.getBoardsWithTitle(title, pageable);
        } else if (name != null) {
            pageDTO = boardService.getBoardsWithPbName(name, pageable);
        } else {
            throw new Exception404("요청오류");
        }

        ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation("최신 컨텐츠순 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/boards")
    public ResponseEntity<?> getBoardsByNew() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBoardWithNew(pageable);
        ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation("조회수 높은 컨텐츠순 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/boards/hot")
    public ResponseEntity<?> getBoardsByHot() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "clickCount"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBoardWithHot(pageable);
        ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation("최신 컨텐츠 2개 + 조회수 높은 컨텐츠 2개")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/lounge/board")
    public ResponseEntity<?> getNewHotBoards() {
        List<BoardResponse.BoardPageDTO> boardList = boardService.getNewHotContents();
        BoardResponse.BoardListOutDTO boardListOutDTO = new BoardResponse.BoardListOutDTO(boardList);
        ResponseDTO<BoardResponse.BoardListOutDTO> responseDTO = new ResponseDTO<>(boardListOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation("컨텐츠 상세 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @GetMapping("/board/{id}")
    public ResponseEntity<?> getBoardDetail(@PathVariable Long id) {

        BoardResponse.BoardDetailDTO boardDetailDTO = boardService.getBoardDetail(id);
        ResponseDTO<BoardResponse.BoardDetailDTO> responseDTO = new ResponseDTO<>(boardDetailDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation("컨텐츠 북마크하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PostMapping("/auth/bookmark/board/{id}")
    public ResponseEntity<?> addBoardBookmark(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        boardService.bookmarkBoard(id, myUserDetails);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @ApiOperation("컨텐츠 북마크 삭제하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @DeleteMapping("/auth/bookmark/board/{id}")
    public ResponseEntity<?> deleteBoardBookmark(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Long memberId = myUserDetails.getMember().getId();
        boardService.deleteBookmarkBoard(id, memberId);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @ApiOperation("컨텐츠 댓글달기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PostMapping("/auth/board/{id}/reply")
    public ResponseEntity<?> postReply(@PathVariable Long id,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails,
                                       @RequestBody ReplyRequest.ReplyInDTO replyInDTO) {

        if (myUserDetails.getMember().getRole().equals(Role.USER)) {
            User user = userService.getUser(myUserDetails.getMember().getId());
            replyService.postUserReply(replyInDTO, user.getId(), id);
        } else if (myUserDetails.getMember().getRole().equals(Role.PB)) {
            PB pb = pbService.getPB(myUserDetails.getMember().getId());
            replyService.postPbReply(replyInDTO, pb.getId(), id);
        }

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @ApiOperation("대댓글달기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PostMapping("/auth/board/{id}/rereply")
    public ResponseEntity<?> postReReply(@PathVariable Long id, @RequestBody ReplyRequest.ReReplyInDTO reReplyInDTO) {

        replyService.postReReply(id, reReplyInDTO);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @ApiOperation("컨텐츠 등록하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/pb/board")
    public ResponseEntity<?> saveBoard(@RequestBody @Valid BoardRequest.BoardInDTO boardInDTO,
                                       @AuthenticationPrincipal MyUserDetails myUserDetails) {

        if (boardInDTO.getContent().isEmpty()) throw new Exception400("content", "컨텐츠 내용 없음");
        if (boardInDTO.getThumbnail().isEmpty()) throw new Exception400("thumbnail", "썸네일 없음");
        if (boardInDTO.getTag1().isEmpty()) throw new Exception400("tag", "태그 없음");
        if (boardInDTO.getTag2().isEmpty()) throw new Exception400("tag", "태그 없음");

        Long id = boardService.saveBoard(boardInDTO, myUserDetails, BoardStatus.ACTIVE);
        ResponseDTO<Long> responseDTO = new ResponseDTO<>(id);

        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation("컨텐츠 임시저장하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/pb/board/temp")
    public ResponseEntity<?> saveTempBoard(@RequestBody @Valid BoardRequest.BoardInDTO boardInDTO,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {

        boardService.saveBoard(boardInDTO, myUserDetails, BoardStatus.TEMP);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @ApiOperation("임시저장 컨텐츠 목록 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/pb/boards/temp")
    public ResponseEntity<?> getTempBoards(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        List<BoardResponse.BoardTempDTO> tempBoards = boardService.getTempBoards(myUserDetails);
        ResponseDTO<List<BoardResponse.BoardTempDTO>> responseDTO = new ResponseDTO<>(tempBoards);

        return ResponseEntity.ok(responseDTO);
    }
    @ApiOperation("임시저장 컨텐츠 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @GetMapping("/pb/board/{id}")
    public ResponseEntity<?> getTempBoard(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long id) {

        BoardResponse.BoardOutDTO boardOutDTO = boardService.getBoard(myUserDetails, id);
        ResponseDTO<BoardResponse.BoardOutDTO> responseDTO = new ResponseDTO<>(boardOutDTO);

        return ResponseEntity.ok(responseDTO);

    }

    @ApiOperation("컨텐츠 수정하기/임시저장 컨텐츠 등록하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PutMapping("/pb/board/{id}")
    public ResponseEntity<?> putBoard(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                      @RequestBody BoardRequest.BoardInDTO boardInDTO,
                                      @PathVariable Long id) {

        boardService.putBoard(myUserDetails, boardInDTO, id);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @ApiOperation("컨텐츠 삭제하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @DeleteMapping("/pb/board/{id}")
    public ResponseEntity<?> deleteBoard(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long id) {

        boardService.deleteBoard(myUserDetails, id);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @ApiOperation("북마크한 컨텐츠 목록 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/auth/bookmarks/boards")
    public ResponseEntity<?> getBookmarkBoards(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBookmarkBoards(myUserDetails, pageable);
        ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> responseDTO = new ResponseDTO<>(pageDTO);

        return ResponseEntity.ok(responseDTO);
    }
}
