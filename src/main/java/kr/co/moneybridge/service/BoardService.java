package kr.co.moneybridge.service;

import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public PageDTO<BoardResponse.BoardOutDTO> 컨텐츠검색(String title, Pageable pageable) {
        Page<BoardResponse.BoardOutDTO> boardPG = boardRepository.findByTitle(title, pageable);
        List<BoardResponse.BoardOutDTO> content = boardPG.getContent().stream().collect(Collectors.toList());
        return new PageDTO<>(content, boardPG);
    }
}
