package kr.co.moneybridge.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.core.annotation.ValidStyleStyles;
import kr.co.moneybridge.model.reservation.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationRequest {
    // validation은 controller에서 수행
    @ApiModel
    @Getter
    @Setter
    public static class ApplyDTO {
        @ApiModelProperty(example = "PROFIT")
        private ReservationGoal goal;

        @ApiModelProperty(example = "VISIT")
        private ReservationType reservationType;

        @ApiModelProperty(example = "BRANCH")
        private LocationType locationType;

        @ApiModelProperty(example = "2023-06-01T10:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime candidateTime1;

        @ApiModelProperty(example = "2023-06-01T11:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime candidateTime2;

        @ApiModelProperty(example = "잘 부탁드립니다.")
        private String question;

        @ApiModelProperty(example = "홍길동")
        private String userName;

        @ApiModelProperty(example = "01012345678")
        private String userPhoneNumber;

        @ApiModelProperty(example = "asdf1234@gmail.com")
        private String userEmail;
    }

    // validation은 controller에서 수행
    @ApiModel
    @Getter
    @Setter
    public static class UpdateDTO {
        @ApiModelProperty(example = "2023-06-01T11:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime time;

        @ApiModelProperty(example = "VISIT")
        private ReservationType type;

        @ApiModelProperty(example = "BRANCH")
        private LocationType category;
    }

    @ApiModel
    @Getter
    @Setter
    public static class ConfirmDTO {
        @ApiModelProperty(example = "2023년 6월 1일 AM 9시 20분")
        @Pattern(regexp = "^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (AM|PM) \\d{1,2}시 \\d{1,2}분$",
                message = "형식에 맞춰 입력해주세요.")
        @NotNull
        private String time;
    }

    @ApiModel
    @Getter
    @Setter
    public static class ReviewDTO {
        @ApiModelProperty(example = "1")
        @NotNull
        private Long reservationId;

        @ApiModelProperty(example = "2")
        @Enumerated(EnumType.STRING)
        @NotNull
        private ReviewAdherence adherence;

        @ApiModelProperty(dataType = "List", example = "[\"KIND\",\"METICULOUS\",\"PROFESSIONAL\"]")
        @ValidStyleStyles
        @NotNull
        private List<StyleStyle> styleList;

        @ApiModelProperty(example = "도움이 많이 되었습니다.")
        private String content;
    }

    // validation은 controller에서 수행
    @ApiModel
    @Getter
    @Setter
    public static class UpdateTimeDTO {
        @ApiModelProperty(example = "09:00")
        private String consultStart;

        @ApiModelProperty(example = "09:00")
        private String consultEnd;

        @ApiModelProperty(example = "월요일 13:00 제외")
        private String consultNotice;
    }
}
