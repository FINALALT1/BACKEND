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
public class MsgUtil {
    private final JavaMailSender javaMailSender;
    private final String header = "<img src=\"https://moneybridge.s3.ap-northeast-2.amazonaws.com/default/email_header.png\" />\n";
    @Getter
    public final String subjectReject = "[Money Bridge] 회원가입 승인 거절 안내드립니다.";
    @Getter
    public final String msgReject = ""
            + header
            + "<div style='margin:20px;'>"
            + "<h3> 안녕하세요. </h3>"
            + "<h3> Money Bridge 입니다.</h3>"
            + "<br>"
            + "<p>귀하의 회원가입 신청에 대한 승인이 거절되었음을 알려드립니다. \n"
            + "<br>"
            + "회원님의 제출한 명함 사진을 확인한 결과, 신원인증이 완료되지 않았으며, 회원가입을 승인할 수 없습니다.</p>"
            + "<br>"
            + "<br>"
            + "<p>회원가입 신청 정보는 모두 삭제 처리 되었으니 \n" +
            "재가입 신청을 원하시면 Money Bridge 메인에서 회원가입을 다시 신청하실 수 있습니다. </p>"
            + "<br>"
            + "<p>감사합니다. 관리자 드림 </p>"
            + "</div>";

    @Getter
    public final String subjectApprove = "[Money Bridge] 회원가입 승인 안내드립니다.";
    @Getter
    public final String msgApprove = ""
            + header
            + "<div style='margin:20px;'>"
            + "<h3> 안녕하세요. </h3>"
            + "<h3> Money Bridge 입니다.</h3>"
            + "<br>"
            + "<p>귀하의 회원가입 신청이 승인되었음을 알려드립니다. \n" +
            "회원님의 제출한 명함 사진을 확인한 결과, 신원인증이 완료되어 회원가입이 승인되었습니다.</p>"
            + "<br>"
            + "<br>"
            + "<p>지금부터 Money Bridge에서 PB로서 모든 활동을 시작하실 수 있습니다. \n <p>"
            + "<br>"
            + "<p>저희와 함께 여정을 시작하신 여러분들에게 진심으로 감사의 말씀을 전합니다. \n"
            + "<br>머니브릿지와 함께하면서 새로운 도약과 성장을 이루어 많은 성공을 경험하시기를 기대합니다.</p>"
            + "<br>"
            + "<p>관리자 드림 </p>"
            + "</div>";

    @Getter
    public final String subjectAuthenticate = "[Money Bridge] 이메일 인증코드 입니다";
    public String getMsgAuthenticate(String code) {
        return ""
                + header
                + "<div style='margin:20px;'>"
                + "<h2 style=\"color: #153445;\"> 안녕하세요. Money Bridge 입니다</h2>"
                + "<br>"
                + "<p>아래 인증코드를 Money Bridge 페이지의 입력 칸에 입력해주세요</p>"
                + "<br>"
                + "<br>"
                + "<div style=\"width: 890px; border: 1px solid #153445; display: flex; justify-content: center; align-items: center;\">"
                + "<br><div style=\"font-size: 130%;\" >"
                + "<h4 style=\"color: #3A7391;\"><br>인증 코드입니다. \n</h4>"
                + "CODE : <strong style=\"color: #91580F;\">"
                + code +"<br></strong><br>"
                + "</div>"
                + "</div>"
                + "<br>";
    }

    public MimeMessage createMessage(String email, String subject, String msg) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, email); // 메일 받을 사용자
            message.setSubject(subject); // 이메일 제목
            message.setText(msg, "utf-8", "html"); // 메일 내용, charset타입, subtype
            message.setFrom(new InternetAddress("sysmetic@naver.com", "Money-Bridge"));
            return message;
        } catch (Exception e) {
            throw new Exception500("이메일 작성 실패 " + e.getMessage());
        }
    }
}
