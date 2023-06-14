package kr.co.moneybridge.model.board;

import kr.co.moneybridge.dto.board.BoardResponse;
import kr.co.moneybridge.model.reservation.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, p, c) " +
            "FROM Board b " +
            "JOIN b.pb p JOIN p.branch bh JOIN bh.company c " +
            "WHERE b.title LIKE CONCAT('%', :title, '%') AND b.status = :status")
    Page<BoardResponse.BoardPageDTO> findByTitle(@Param("title") String title, @Param("status") BoardStatus status, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, p, c) " +
            "FROM Board b " +
            "JOIN b.pb p JOIN p.branch bh JOIN bh.company c " +
            "WHERE p.name LIKE CONCAT('%', :name, '%') AND b.status = :status")
    Page<BoardResponse.BoardPageDTO> findByPbName(@Param("name") String name, @Param("status") BoardStatus status, Pageable pageable);

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
    Optional<BoardResponse.BoardDetailDTO> findBoardWithPBReply(@Param("boardId") Long boardId, @Param("status") BoardStatus status);

    @Query("SELECT b FROM Board b WHERE b.pb.id = :pbId AND b.status = :status")
    List<Board> findBoardsByPbId(@Param("pbId") Long pbId, @Param("status") BoardStatus status);

    @Query("SELECT b FROM Board b JOIN PB pb ON b.pb.id = pb.id WHERE b.id = :boardId AND pb.id = :pbId")
    Optional<Board> findByIdAndPbId(@Param("boardId") Long boardId, @Param("pbId") Long pbId);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, pb, c) " +
            "FROM Board b " +
            "JOIN PB pb ON b.pb = pb " +
            "JOIN Branch br ON pb.branch = br " +
            "JOIN Company c ON br.company = c " +
            "JOIN BoardBookmark bb ON bb.board = b " +
            "WHERE bb.bookmarkerId = :userId AND bb.bookmarkerRole = 'USER'")
    Page<BoardResponse.BoardPageDTO> findBookmarkBoardsWithUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new kr.co.moneybridge.dto.board.BoardResponse$BoardPageDTO(b, pb, c) " +
            "FROM Board b " +
            "JOIN PB pb ON b.pb = pb " +
            "JOIN Branch br ON pb.branch = br " +
            "JOIN Company c ON br.company = c " +
            "JOIN BoardBookmark bb ON bb.board = b " +
            "WHERE bb.bookmarkerId = :pbId AND bb.bookmarkerRole = 'PB'")
    Page<BoardResponse.BoardPageDTO> findBookmarkBoardsWithPbId(@Param("pbId") Long pbId, Pageable pageable);
    @Modifying
    void deleteById(Long boardId);

    @Modifying
    @Query("delete from Board b where b.pb.id = :pbId")
    void deleteByPBId(@Param("pbId") Long pbId);

    @Query("select b from Board b where b.pb.id = :pbId")
    List<Board> findAllByPBId(@Param("pbId") Long pbId);


}
