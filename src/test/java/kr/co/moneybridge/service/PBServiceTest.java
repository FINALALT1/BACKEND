package kr.co.moneybridge.service;

import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.pb.Branch;
import kr.co.moneybridge.model.pb.Company;
import kr.co.moneybridge.model.pb.PB;
import kr.co.moneybridge.model.pb.PBRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PBServiceTest extends MockDummyEntity {

    @InjectMocks
    PBService pbService;
    @Mock
    PBRepository pbRepository;
    @Mock
    PB pb;
    @Mock
    Branch branch;
    @Mock
    Company company;

    @Test
    void joinPB() {
    }

    @Test
    void getBookmarkPBs() {
    }

    @Test
    void getPB() {
    }

    @Test
    void getPBWithName() {
        //given
        String name = "김피비";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> pbList = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPage = new PageImpl<>(pbList, pageable, pbList.size());

        //stub
        when(pbRepository.findByName(name, pageable)).thenReturn(pbPage);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getPBWithName(name, pageable);

        //then
        Assertions.assertThat(result.getList().size()).isEqualTo(pbPage.getContent().size());
        Mockito.verify(pbRepository, Mockito.times(1)).findByName(name, pageable);
    }
}