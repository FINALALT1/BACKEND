package kr.co.moneybridge.dto.pb;

import kr.co.moneybridge.model.Member;
import kr.co.moneybridge.model.Role;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.user.User;
import lombok.Getter;
import lombok.Setter;

public class PBResponse {
    @Setter
    @Getter
    public static class JoinOutDTO {
        private Long id;

        public JoinOutDTO(PB pb) {
            this.id = pb.getId();
        }
    }
}
