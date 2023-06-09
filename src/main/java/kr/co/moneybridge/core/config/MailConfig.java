package kr.co.moneybridge.core.config;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Value;
=======
>>>>>>> 2b58bfa (Feat: 회원가입시 이메일 인증 API)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
<<<<<<< HEAD
    @Value("${EMAIL_PASSWORD}")
    private String emailPassword;

=======
>>>>>>> 2b58bfa (Feat: 회원가입시 이메일 인증 API)
    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.naver.com"); // smtp 서버 주소
        javaMailSender.setUsername("moneybridge@naver.com");
<<<<<<< HEAD
        javaMailSender.setPassword(emailPassword);
=======
        javaMailSender.setPassword("finalback1234!!");
>>>>>>> 2b58bfa (Feat: 회원가입시 이메일 인증 API)
        javaMailSender.setPort(465);
        javaMailSender.setJavaMailProperties(getMailProperties()); // 메일 인증서버 정보 가져오기
        return javaMailSender;
    }

    private Properties getMailProperties(){
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp"); // 프로토콜(smtp) 설정
        properties.setProperty("mail.smtp.auth", "true"); // smtp 인증 사용함
        properties.setProperty("mail.smtp.starttls.enable", "true"); // smtp StartTLS 사용 (SMTP 연결을 암호화하는 프로토콜)
        properties.setProperty("mail.debug", "true"); //  디버그 모드를 설정 - 자세한 로그가 출력
        properties.setProperty("mail.smtp.ssl.trust","smtp.naver.com"); //  SSL 인증을 사용할 SMTP 서버의 호스트를 설정(SSL 인증 서버)는 smtp.naver.com
        properties.setProperty("mail.smtp.ssl.enable","true"); // SSL 사용
        return properties;
    }
}
