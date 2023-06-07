package kr.co.moneybridge.dto.reservation;

import kr.co.moneybridge.model.reservation.LocationType;
import kr.co.moneybridge.model.reservation.ReservationGoal;
import kr.co.moneybridge.model.reservation.ReservationType;
import lombok.Getter;
import lombok.Setter;

public class ReservationRequest {
    // validation은 controller에서 수행
    @Getter
    @Setter
    public static class ApplyReservationInDTO {
        private ReservationGoal goal1;
        private ReservationGoal goal2;
        private ReservationType reservationType;
        private LocationType locationType;
        private String locationName;
        private String locationAddress;
        private String candidateTime1;
        private String candidateTime2;
        private String question;
        private String userName;
        private String userPhoneNumber;
        private String userEmail;
    }
}
