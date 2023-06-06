package kr.co.moneybridge.model.board;

import kr.co.moneybridge.dto.board.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardOutDTO(b, p, c) FROM Board b JOIN b.pb p JOIN p.branch bh JOIN bh.company c WHERE b.title LIKE CONCAT('%', :title, '%')")
    Page<BoardResponse.BoardOutDTO> findByTitle(@Param("title") String title, Pageable pageable);
}
