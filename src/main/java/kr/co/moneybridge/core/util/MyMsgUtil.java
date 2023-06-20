package kr.co.moneybridge.core.util;

import kr.co.moneybridge.core.exception.Exception500;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Component
public class MyMsgUtil {
    private final JavaMailSender javaMailSender;
    @Getter
    public final String subjectReject = "[Money Bridge] 회원가입 승인 거절 안내드립니다.";
    @Getter
    public final String msgReject = ""
            // += "<img src=../resources/static/image/emailheader.jpg />"; // header image
            + "<div style='margin:20px;'>"
            + "<h3> 안녕하세요. </h3>"
            + "<h3> Money Bridge 입니다.</h3>"
            + "<br>"
            + "<p>귀하의 회원가입 신청에 대한 승인이 거절되었음을 알려드립니다. \n" +
            "회원님의 제출한 명함 사진을 확인한 결과, 신원인증이 완료되지 않았으며, 회원가입을 승인할 수 없습니다.</p>"
            + "<br>"
            + "<br>"
            + "<p>회원가입 신청 정보는 모두 삭제 처리 되었으니 \n" +
            "재가입 신청을 원하시면 Money Bridge 메인에서 회원가입을 다시 신청하실 수 있습니다. </p>"
            + "<br>"
            + "<p>감사합니다. 관리자 드림 </p>"
            + "</div>";
    // msg += "<img src=../resources/static/image/emailfooter.jpg />"; // footer image;

    @Getter
    public final String subjectApprove = "[Money Bridge] 회원가입 승인 안내드립니다.";
    @Getter
    public final String msgApprove = ""
            // += "<img src=../resources/static/image/emailheader.jpg />"; // header image
            + "<div style='margin:20px;'>"
            + "<h3> 안녕하세요. </h3>"
            + "<h3> Money Bridge 입니다.</h3>"
            + "<br>"
            + "<p>귀하의 회원가입 신청이 승인되었음을 알려드립니다. \n" +
            "회원님의 제출한 명함 사진을 확인한 결과, 신원인증이 완료되어 회원가입을 승인하였습니다.</p>"
            + "<br>"
            + "<br>"
            + "<p>지금부터 Money Bridge에서 PB로서 모든 활동을 시작하실 수 있습니다. \n <p>"
            + "<br>"
            + "<p>저희와 함께 여정을 시작하신 여러분들에게 진심으로 감사의 말씀을 전합니다. \n"
            + "머니브릿지와 함께하면서 새로운 도약과 성장을 이루어 많은 성공을 경험하시기를 기대합니다.</p>"
            + "<br>"
            + "<p>관리자 드림 </p>"
            + "</div>";
    // msg += "<img src=../resources/static/image/emailfooter.jpg />"; // footer image;

    @Getter
    public final String subjectAuthenticate = "[Money Bridge] 이메일 인증코드 입니다";
    public String getMsgAuthenticate(String code) {
        return ""
                // + "<img src=../resources/static/image/emailheader.jpg />"; // header image
                + "<div style='margin:20px;'>"
                + "<h1> 안녕하세요. </h1>"
                + "<br>"
                + "<h1> Money Bridge 입니다</h1>"
                + "<br>"
                + "<p>아래 인증코드를 Money Bridge 페이지의 입력 칸에 입력해주세요</p>"
                + "<br>"
                + "<br>"
                + "<div align='center' style='border:1px solid black; font-family:verdana';>"
                + "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>"
                + "<div style='font-size:130%'>"
                + "CODE : <strong>"
                + code + "</strong><div><br/> "
                + "</div>";
        // msg += "<img src=../resources/static/image/emailfooter.jpg />"; // footer image
    }

    public MimeMessage createMessage(String email, String subject, String msg) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, email); // 메일 받을 사용자
            message.setSubject(subject); // 이메일 제목
            message.setText(msg, "utf-8", "html"); // 메일 내용, charset타입, subtype
            message.setFrom(new InternetAddress("moneybridge@naver.com", "Money-Bridge"));
            return message;
        } catch (Exception e) {
            throw new Exception500("이메일 작성 실패 " + e.getMessage());
        }
    }
}
