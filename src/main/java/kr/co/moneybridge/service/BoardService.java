package kr.co.moneybridge.service;

import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.board.BoardRepository;
import kr.co.moneybridge.model.board.BoardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public PageDTO<BoardResponse.BoardPageDTO> 컨텐츠검색(String title, Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findByTitle(title, BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> content = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(content, boardPG);
    }

    public PageDTO<BoardResponse.BoardPageDTO> 최신컨텐츠순(Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findAll(BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> content = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(content, boardPG);
    }

    public PageDTO<BoardResponse.BoardPageDTO> 핫한컨텐츠순(Pageable pageable) {

        Page<BoardResponse.BoardPageDTO> boardPG = boardRepository.findAll(BoardStatus.ACTIVE, pageable);
        List<BoardResponse.BoardPageDTO> content = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(content, boardPG);
    }

    public List<BoardResponse.BoardPageDTO> 최신핫한컨텐츠() {

        List<BoardResponse.BoardPageDTO> boardList = new ArrayList<>();
        PageRequest pageRequestNew = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        PageRequest pageRequestHot = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "clickCount"));

        boardList.addAll(boardRepository.findTop2ByNew(BoardStatus.ACTIVE, pageRequestNew));
        boardList.addAll(boardRepository.findTop2ByHot(BoardStatus.ACTIVE, pageRequestHot));
        return boardList;
    }
}
