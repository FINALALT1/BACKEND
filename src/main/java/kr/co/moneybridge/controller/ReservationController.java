package kr.co.moneybridge.controller;

import kr.co.moneybridge.core.annotation.MyLog;
import kr.co.moneybridge.dto.ResponseDTO;
import kr.co.moneybridge.dto.reservation.ReservationResponse;
import kr.co.moneybridge.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    // PB 상담 예약 가능 시간 정보 + 지점 위치 불러오기 API
    @MyLog
    @GetMapping("/user/reservation/{pbId}")
    public ResponseEntity<?> getReservationBase(@PathVariable Long pbId) {
        ReservationResponse.ReservationBaseOutDTO reservationBaseOutDTO = reservationService.getReservationBase(pbId);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(reservationBaseOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
