package kr.co.moneybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.core.auth.session.MyUserDetails;
import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.core.util.S3Util;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.board.BoardRequest;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.board.*;
import kr.co.moneybridge.model.pb.*;
import kr.co.moneybridge.model.user.User;
import kr.co.moneybridge.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BoardServiceTest extends MockDummyEntity {

    @InjectMocks
    private BoardService boardService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardBookmarkRepository boardBookmarkRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PBRepository pbRepository;
    @Mock
    private ReplyRepository replyRepository;
    @Mock
    private ReReplyRepository reReplyRepository;
    @Mock
    private MyUserDetails myUserDetails;
    @Mock
    private Member member;
    @Mock
    private BoardBookmark boardBookmark;
    @Mock
    private PB pb;
    @Mock
    private Company company;
    @Mock
    private User user;
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private S3Util s3Util;
    @Spy
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("컨텐츠검색")
    void getBoardsWithTitle() {
        //given
        String title = "제목";
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb1 = newMockPB(1L, "이피비", branch);
        PB pb2 = newMockPB(2L, "김피비", branch);
        Board board1 = newMockBoard(1L, "제목1.", pb1);
        Board board2 = newMockBoard(2L, "제목2.", pb1);
        Board board3 = newMockBoard(3L, "타이틀1.", pb2);
        Board board4 = newMockBoard(4L, "타이틀2.", pb2);
        Pageable pageable = PageRequest.of(0, 10);

        List<BoardResponse.BoardPageDTO> list = new ArrayList<>();
        list.add(new BoardResponse.BoardPageDTO(board1, pb1, company));
        list.add(new BoardResponse.BoardPageDTO(board2, pb1, company));
        list.add(new BoardResponse.BoardPageDTO(board3, pb2, company));
        list.add(new BoardResponse.BoardPageDTO(board4, pb2, company));

        Page<BoardResponse.BoardPageDTO> page = new PageImpl<>(list, pageable, list.size());

        //stub
        Mockito.when(boardRepository.findBySearch(title, BoardStatus.ACTIVE, pageable)).thenReturn(page);

        //when
        PageDTO<BoardResponse.BoardPageDTO> result = boardService.getBoardsWithTitle(title, pageable);

        //then
        assertEquals(list.size(), result.getList().size());
        assertEquals(list.get(0).getTitle(), result.getList().get(0).getTitle());
    }

    @Test
    @DisplayName("최신컨텐츠순으로 가져오기")
    void getBoardWithNew() {
        //given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb1 = newMockPB(1L, "이피비", branch);
        PB pb2 = newMockPB(2L, "김피비", branch);
        Board board1 = newMockBoard(1L, "제목1", pb1);
        Board board2 = newMockBoard(2L, "제목2", pb1);
        Board board3 = newMockBoard(3L, "타이틀1", pb2);
        Board board4 = newMockBoard(4L, "타이틀2", pb2);
        Pageable pageable = PageRequest.of(0, 10);

        List<BoardResponse.BoardPageDTO> list = new ArrayList<>();
        list.add(new BoardResponse.BoardPageDTO(board1, pb1, company));
        list.add(new BoardResponse.BoardPageDTO(board2, pb1, company));
        list.add(new BoardResponse.BoardPageDTO(board3, pb2, company));
        list.add(new BoardResponse.BoardPageDTO(board4, pb2, company));

        Page<BoardResponse.BoardPageDTO> page = new PageImpl<>(list, pageable, list.size());

        //stub
        Mockito.when(boardRepository.findAll(BoardStatus.ACTIVE, pageable)).thenReturn(page);

        //when
        PageDTO<BoardResponse.BoardPageDTO> result = boardService.getBoardWithNew(pageable);

        //then
        Assertions.assertThat(result.getList().get(0).getTitle()).isEqualTo("제목1");
        Assertions.assertThat(result.getList().get(1).getTitle()).isEqualTo("제목2");
        Assertions.assertThat(result.getList().get(2).getTitle()).isEqualTo("타이틀1");
        Assertions.assertThat(result.getList().get(3).getTitle()).isEqualTo("타이틀2");
    }

    @Test
    @DisplayName("핫한컨테츠순으로 가져오기")
    void getBoardWithHot() {
        //given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb1 = newMockPB(1L, "이피비", branch);
        PB pb2 = newMockPB(2L, "김피비", branch);
        Board board1 = newMockBoard(1L, "제목1", pb1);
        Board board2 = newMockBoard(2L, "제목2", pb1);
        Board board3 = newMockBoard(3L, "타이틀1", pb2);
        Board board4 = newMockBoard(4L, "타이틀2", pb2);
        Pageable pageable = PageRequest.of(0, 10);

        List<BoardResponse.BoardPageDTO> list = new ArrayList<>();
        list.add(new BoardResponse.BoardPageDTO(board1, pb1, company));
        list.add(new BoardResponse.BoardPageDTO(board2, pb1, company));
        list.add(new BoardResponse.BoardPageDTO(board3, pb2, company));
        list.add(new BoardResponse.BoardPageDTO(board4, pb2, company));

        Page<BoardResponse.BoardPageDTO> page = new PageImpl<>(list, pageable, list.size());

        //stub
        Mockito.when(boardRepository.findAll(BoardStatus.ACTIVE, pageable)).thenReturn(page);

        //when
        PageDTO<BoardResponse.BoardPageDTO> result = boardService.getBoardWithHot(pageable);

        //then
        Assertions.assertThat(result.getList().get(0).getTitle()).isEqualTo("제목1");
        Assertions.assertThat(result.getList().get(1).getTitle()).isEqualTo("제목2");
        Assertions.assertThat(result.getList().get(2).getTitle()).isEqualTo("타이틀1");
        Assertions.assertThat(result.getList().get(3).getTitle()).isEqualTo("타이틀2");
    }

    @Test
    @DisplayName("최신컨텐츠2개 + 핫한컨텐츠2개 가져오기")
    void getNewHotContents() {
        //given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb1 = newMockPB(1L, "이피비", branch);
        PB pb2 = newMockPB(2L, "김피비", branch);
        Board board1 = newMockBoard(1L, "제목1", pb1);
        Board board2 = newMockBoard(2L, "제목2", pb1);
        Board board3 = newMockBoard(3L, "타이틀1", pb2);
        Board board4 = newMockBoard(4L, "타이틀2", pb2);
        Pageable pageable1 = PageRequest.of(0, 2, Sort.by("id").descending());
        Pageable pageable2 = PageRequest.of(0, 2, Sort.by("clickCount").descending());
        List<BoardResponse.BoardPageDTO> list = new ArrayList<>();
        list.add(new BoardResponse.BoardPageDTO(board1,pb1,company));
        list.add(new BoardResponse.BoardPageDTO(board2,pb1,company));
        list.add(new BoardResponse.BoardPageDTO(board3,pb1,company));
        list.add(new BoardResponse.BoardPageDTO(board4,pb1,company));

        //stub
        Mockito.when(boardRepository.findTop2ByNew(BoardStatus.ACTIVE, pageable1)).thenReturn(list);
        Mockito.when(boardRepository.findTop2ByHot(BoardStatus.ACTIVE, pageable2)).thenReturn(list);

        //when
        List<BoardResponse.BoardPageDTO> result = boardService.getNewHotContents();

        //then
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("제목1");
        Assertions.assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("컨텐츠 상세 가져오기")
    void getBoardDetail() {
        //given
        Member member = newMockUser(1L, "김투자");
        MyUserDetails myUserDetails1 = new MyUserDetails(member);
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        Board board = newMockBoard(1L, "컨텐츠입니다", pb);
        BoardResponse.BoardDetailDTO boardDetailDTO = new BoardResponse.BoardDetailDTO(board, pb);

        //stub
        Mockito.when(boardRepository.findBoardWithPBReply(1L, BoardStatus.ACTIVE)).thenReturn(Optional.of(boardDetailDTO));
        Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        Mockito.when(boardBookmarkRepository.existsByBookmarkerIdAndBookmarkerRoleAndBoardId(1L, BookmarkerRole.USER, 1L)).thenReturn(true);

        //when
        BoardResponse.BoardDetailDTO result = boardService.getBoardDetail(myUserDetails1,1L);

        //then
        Assertions.assertThat(result).isEqualTo(boardDetailDTO);
    }

    @Test
    @DisplayName("댓글 가져오기")
    void getReplies() {
        //given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        Board board = newMockBoard(1L, "컨텐츠입니다", pb);
        User user = newMockUser(1L, "투자자A");
        Reply reply1 = newMockUserReply(1L, board, user);
        Reply reply2 = newMockPBReply(1L, board, pb);
        ReReply reReply = newMockUserReReply(1L, reply1, user);

        BoardResponse.ReplyOutDTO userReplyDTO = new BoardResponse.ReplyOutDTO(reply1, user);
        BoardResponse.ReplyOutDTO pbReplyDTO = new BoardResponse.ReplyOutDTO(reply2, pb);
        BoardResponse.ReReplyOutDTO reReplyOutDTO = new BoardResponse.ReReplyOutDTO(reReply, user);

        List<BoardResponse.ReReplyOutDTO> reReplyOutDTOList = new ArrayList<>();
        reReplyOutDTOList.add(reReplyOutDTO);
        userReplyDTO.setReReply(reReplyOutDTOList);

        List<BoardResponse.ReplyOutDTO> userReplyList = new ArrayList<>();
        List<BoardResponse.ReplyOutDTO> pbReplyList = new ArrayList<>();
        userReplyList.add(userReplyDTO);
        pbReplyList.add(pbReplyDTO);

        //stub
        Mockito.when(replyRepository.findUserRepliesByBoardId(1L)).thenReturn(userReplyList);
        Mockito.when(replyRepository.findPBRepliesByBoardId(1L)).thenReturn(pbReplyList);

        //when
        List<BoardResponse.ReplyOutDTO> result = boardService.getReplies(1L);

        //then
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).contains(userReplyDTO, pbReplyDTO);
//        Assertions.assertThat(result.get(1).getReReply()).hasSize(1);
    }

    @Test
    @DisplayName("대댓글 가져오기")
    void getReReplies() {
        //given
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        Board board = newMockBoard(1L, "컨텐츠입니다", pb);
        User user = newMockUser(1L, "투자자A");
        Reply reply = newMockUserReply(1L, board, user);
        ReReply reReply = newMockUserReReply(1L, reply, user);

        BoardResponse.ReReplyOutDTO reReplyOutDTO = new BoardResponse.ReReplyOutDTO(reReply, user);
        List<BoardResponse.ReReplyOutDTO> reReplyOutDTOList = new ArrayList<>();
        reReplyOutDTOList.add(reReplyOutDTO);

        //stub
        Mockito.when(reReplyRepository.findUserReReplyByReplyId(1L)).thenReturn(reReplyOutDTOList);
        Mockito.when(reReplyRepository.findPBReReplyByReplyId(1L)).thenReturn(reReplyOutDTOList);

        //when
        List<BoardResponse.ReReplyOutDTO> result = boardService.getReReplies(1L);
        System.out.println(result.get(0).getContent());
        System.out.println(result.get(1).getContent());
        //then
        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("북마크 저장하기")
    void bookmarkBoard() {
        //given
        User user = newMockUser(1L, "투자자A");
        Company company = newMockCompany(1L, "미래에셋");
        Branch branch = newMockBranch(1L, company, 1);
        PB pb = newMockPB(1L, "이피비", branch);
        Board board = newMockBoard(1L, "컨텐츠입니다", pb);

        //stub
//        Mockito.when(myUserDetails.getMember()).thenReturn(user);
//        Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
//        Mockito.when(boardBookmarkRepository.findWithUserAndBoard(1L, 1L)).thenReturn(Optional.empty());

        //when

        //then
//        Assertions.assertThatCode(() -> boardService.bookmarkBoard(1L, myUserDetails)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("북마크 취소하기")
    void deleteBookmarkBoard() {
        //given

        //stub
        Mockito.when(boardBookmarkRepository.findByMemberAndBoardId(1L, 1L)).thenReturn(Optional.of(boardBookmark));
        Mockito.when(boardBookmark.getId()).thenReturn(1L);

        //when

        //then
        Assertions.assertThatCode(() -> boardService.deleteBookmarkBoard(1L, 1L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("컨텐츠 저장하기")
    void saveBoard() {
        //given
        BoardRequest.BoardInDTO boardInDTO = new BoardRequest.BoardInDTO();
        boardInDTO.setTitle("제목입니다");
        boardInDTO.setContent("컨텐츠입니다");
        boardInDTO.setTag1("tag1");
        boardInDTO.setTag2("tag2");

        //stub
        Mockito.when(myUserDetails.getMember()).thenReturn(member);
        Mockito.when(member.getId()).thenReturn(1L);
        Mockito.when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        Mockito.when(boardRepository.save(ArgumentMatchers.any(Board.class))).thenAnswer(i -> i.getArguments()[0]);

        //when

        //then
        Assertions.assertThatCode(() -> boardService.saveBoard(multipartFile, boardInDTO, myUserDetails, BoardStatus.ACTIVE)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("임시저장 컨텐츠들 가져오기")
    void getTempBoards() {
        //given
        Board board = Board.builder()
                .id(1L)
                .pb(pb)
                .title("타이틀입니다.")
                .content("컨텐츠입니다.")
                .createdAt(LocalDateTime.now())
                .status(BoardStatus.TEMP)
                .build();

        List<Board> boardList = Collections.singletonList(board);

        //stub
        Mockito.when(myUserDetails.getMember()).thenReturn(member);
        Mockito.when(member.getId()).thenReturn(1L);
        Mockito.when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        Mockito.when(pb.getId()).thenReturn(1L);
        Mockito.when(boardRepository.findBoardsByPbId(1L, BoardStatus.TEMP)).thenReturn(boardList);

        //when
        List<BoardResponse.BoardTempDTO> result = boardService.getTempBoards(myUserDetails);

        //then
        Assertions.assertThat(result).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", board.getId());
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("title", board.getTitle());
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("content", board.getContent());
    }

    @Test
    @DisplayName("저장,임시저장 컨텐츠 가져오기(수정용)")
    void getBoard() {
        //given
        Board board = Board.builder()
                .id(1L)
                .pb(pb)
                .title("타이틀입니다.")
                .content("컨텐츠입니다.")
                .createdAt(LocalDateTime.now())
                .status(BoardStatus.TEMP)
                .build();

        //stub
        Mockito.when(myUserDetails.getMember()).thenReturn(member);
        Mockito.when(member.getId()).thenReturn(1L);
        Mockito.when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        Mockito.when(pb.getId()).thenReturn(1L);
        Mockito.when(boardRepository.findByIdAndPbId(1L, 1L)).thenReturn(Optional.ofNullable(board));

        //when
        BoardResponse.BoardOutDTO result = boardService.getBoard(myUserDetails, 1L);

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasFieldOrPropertyWithValue("title", board.getTitle());
        Assertions.assertThat(result).hasFieldOrPropertyWithValue("content", board.getContent());
        Assertions.assertThat(result).hasFieldOrPropertyWithValue("status", board.getStatus());
    }

    @Test
    @DisplayName("컨텐츠 수정하기/임시저장컨텐츠 업로드하기")
    void putBoard() {
        //given
        Board board = Board.builder()
                .id(1L)
                .pb(pb)
                .title("타이틀입니다.")
                .content("컨텐츠입니다.")
                .createdAt(LocalDateTime.now())
                .status(BoardStatus.TEMP)
                .build();

        BoardRequest.BoardUpdateDTO boardUpdateDTO = new BoardRequest.BoardUpdateDTO();
        boardUpdateDTO.setTitle("업데이트된 타이틀입니다.");
        boardUpdateDTO.setContent("업데이트된 제목입니다.");

        //stub
        Mockito.when(myUserDetails.getMember()).thenReturn(member);
        Mockito.when(member.getId()).thenReturn(1L);
        Mockito.when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        Mockito.when(pb.getId()).thenReturn(1L);
        Mockito.when(boardRepository.findByIdAndPbId(1L, 1L)).thenReturn(Optional.ofNullable(board));

        //when
        Assertions.assertThatCode(() -> boardService.putBoard(multipartFile, myUserDetails, boardUpdateDTO, 1L)).doesNotThrowAnyException();

        //then
        Assertions.assertThat(board).hasFieldOrPropertyWithValue("title", boardUpdateDTO.getTitle());
        Assertions.assertThat(board).hasFieldOrPropertyWithValue("content", boardUpdateDTO.getContent());
    }

    @Test
    @DisplayName("컨텐츠 삭제하기")
    void deleteBoard() {
        //given
        Board board = Board.builder()
                .id(1L)
                .pb(pb)
                .title("타이틀입니다.")
                .content("컨텐츠입니다.")
                .createdAt(LocalDateTime.now())
                .status(BoardStatus.TEMP)
                .build();

        //stub
        Mockito.when(myUserDetails.getMember()).thenReturn(member);
        Mockito.when(member.getId()).thenReturn(1L);
        Mockito.when(pbRepository.findById(1L)).thenReturn(Optional.of(pb));
        Mockito.when(pb.getId()).thenReturn(1L);
        Mockito.when(boardRepository.findByIdAndPbId(1L, 1L)).thenReturn(Optional.ofNullable(board));
        Mockito.doNothing().when(replyRepository).deleteByBoardId(1L);
        Mockito.doNothing().when(boardBookmarkRepository).deleteByBoardId(1L);
        Mockito.doNothing().when(boardRepository).deleteById(1L);

        //when

        //then
        Assertions.assertThatCode(() -> boardService.deleteBoard(myUserDetails, 1L)).doesNotThrowAnyException();
        Mockito.verify(replyRepository, Mockito.times(1)).deleteByBoardId(1L);
        Mockito.verify(boardBookmarkRepository).deleteByBoardId(1L);
        Mockito.verify(boardRepository).deleteById(1L);
    }

    @Test
    @DisplayName("북마크한 컨텐츠 목록 가져오기")
    void getBookmarkBoards() {
        //given
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<BoardResponse.BoardPageDTO> content = new ArrayList<>();
        Page<BoardResponse.BoardPageDTO> boardPage = new PageImpl<>(content, pageable, 0);

        //stub
        Mockito.when(myUserDetails.getMember()).thenReturn(member);
        Mockito.when(member.getRole()).thenReturn(Role.USER);
        Mockito.when(member.getId()).thenReturn(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(user.getId()).thenReturn(1L);
        Mockito.when(boardRepository.findBookmarkBoardsWithUserId(1L, pageable)).thenReturn(boardPage);

        //when
        PageDTO<BoardResponse.BoardPageDTO> result = boardService.getBookmarkBoards(myUserDetails, pageable);

        //then
        Assertions.assertThat(result.getList().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("맞춤컨텐츠 2개 가져오기")
    void getRecommendedBoards() {
        //given
        Long memberId = 1L; // Assume
        User user = newMockUser(memberId, "김테스터");
        Board board1 = newMockBoard(1L, "테스트입니다1", pb);
        Board board2 = newMockBoard(2L, "테스트입니다2", pb);
        MyUserDetails myUserDetails = new MyUserDetails(user);
        BoardResponse.BoardPageDTO boardDTO1 = new BoardResponse.BoardPageDTO(board1, pb, company);
        BoardResponse.BoardPageDTO boardDTO2 = new BoardResponse.BoardPageDTO(board2, pb, company);
        List<BoardResponse.BoardPageDTO> list = Arrays.asList(boardDTO1, boardDTO2);

        //stub
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(boardRepository.findRecommendedBoards(any(PageRequest.class), any(PBSpeciality.class),
                any(PBSpeciality.class), any(PBSpeciality.class))).thenReturn(list);

        //when
        List<BoardResponse.BoardPageDTO> result = boardService.getRecommendedBoards(myUserDetails);

        //then
        assertThat(result).isEqualTo(list);
    }

    @Test
    @DisplayName("맞춤컨텐츠 2개 가져오기")
    void getTwoBoards() {
        //given
        Board board1 = newMockBoard(1L, "테스트입니다1", pb);
        Board board2 = newMockBoard(2L, "테스트입니다2", pb);
        BoardResponse.BoardPageDTO boardDTO1 = new BoardResponse.BoardPageDTO(board1, pb, company);
        BoardResponse.BoardPageDTO boardDTO2 = new BoardResponse.BoardPageDTO(board2, pb, company);
        List<BoardResponse.BoardPageDTO> list = Arrays.asList(boardDTO1, boardDTO2);

        //stub
        when(boardRepository.findTwoBoards(any(PageRequest.class))).thenReturn(list);

        //when
        List<BoardResponse.BoardPageDTO> result = boardService.getTwoBoards();

        //then
        assertThat(result).isEqualTo(list);
    }
}