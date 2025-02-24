package com.flash21.accounting.outsourcing.domain.repository;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import com.flash21.accounting.outsourcing.domain.entity.OutsourcingStatus;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutsourcingRepositoryTest {

    @Mock
    private OutsourcingRepository outsourcingRepository;

    private User admin;
    private Contract contract;
    private DetailContract detailContract;
    private Correspondent correspondent;
    private Outsourcing outsourcing;

    @BeforeEach
    void setUp() {
        // 테스트에 필요한 기본 객체들 생성
        admin = User.builder()
                .username("admin")
                .password("password")
                .name("관리자")
                .phoneNumber("010-1234-5678")
                .email("admin@test.com")
                .address("서울")
                .addressDetail("강남구")
                .role(Role.ROLE_ADMIN)
                .grade("A")
                .companyPhoneNumber("02-1234-5678")
                .companyFaxNumber("02-1234-5679")
                .build();

        correspondent = Correspondent.builder()
                .id(1L)
                .correspondentName("외주업체")
                .businessRegNumber("123-45-67890")
                .address("서울시")
                .detailedAddress("강남구")
                .build();

        contract = Contract.builder()
                .contractId(1L)
                .admin(admin)
                .method(Method.GENERAL)
                .processStatus(ProcessStatus.CONTRACTED)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .correspondent(correspondent)
                .contractCategory(ContractCategory.ETC)
                .registerDate(LocalDate.now())
                .lastModifyUser(admin)
                .build();

        detailContract = DetailContract.builder()
                .detailContractId(1L)
                .contract(contract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("웹사이트 구축")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .hasOutsourcing(true)
                .build();

        outsourcing = Outsourcing.builder()
                .outsourcingId(1L)
                .correspondent(correspondent)
                .detailContract(detailContract)
                .status(OutsourcingStatus.TEMPORARY)
                .content("외주 개발")
                .quantity(1)
                .unitPrice(800000)
                .supplyPrice(800000)
                .totalPrice(880000)
                .build();
    }

    @Test
    @DisplayName("외주계약 저장 테스트")
    void save() {
        // given
        given(outsourcingRepository.save(any(Outsourcing.class))).willReturn(outsourcing);

        // when
        Outsourcing savedOutsourcing = outsourcingRepository.save(outsourcing);

        // then
        assertThat(savedOutsourcing).isNotNull();
        assertThat(savedOutsourcing.getOutsourcingId()).isEqualTo(1L);
        assertThat(savedOutsourcing.getContent()).isEqualTo("외주 개발");
        verify(outsourcingRepository).save(any(Outsourcing.class));
    }

    @Test
    @DisplayName("ID로 외주계약 조회 테스트")
    void findById() {
        // given
        given(outsourcingRepository.findById(1L)).willReturn(Optional.of(outsourcing));

        // when
        Optional<Outsourcing> found = outsourcingRepository.findById(1L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getOutsourcingId()).isEqualTo(1L);
        assertThat(found.get().getContent()).isEqualTo("외주 개발");
    }

    @Test
    @DisplayName("세부계약서 ID로 외주계약 조회 테스트")
    void findByDetailContractDetailContractId() {
        // given
        given(outsourcingRepository.findByDetailContractDetailContractId(1L))
                .willReturn(Optional.of(outsourcing));

        // when
        Optional<Outsourcing> found = outsourcingRepository.findByDetailContractDetailContractId(1L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getDetailContract().getDetailContractId()).isEqualTo(1L);
        assertThat(found.get().getContent()).isEqualTo("외주 개발");
    }
}