package kr.co.moneybridge.core.annotation;

import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SwaggerResponses {
    private static final String BAD_REQUEST = "{\n" +
            "&nbsp;&nbsp;\"status\": 400,\n" +
            "&nbsp;&nbsp;\"msg\": \"badRequest\",\n" +
            "&nbsp;&nbsp;\"data\": {\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"key\": \"변수명\",\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"value\": \"에러 메세지\"\n" +
            "&nbsp;&nbsp;}\n" +
            "}";
    private static final String UNAUTHORIZED = "{\n" +
            "&nbsp;&nbsp;\"status\": \"unAuthorized\",\n" +
            "&nbsp;&nbsp;\"msg\": 401,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private static final String FORBIDDEN = "{\n" +
            "&nbsp;&nbsp;\"status\": \"forbidden\",\n" +
            "&nbsp;&nbsp;\"msg\": 403,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private static final String NOT_FOUND = "{\n" +
            "&nbsp;&nbsp;\"status\": \"notFound\",\n" +
            "&nbsp;&nbsp;\"msg\": 404,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";
    private static final String INTERNAL_SERVER_ERROR = "{\n" +
            "&nbsp;&nbsp;\"status\": \"unknownServerError\",\n" +
            "&nbsp;&nbsp;\"msg\": 500,\n" +
            "&nbsp;&nbsp;\"data\": \"에러 메세지\"\n" +
            "}";

    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultApiResponses {
    }

    @ApiOperation(value = "지점 등록")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AddBranch {
    }

    @ApiOperation(value = "대댓글 강제 삭제")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", example = "1", value = "rereply(대댓글) id")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DeleteRereply {
    }

    @ApiOperation(value = "댓글 강제 삭제")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", example = "1", value = "reply(댓글) id")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DeleteReply {
    }

    @ApiOperation(value = "콘텐츠 강제 삭제")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", example = "1", value = "board(콘텐츠) id")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DeleteBoard {
    }

    @ApiOperation(value = "로그인 계정 정보 받아오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetAccount {
    }

    @ApiOperation(value = "상담 현황 페이지 전체 가져오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", example = "0", value = "curPage 번호")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetReservations {
    }

    @ApiOperation(value = "해당 투자자 강제 탈퇴")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", example = "1", value="탈퇴시키려는 투자자의 id")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ForceWithdrawUser {
    }

    @ApiOperation(value = "해당 PB 강제 탈퇴")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", example = "1", value="탈퇴시키려는 pb의 id")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ForceWithdrawPB {
    }

    @ApiOperation(value = "해당 투자자를 관리자로 등록/취소")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", example = "1", value="user의 id"),
            @ApiImplicitParam(name="admin", example = "true", value = "관리자 등록하려면 true, 취소하려면 false")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AuthorizeAdmin {
    }

    @ApiOperation(value = "회원 관리 페이지 전체 가져오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userPage", example = "0", value = "userPage의 curPage 번호"),
            @ApiImplicitParam(name = "pbPage", example = "0", value = "pbPage의 curPage 번호")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMembers {
    }

    @ApiOperation(value = "해당 PB 승인/승인 거부")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", example = "1", value="pb의 id"),
            @ApiImplicitParam(name="approve", example = "true", value = "승인하려면 true, 거절하려면 false")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ApprovePB {
    }

    @ApiOperation(value = "PB 회원 가입 요청 승인 페이지 전체 가져오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetPBPending {
    }

    @ApiOperation(value = "현재 나의 상담 가능 시간 불러오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMyConsultTime {
    }

    @ApiOperation(value = "나의 후기 하나 가져오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMyReview {
    }

    @ApiOperation(value = "공지사항 목록 가져오기")
    @ApiResponses({
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", example = "0", value = "curPage 번호")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetNotice {
    }

    @ApiOperation(value = "자주 묻는 질문 목록 가져오기")
    @ApiResponses({
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", example = "0", value = "curPage 번호")})
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetFAQ {
    }

    @ApiOperation(value = "나의 투자 성향 분석페이지 하단의 맞춤 PB리스트", notes = "<b>투자 성향에 따라 맞춤 분야별 PB리스트 필터링 3개</b>\n<br>" +
            "공격형 => 채권, 미국주식, 한국주식, 펀드, 파생, ETF, 랩\n" +
            "적극형 => 채권, 미국주식, 한국주식, 펀드, ETF, 랩\n" +
            "위험중립형 => 채권, 펀드, 랩\n" +
            "안정추구형 => 채권, 펀드, 랩\n" +
            "안정형 => 채권, 펀드, 랩\n<br>" +
            "해당 분야에 속하는 PB중 랜덤으로 3명 리스트")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMyPropensityPB {
    }

    @ApiOperation(value = "투자자 성향 체크")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestPropensity {
    }

    @ApiOperation(value = "PB 마이페이지 가져오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMyPagePB {
    }

    @ApiOperation(value = "투자자 마이페이지 가져오기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMyPageUser {
    }

    @ApiOperation(value = "지점 검색")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SearchBranch {
    }

    @ApiOperation(value = "로그아웃")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Logout {
    }

    @ApiOperation(value = "토큰 재발급", notes = "<b>401 에러 + data가 \"Access token expired\"일 때, 이 API 호출</b>\n<br>" +
            "인증이 필요한 요청에서 응답 메세지가 다음과 같을 때 이 요청을 보낸다.\n"+
            "{\n" +
            "&nbsp;&nbsp;\"status\": 401,\n" +
            "&nbsp;&nbsp;\"msg\": \"unAuthorized\",\n" +
            "&nbsp;&nbsp;\"data\": \"Access token expired\"\n" +
            "}\n<br><br>" +
            "<b>성공시, 응답 데이터</b>\n<br>" +
            "Authorization: Bearer <JWT 토큰>&nbsp;<font color=\"#C0C0C0\">// 액세스 토큰</font>\n" +
            "Set-Cookie: refreshToken=<JWT 토큰>; HttpOnly; Path=/&nbsp;<font color=\"#C0C0C0\">// 리프레시 토큰</font>\n<br>" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {}}\n" +
            "}")
    @ApiResponses({
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Reissue {
    }

    @ApiOperation(value = "로그인", notes = "<b>성공시, 응답 데이터</b>\n<br>" +
            "Authorization: Bearer <JWT 토큰>&nbsp;<font color=\"#C0C0C0\">// 액세스 토큰</font>\n" +
            "Set-Cookie: refreshToken=<JWT 토큰>; HttpOnly; Path=/&nbsp;<font color=\"#C0C0C0\">// 리프레시 토큰</font>\n<br>" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"id\": 1,&nbsp;<font color=\"#C0C0C0\">// 해당 투자자의 id</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"code\": \"YFOEC1AC\"&nbsp;<font color=\"#C0C0C0\">// 관리자가 아닐때는 null</font>\n" +
            "&nbsp;&nbsp;}\n" +
            "}")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Login {
    }

    @ApiOperation(value = "투자자 회원가입", notes = "<b>성공시, 응답데이터</b>\n<br>" +
            "Authorization: Bearer <JWT 토큰>&nbsp;<font color=\"#C0C0C0\">// 액세스 토큰</font>\n" +
            "Set-Cookie: refreshToken=<JWT 토큰>; HttpOnly; Path=/&nbsp;<font color=\"#C0C0C0\">// 리프레시 토큰</font>\n<br>" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"id\": 1,&nbsp;<font color=\"#C0C0C0\">// 해당 투자자의 id</font>\n" +
            "&nbsp;&nbsp;}\n" +
            "}")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JoinUser {
    }

    @ApiOperation(value = "탈퇴", notes = "연관데이터 전부 즉시 삭제")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Withdraw {
    }

    @ApiOperation(value = "이메일 인증")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Email {
    }

    @ApiOperation(value = "비밀번호 찾기시 이메일 인증")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Password {
    }

    @ApiOperation(value = "이메일 찾기")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FindEmail {
    }

    @ApiOperation(value = "비밀번호 재설정")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UpdatePassword {
    }

    @ApiOperation(value = "개인정보 수정시 비밀번호 확인")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CheckPassword {
    }

    @ApiOperation(value = "개인 정보 가져오기", notes = "i 소문자")
    @ApiResponses({
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMyInfo {
    }

    @ApiOperation(value = "개인 정보 수정", notes = "i 소문자")
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UpdateMyInfo {
    }

    @ApiOperation(value = "PB 회원가입", notes = "<b>joinInDTO 예시 및 설명</b>\n<br>{\n" +
            "&nbsp;&nbsp;\"email\": \"investor2@naver.com\",&nbsp;<font color=\"#C0C0C0\">// @ 포함해야함 + 30바이트 이내</font>\n" +
            "&nbsp;&nbsp;\"password\": \"abcd5678\",&nbsp;<font color=\"#C0C0C0\">// 영문(대소문자), 숫자 포함해서 8자 이상, 공백없이</font>\n" +
            "&nbsp;&nbsp;\"name\": \"김피비\",&nbsp;<font color=\"#C0C0C0\">// 20바이트 이내</font>\n" +
            "&nbsp;&nbsp;\"phoneNumber\": \"01012345678\",&nbsp;<font color=\"#C0C0C0\">// -없이 + (정규식: ^01(?:0|1|[6-9])(?:\\\\d{3}|\\\\d{4})\\\\d{4}$)</font>\n" +
            "&nbsp;&nbsp;\"branchId\": 1,&nbsp;<font color=\"#C0C0C0\">// 지점id</font>\n" +
            "&nbsp;&nbsp;\"career\": 5,&nbsp;<font color=\"#C0C0C0\">// 경력(연차) + 0 또는 양수만 가능</font>\n" +
            "&nbsp;&nbsp;\"speciality1\": \"FUND\",&nbsp;<font color=\"#C0C0C0\">// 전문분야(enum)</font>\n" +
            "&nbsp;&nbsp;\"speciality2\": \"US_STOCK\",&nbsp;<font color=\"#C0C0C0\">// 없어도 됨</font>\n" +
            "&nbsp;&nbsp;\"agreements\": [&nbsp;<font color=\"#C0C0C0\">// 없어도 가능</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;{\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"title\": \"돈줄 이용약관 동의\",&nbsp;<font color=\"#C0C0C0\">// 약관명</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"type\": \"REQUIRED\",&nbsp;<font color=\"#C0C0C0\">// 약관 종류(enum)</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"isAgreed\": true&nbsp;<font color=\"#C0C0C0\">// 동의 여부</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;},\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;{\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"title\": \"마케팅 정보 수신 동의\",\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"type\": \"OPTIONAL\",\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"isAgreed\": true\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;}\n"+
            "&nbsp;&nbsp;}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="businessCard", value="명함 사진"),
            @ApiImplicitParam(name="joinInDTO", value = "joinInDTO")})
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = BAD_REQUEST),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JoinPB {
    }

    @ApiOperation(value = "증권사 리스트 가져오기", notes = "메인페이지, 회원가입시 사용.\n" +
            "<b>includeLogo=true면 증권사 로고 포함(디폴트) => 응답 데이터 예시</b>\n" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"list\": [\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"id\": 1,\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"logo\": \"logo.png\",\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"name\": \"미래에셋증권\"\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},&nbsp;<font color=\"#C0C0C0\">// ... 실제로는 30개</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;]\n" +
            "&nbsp;&nbsp;}\n" +
            "}\n<br>" +
            "<b>includeLogo=false면 증권사 로고 불포함 => 응답 데이터 예시</b>\n" +
            "{\n" +
            "&nbsp;&nbsp;\"status\": 200,\n" +
            "&nbsp;&nbsp;\"msg\": \"ok\",\n" +
            "&nbsp;&nbsp;\"data\": {\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"list\": [\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"id\": 1,\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"name\": \"미래에셋증권\"\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},&nbsp;<font color=\"#C0C0C0\">// ... 실제로는 30개</font>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;]\n" +
            "&nbsp;&nbsp;}\n" +
            "}")
    @ApiResponses({
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @ResponseStatus(HttpStatus.OK)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetCompanies {
    }

    @ApiResponses({
            @ApiResponse(code = 401,
                    message = UNAUTHORIZED),
            @ApiResponse(code = 403,
                    message = FORBIDDEN),
            @ApiResponse(code = 404,
                    message = NOT_FOUND),
            @ApiResponse(code = 500,
                    message = INTERNAL_SERVER_ERROR)
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ApiResponsesWithout400 {
    }
}
