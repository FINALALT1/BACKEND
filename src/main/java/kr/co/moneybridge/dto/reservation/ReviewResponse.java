package kr.co.moneybridge.dto.reservation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.co.moneybridge.model.reservation.Review;
import kr.co.moneybridge.model.reservation.StyleStyle;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ReviewResponse {

    @ApiModel(description = "PB 최신리뷰 3개 데이터")
    @Getter
    @Setter
    public static class ReviewOutDTO {
        @ApiModelProperty(example = "1", value = "리뷰 id")
        private Long reviewId;
        @ApiModelProperty(example = "김투자자", value = "투자자이름")
        private String userName;
        @ApiModelProperty(example = "친절해요", value = "후기")
        private String content;
        @ApiModelProperty
        private List<StyleOutDTO> list; //set해줘야함

        public ReviewOutDTO(User user, Review review) {
            this.reviewId = review.getId();
            this.userName = user.getName();
            this.content = review.getContent();
        }
    }

    @Getter
    public static class StyleOutDTO {
        private StyleStyle style;

        public StyleOutDTO(StyleStyle style) {
            this.style = style;
        }
    }

    @Getter
    @Setter
    public static class ReviewListOutDTO {
        private List<ReviewResponse.ReviewOutDTO> list;

        public ReviewListOutDTO(List<ReviewOutDTO> list) {
            this.list = list;
        }
    }
}
