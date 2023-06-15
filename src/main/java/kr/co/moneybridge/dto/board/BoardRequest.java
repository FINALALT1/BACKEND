package kr.co.moneybridge.dto.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class BoardRequest {

    @Getter
    @Setter
    public static class BoardInDTO {

        @ApiModelProperty(example = "제목입니다.")
        @NotEmpty
        private String title;
        @ApiModelProperty(example = "내용입니다.")
        @Column(columnDefinition = "TEXT")
        private String content;
        @ApiModelProperty(example = "태그1")
        @Size(max = 30)
        private String tag1;
        @ApiModelProperty(example = "태그2")
        @Size(max = 30)
        private String tag2;
        @ApiModelProperty(example = "thumbnail.png")
        private String thumbnail;

    }
}
