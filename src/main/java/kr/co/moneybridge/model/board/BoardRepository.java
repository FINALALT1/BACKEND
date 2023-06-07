package kr.co.moneybridge.model.board;

import kr.co.moneybridge.dto.board.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, p, c) " +
            "FROM Board b " +
            "JOIN b.pb p JOIN p.branch bh JOIN bh.company c " +
            "WHERE b.title LIKE CONCAT('%', :title, '%') AND b.status = :status")
    Page<BoardResponse.BoardPageDTO> findByTitle(@Param("title") String title, @Param("status") BoardStatus status, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, p, c) " +
            "FROM Board b JOIN b.pb p JOIN p.branch bh JOIN bh.company c " +
            "WHERE b.status = :status")
    Page<BoardResponse.BoardPageDTO> findAll(@Param("status") BoardStatus status, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, p, c) " +
            "FROM Board b JOIN b.pb p JOIN p.branch bh JOIN bh.company c " +
            "WHERE b.status = :status ORDER BY b.id DESC")
    List<BoardResponse.BoardPageDTO> findTop2ByNew(@Param("status") BoardStatus status, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, p, c) " +
            "FROM Board b JOIN b.pb p JOIN p.branch bh JOIN bh.company c " +
            "WHERE b.status = :status ORDER BY b.clickCount DESC")
    List<BoardResponse.BoardPageDTO> findTop2ByHot(@Param("status") BoardStatus status, Pageable pageable);

    @Query("SELECT NEW kr.co.moneybridge.dto.board.BoardResponse$BoardDetailDTO(b, pb) " +
            "FROM Board b " +
            "JOIN PB pb ON b.pb.id = pb.id " +
            "WHERE b.id = :boardId AND b.status = :status")
    BoardResponse.BoardDetailDTO findBoardWithPBReply(@Param("boardId") Long boardId, @Param("status") BoardStatus status);


}
