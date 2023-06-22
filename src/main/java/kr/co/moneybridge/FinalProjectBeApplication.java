package kr.co.moneybridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FinalProjectBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProjectBeApplication.class, args);
    }

}
