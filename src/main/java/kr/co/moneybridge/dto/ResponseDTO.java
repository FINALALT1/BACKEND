package kr.co.moneybridge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@ApiModel
@Getter
public class ResponseDTO<T> {
    @ApiModelProperty(example = "200")
    private Integer status; // 에러시에 의미 있음.

    @ApiModelProperty(example = "ok")
    private String msg; // 에러시에 의미 있음. ex) badRequest

    @ApiModelProperty
    private T data; // 에러시에는 구체적인 에러 내용 ex) username이 입력되지 않았습니다

    public ResponseDTO() {
        this.status = HttpStatus.OK.value();
        this.msg = "ok";
    }

    public ResponseDTO(T data) {
        this.status = HttpStatus.OK.value();
        this.msg = "ok";
        this.data = data; // 응답할 데이터 바디
    }

    public ResponseDTO(HttpStatus httpStatus, String msg, T data) {
        this.status = httpStatus.value();
        this.msg = msg; // 에러 제목
        this.data = data; // 에러 내용
    }
}
