package kr.co.moneybridge.core.util;

import kr.co.moneybridge.model.reservation.LocationType;
import kr.co.moneybridge.model.reservation.ReservationGoal;
import kr.co.moneybridge.model.reservation.ReservationType;
import kr.co.moneybridge.model.reservation.StyleStyle;

public class EnumUtil {
    public static boolean isValidReservationGoal(ReservationGoal goal) {
        for (ReservationGoal valid : ReservationGoal.values()) {
            if (valid == goal) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidReservationType(ReservationType type) {
        for (ReservationType valid : ReservationType.values()) {
            if (valid == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidLocationType(LocationType type) {
        for (LocationType valid : LocationType.values()) {
            if (valid == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidStyleStyle(StyleStyle style) {
        for (StyleStyle valid : StyleStyle.values()) {
            if (valid == style) {
                return true;
            }
        }
        return false;
    }
}
