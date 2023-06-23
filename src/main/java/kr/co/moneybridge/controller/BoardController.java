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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @ApiImplicitParam(name = "title", value = "제목"),
            @ApiImplicitParam(name = "name", value = "김피비"),
            @ApiImplicitParam(name = "page", value = "0")
    })
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/lounge/boards")
    public ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> getBoardsWithTitle(@RequestParam(value = "title", required = false) String title,
                                                                               @RequestParam(value = "name", required = false) String name,
                                                                               @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO;

        if (title != null) {
            pageDTO = boardService.getBoardsWithTitle(title, pageable);
        } else if (name != null) {
            pageDTO = boardService.getBoardsWithPbName(name, pageable);
        } else {
            throw new Exception404("요청오류");
        }

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("최신 컨텐츠순 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "page", value = "0")
    @GetMapping("/boards")
    public ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> getBoardsByNew(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBoardWithNew(pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("조회수 높은 컨텐츠순 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "page", value = "0")
    @GetMapping("/boards/hot")
    public ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> getBoardsByHot(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "clickCount"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBoardWithHot(pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("최신 컨텐츠 2개 + 조회수 높은 컨텐츠 2개")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/lounge/board")
    public ResponseDTO<BoardResponse.BoardListOutDTO> getNewHotBoards() {
        List<BoardResponse.BoardPageDTO> boardList = boardService.getNewHotContents();
        BoardResponse.BoardListOutDTO boardListOutDTO = new BoardResponse.BoardListOutDTO(boardList);

        return new ResponseDTO<>(boardListOutDTO);
    }

    @ApiOperation("컨텐츠 상세 가져오기(로그인)")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @GetMapping("/auth/board/{id}")
    public ResponseDTO<BoardResponse.BoardDetailDTO> getBoardDetail(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long id) {

        BoardResponse.BoardDetailDTO boardDetailDTO = boardService.getBoardDetail(myUserDetails, id);

        return new ResponseDTO<>(boardDetailDTO);
    }

    @ApiOperation("컨텐츠 상세 가져오기(비로그인)")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "id", value = "1", dataType = "Long")
    @GetMapping("/board/{id}")
    public ResponseDTO<BoardResponse.BoardThumbnailDTO> getBoardThumbnail(@PathVariable Long id) {

        BoardResponse.BoardThumbnailDTO boardThumbnailDTO = boardService.getBoardThumbnail(id);

        return new ResponseDTO<>(boardThumbnailDTO);
    }

    @ApiOperation("컨텐츠 북마크하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PostMapping("/auth/bookmark/board/{id}")
    public ResponseDTO addBoardBookmark(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        boardService.bookmarkBoard(id, myUserDetails);

        return new ResponseDTO<>();
    }

    @ApiOperation("컨텐츠 북마크 삭제하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @DeleteMapping("/auth/bookmark/board/{id}")
    public ResponseDTO deleteBoardBookmark(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Long memberId = myUserDetails.getMember().getId();
        boardService.deleteBookmarkBoard(id, memberId);

        return new ResponseDTO<>();
    }

    @ApiOperation("컨텐츠 댓글달기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PostMapping("/auth/board/{id}/reply")
    public ResponseDTO postReply(@PathVariable Long id,
                                 @AuthenticationPrincipal MyUserDetails myUserDetails,
                                 @RequestBody ReplyRequest.ReplyInDTO replyInDTO) {

        if (myUserDetails.getMember().getRole().equals(Role.USER)) {
            User user = userService.getUser(myUserDetails.getMember().getId());
            replyService.postUserReply(replyInDTO, user.getId(), id);
        } else if (myUserDetails.getMember().getRole().equals(Role.PB)) {
            PB pb = pbService.getPB(myUserDetails.getMember().getId());
            replyService.postPbReply(replyInDTO, pb.getId(), id);
        }

        return new ResponseDTO<>();
    }

    @ApiOperation("댓글 수정하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "replyId", value = "1", dataType = "Long")
    @PatchMapping("/auth/board/reply/{replyId}")
    public ResponseDTO updateReply(@PathVariable(value = "replyId") Long replyId,
                                   @AuthenticationPrincipal MyUserDetails myUserDetails,
                                   @RequestBody ReplyRequest.ReplyInDTO replyInDTO) {

        boardService.updateReply(myUserDetails, replyId, replyInDTO);

        return new ResponseDTO<>();
    }

    @ApiOperation("댓글 삭제하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "replyId", value = "1", dataType = "Long")
    @DeleteMapping("/auth/board/reply/{replyId}")
    public ResponseDTO deleteReply(@PathVariable(value = "replyId") Long replyId,
                                   @AuthenticationPrincipal MyUserDetails myUserDetails) {

        boardService.deleteReply(myUserDetails, replyId);

        return new ResponseDTO<>();
    }

    @ApiOperation("대댓글 수정하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "rereplyId", value = "1", dataType = "Long")
    @PatchMapping("/auth/board/rereply/{rereplyId}")
    public ResponseDTO updateReReply(@PathVariable(value = "rereplyId") Long reReplyId,
                                   @AuthenticationPrincipal MyUserDetails myUserDetails,
                                   @RequestBody ReplyRequest.ReReplyInDTO reReplyInDTO) {

        boardService.updateReReply(myUserDetails, reReplyId, reReplyInDTO);

        return new ResponseDTO<>();
    }

    @ApiOperation("대댓글 삭제하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "rereplyId", value = "1", dataType = "Long")
    @DeleteMapping("/auth/board/rereply/{rereplyId}")
    public ResponseDTO deleteReReply(@PathVariable(value = "rereplyId") Long reReplyId,
                                   @AuthenticationPrincipal MyUserDetails myUserDetails) {

        boardService.deleteReReply(myUserDetails, reReplyId);

        return new ResponseDTO<>();
    }

    @ApiOperation("대댓글달기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PostMapping("/auth/board/{id}/rereply")
    public ResponseDTO postReReply(@PathVariable Long id, @RequestBody ReplyRequest.ReReplyInDTO reReplyInDTO) {

        replyService.postReReply(id, reReplyInDTO);

        return new ResponseDTO<>();
    }

    @ApiOperation("컨텐츠 등록하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/pb/board")
    public ResponseDTO saveBoard(@RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
                                 @RequestPart(value = "boardInDTO") @Valid BoardRequest.BoardInDTO boardInDTO,
                                 @AuthenticationPrincipal MyUserDetails myUserDetails) {

        if (boardInDTO.getContent() == null || boardInDTO.getContent().isEmpty()) throw new Exception400("content", "컨텐츠 내용 없음");
        if (boardInDTO.getTag1() == null || boardInDTO.getTag1().isEmpty()) throw new Exception400("tag", "태그 없음");
        if (boardInDTO.getTag2() == null || boardInDTO.getTag2().isEmpty()) throw new Exception400("tag", "태그 없음");

        Long id = boardService.saveBoard(thumbnailFile, boardInDTO, myUserDetails, BoardStatus.ACTIVE);

        return new ResponseDTO<>(id);
    }

    @ApiOperation("컨텐츠 임시저장하기")
    @SwaggerResponses.DefaultApiResponses
    @PostMapping("/pb/board/temp")
    public ResponseDTO saveTempBoard(@RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile,
                                     @RequestPart(value = "boardInDTO") @Valid BoardRequest.BoardInDTO boardInDTO,
                                     @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Long id = boardService.saveBoard(thumbnailFile, boardInDTO, myUserDetails, BoardStatus.TEMP);

        return new ResponseDTO<>(id);
    }

    @ApiOperation("임시저장 컨텐츠 목록 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/pb/boards/temp")
    public ResponseDTO<List<BoardResponse.BoardTempDTO>> getTempBoards(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        List<BoardResponse.BoardTempDTO> tempBoards = boardService.getTempBoards(myUserDetails);

        return new ResponseDTO<>(tempBoards);
    }

    @ApiOperation("임시저장 컨텐츠 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @GetMapping("/pb/board/{id}")
    public ResponseDTO<BoardResponse.BoardOutDTO> getTempBoard(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long id) {

        BoardResponse.BoardOutDTO boardOutDTO = boardService.getBoard(myUserDetails, id);

        return new ResponseDTO<>(boardOutDTO);

    }

    @ApiOperation("컨텐츠 수정하기/임시저장 컨텐츠 등록하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @PutMapping("/pb/board/{id}")
    public ResponseDTO putBoard(@RequestPart(value = "thumbnail",required = false) MultipartFile thumbnailFile,
                                @RequestPart(value = "boardUpdateDTO") @Valid BoardRequest.BoardUpdateDTO boardUpdateDTO,
                                @PathVariable Long id,
                                @AuthenticationPrincipal MyUserDetails myUserDetails) {

        if (boardUpdateDTO.getContent() == null || boardUpdateDTO.getContent().isEmpty()) throw new Exception400("content", "컨텐츠 내용 없음");
        if (boardUpdateDTO.getTag1() == null || boardUpdateDTO.getTag1().isEmpty()) throw new Exception400("tag", "태그 없음");
        if (boardUpdateDTO.getTag2() == null || boardUpdateDTO.getTag2().isEmpty()) throw new Exception400("tag", "태그 없음");

        boardService.putBoard(thumbnailFile, myUserDetails, boardUpdateDTO, id);

        return new ResponseDTO<>();
    }

    @ApiOperation("컨텐츠 삭제하기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "1", dataType = "Long", paramType = "query")})
    @DeleteMapping("/pb/board/{id}")
    public ResponseDTO deleteBoard(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long id) {

        boardService.deleteBoard(myUserDetails, id);

        return new ResponseDTO<>();
    }

    @ApiOperation("북마크한 컨텐츠 목록 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "page", value = "0")
    @GetMapping("/auth/bookmarks/boards")
    public ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> getBookmarkBoards(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                                                              @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getBookmarkBoards(myUserDetails, pageable);

        return new ResponseDTO<>(pageDTO);
    }

    @ApiOperation("맞춤컨텐츠 2개 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/user/main/board")
    public ResponseDTO<List<BoardResponse.BoardPageDTO>> getRecommendedBoards(@AuthenticationPrincipal MyUserDetails myUserDetails) {

        List<BoardResponse.BoardPageDTO> list = boardService.getRecommendedBoards(myUserDetails);
        BoardResponse.BoardListOutDTO listOutDTO = new BoardResponse.BoardListOutDTO(list);

        return new ResponseDTO(listOutDTO);
    }

    @ApiOperation("컨텐츠 2개 가져오기(성향X)")
    @SwaggerResponses.DefaultApiResponses
    @GetMapping("/main/board")
    public ResponseDTO<List<BoardResponse.BoardPageDTO>> getBoards() {

        List<BoardResponse.BoardPageDTO> list = boardService.getTwoBoards();
        BoardResponse.BoardListOutDTO listOutDTO = new BoardResponse.BoardListOutDTO(list);

        return new ResponseDTO(listOutDTO);
    }

    @ApiOperation("특정 PB의 컨텐츠 리스트 가져오기")
    @SwaggerResponses.DefaultApiResponses
    @ApiImplicitParam(name = "page", value = "0")
    @GetMapping("/auth/boards/{pbId}")
    public ResponseDTO<PageDTO<BoardResponse.BoardPageDTO>> getPBBoards(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                                                        @PathVariable(value = "pbId") Long pbId,
                                                                        @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        PageDTO<BoardResponse.BoardPageDTO> pageDTO = boardService.getPBBoards(myUserDetails, pbId, pageable);

        return new ResponseDTO<>(pageDTO);
    }
}
