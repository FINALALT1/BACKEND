package kr.co.moneybridge.service;

import kr.co.moneybridge.core.dummy.MockDummyEntity;
import kr.co.moneybridge.dto.PageDTO;
import kr.co.moneybridge.dto.pb.PBResponse;
import kr.co.moneybridge.model.pb.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @Mock
    Pageable pageable;

    @Test
    void joinPB() {
    }

    @Test
    void getBookmarkPBs() {
    }

    @Test
    @DisplayName("PB 검색하기")
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
        assertThat(result.getList().size()).isEqualTo(pbPage.getContent().size());
        Mockito.verify(pbRepository, Mockito.times(1)).findByName(name, pageable);
    }

    @Test
    @DisplayName("거리순 PB리스트 가져오기(전문분야필터)")
    void getSpecialityPBWithDistance() {
        //given
        Double latitude = 112.1414;
        Double longitude = 87.1234;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        PBSpeciality speciality = PBSpeciality.BOND;

        //stub
        when(pbRepository.findByPBListSpeciality(speciality)).thenReturn(list);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getSpecialityPBWithDistance(latitude, longitude, speciality, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("거리순 PB리스트 가져오기(증권사필터)")
    void getCompanyPBWithDistance() {
        //given
        Double latitude = 112.1414;
        Double longitude = 87.1234;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Long companyId = 1L;

        //stub
        when(pbRepository.findByPBListCompany(anyLong())).thenReturn(list);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getCompanyPBWithDistance(latitude, longitude, companyId, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("거리순 전체PB리스트 가져오기")
    void getPBWithDistance() {
        //given
        Double latitude = 112.1414;
        Double longitude = 87.1234;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);

        //stub
        when(pbRepository.findAllPB()).thenReturn(list);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getPBWithDistance(latitude, longitude, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("경력순 PB리스트 가져오기(전문분야필터)")
    void getSpecialityPBWithCareer() {
        //given
        PBSpeciality speciality = PBSpeciality.US_STOCK;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl<>(list, pageable, list.size());

        //stub
        when(pbRepository.findBySpecialityOrderedByCareer(speciality, pageable)).thenReturn(pbPG);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getSpecialityPBWithCareer(speciality, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("경력순 PB리스트 가져오기(증권사필터)")
    void getCompanyPBWithCareer() {
        //given
        Long companyId = 1L;
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl<>(list, pageable, list.size());

        //stub
        when(pbRepository.findByCompanyIdOrderedByCareer(companyId, pageable)).thenReturn(pbPG);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getCompanyPBWithCareer(companyId, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }

    @Test
    @DisplayName("경력순 전체 PB리스트 가져오기")
    void getPBWithCareer() {
        //given
        PBResponse.PBPageDTO pbPageDTO1 = new PBResponse.PBPageDTO(pb, branch, company);
        PBResponse.PBPageDTO pbPageDTO2 = new PBResponse.PBPageDTO(pb, branch, company);
        List<PBResponse.PBPageDTO> list = Arrays.asList(pbPageDTO1, pbPageDTO2);
        Page<PBResponse.PBPageDTO> pbPG = new PageImpl<>(list, pageable, list.size());

        //stub
        when(pbRepository.findAllPBWithCareer(pageable)).thenReturn(pbPG);

        //when
        PageDTO<PBResponse.PBPageDTO> result = pbService.getPBWithCareer(pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(list.size());
        assertThat(result.getList()).isEqualTo(list);
    }
}