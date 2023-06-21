package kr.co.moneybridge.core.exception;

import kr.co.moneybridge.dto.ResponseDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;


// 성공했지만 반환할 게 없을 때
@Getter
public class Exception200 extends RuntimeException {
    public Exception200(String message) {
        super(message);
    }

    public ResponseDTO<?> body(){
        return new ResponseDTO<>(HttpStatus.OK, "ok_but", getMessage());
    }

    public HttpStatus status(){
        return HttpStatus.OK;
    }
}