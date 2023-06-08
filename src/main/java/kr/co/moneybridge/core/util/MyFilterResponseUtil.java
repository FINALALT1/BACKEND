package kr.co.moneybridge.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.moneybridge.dto.ResponseDTO;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilterResponseUtil {
    public static void unAuthorized(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json; charset=utf-8");
        ResponseDTO<?> responseDto = new ResponseDTO<>(HttpStatus.UNAUTHORIZED, "unAuthorized", e.getMessage());
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(responseDto);
//        resp.getWriter().println(responseBody);

        PrintWriter writer = resp.getWriter();
        writer.println(responseBody);
        writer.flush();
        writer.close();
    }

    public static void forbidden(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(403);
        resp.setContentType("application/json; charset=utf-8");
        ResponseDTO<?> responseDto = new ResponseDTO<>(HttpStatus.FORBIDDEN, "forbidden", e.getMessage());
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(responseDto);
        resp.getWriter().println(responseBody);
    }

    public static void serverError(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(500);
        resp.setContentType("application/json; charset=utf-8");
        ResponseDTO<?> responseDto = new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, "serverError", e.getMessage());
        ObjectMapper om = new ObjectMapper();
        String responseBody = om.writeValueAsString(responseDto);

        // 이미 응답에 대한 쓰기 작업이 시작되었다면, 여기서 오류가 발생.
        // 이 경우, 이 메소드를 호출하기 전에 쓰기 작업이 종료되도록 코드 작성.
        PrintWriter writer = resp.getWriter();
        writer.println(responseBody);
        writer.flush();  // PrintWriter의 버퍼를 비웁니다.
        writer.close();  // PrintWriter를 닫습니다.
    }
}
