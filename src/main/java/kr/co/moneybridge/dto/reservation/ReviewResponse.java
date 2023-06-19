package kr.co.moneybridge.dto.reservation;

import kr.co.moneybridge.model.reservation.Review;
import kr.co.moneybridge.model.reservation.StyleStyle;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ReviewResponse {

    @Getter
    @Setter
    public static class ReviewOutDTO {
        private Long reviewId;
        private String userName;
        private String content;
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
