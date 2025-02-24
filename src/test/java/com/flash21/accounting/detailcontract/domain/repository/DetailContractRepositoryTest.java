package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetailContractRepositoryTest {

    @Mock
    private DetailContractRepository detailContractRepository;

    @Mock
    private PaymentRepository paymentRepository;

    private User admin;
    private Contract contract;
    private DetailContract detailContract;
    private Payment payment;
    private Correspondent correspondent;

    @BeforeEach
    void setUp() {
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
                .correspondentName("테스트 업체")
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
                .hasOutsourcing(false)
                .build();

        payment = Payment.builder()
                .paymentId(1L)
                .detailContract(detailContract)
                .method("계좌이체")
                .condition("선금 50%, 잔금 50%")
                .build();
    }


    @Test
    @DisplayName("세부계약서 저장 테스트")
    void save() {
        // given
        given(detailContractRepository.save(any(DetailContract.class))).willReturn(detailContract);

        // when
        DetailContract savedDetailContract = detailContractRepository.save(detailContract);

        // then
        assertThat(savedDetailContract).isNotNull();
        assertThat(savedDetailContract.getDetailContractId()).isEqualTo(1L);
        assertThat(savedDetailContract.getContent()).isEqualTo("웹사이트 구축");
        verify(detailContractRepository).save(any(DetailContract.class));
    }

    @Test
    @DisplayName("ID로 세부계약서 조회 테스트")
    void findById() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));

        // when
        Optional<DetailContract> found = detailContractRepository.findById(1L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getDetailContractId()).isEqualTo(1L);
        assertThat(found.get().getContent()).isEqualTo("웹사이트 구축");
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회 테스트")
    void findByContractContractId() {
        // given
        given(detailContractRepository.findByContractContractId(1L))
                .willReturn(List.of(detailContract));

        // when
        List<DetailContract> detailContracts = detailContractRepository.findByContractContractId(1L);

        // then
        assertThat(detailContracts).hasSize(1);
        assertThat(detailContracts.get(0).getDetailContractId()).isEqualTo(1L);
        assertThat(detailContracts.get(0).getContent()).isEqualTo("웹사이트 구축");
    }

    @Test
    @DisplayName("Contract 엔티티로 세부계약서 목록 조회 테스트")
    void findByContract() {
        // given
        given(detailContractRepository.findByContract(contract))
                .willReturn(List.of(detailContract));

        // when
        List<DetailContract> detailContracts = detailContractRepository.findByContract(contract);

        // then
        assertThat(detailContracts).hasSize(1);
        assertThat(detailContracts.get(0).getContract()).isEqualTo(contract);
        assertThat(detailContracts.get(0).getContent()).isEqualTo("웹사이트 구축");
    }

    @Test
    @DisplayName("세부계약서와 Payment 연관관계 테스트")
    void detailContractPaymentRelation() {
        // given
        given(paymentRepository.findByDetailContractDetailContractId(1L)).willReturn(Optional.of(payment));

        // when
        Optional<Payment> foundPayment = paymentRepository.findByDetailContractDetailContractId(1L);

        // then
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getDetailContract()).isEqualTo(detailContract);
        assertThat(foundPayment.get().getMethod()).isEqualTo("계좌이체");
        assertThat(foundPayment.get().getCondition()).isEqualTo("선금 50%, 잔금 50%");
    }
}