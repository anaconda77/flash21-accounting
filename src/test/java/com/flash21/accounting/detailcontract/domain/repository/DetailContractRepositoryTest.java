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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DetailContractRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DetailContractRepository detailContractRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private User testAdmin;
    private Correspondent testCorrespondent;
    private Contract testContract;
    private DetailContract testDetailContract;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // 테스트 어드민 생성 및 저장
        testAdmin = User.builder()
                .username("testAdmin")
                .password("password")
                .name("Test Admin")
                .phoneNumber("010-1234-5678")
                .email("test@example.com")
                .address("서울시 강남구")
                .addressDetail("테스트빌딩 3층")
                .role(Role.ROLE_ADMIN)
                .grade("HIGH")
                .companyPhoneNumber("02-1234-5678")
                .companyFaxNumber("02-1234-5679")
                .build();
        entityManager.persist(testAdmin);

        // 테스트 거래처 생성 및 저장
        testCorrespondent = Correspondent.builder()
                .correspondentName("테스트 거래처")
                .businessRegNumber("123-45-67890")
                .presidentName("김사장")
                .build();
        entityManager.persist(testCorrespondent);

        // 테스트 계약서 생성 및 저장
        testContract = Contract.builder()
                .admin(testAdmin)
                .correspondent(testCorrespondent)
                .name("테스트 계약서")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusMonths(1))
                .method(Method.GENERAL)
                .processStatus(ProcessStatus.AWAITING_PAYMENT)
                .contractCategory(ContractCategory.ETC)
                .lastModifyUser(testAdmin)
                .build();
        entityManager.persist(testContract);

        // 테스트 세부계약서 생성 및 저장
        testDetailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 세부계약 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .build();

        // 테스트 결제정보 생성 및 저장
        testPayment = Payment.builder()
                .detailContract(testDetailContract)
                .method("계좌이체")
                .condition("선금 50%, 잔금 50%")
                .build();

        testDetailContract.setPayment(testPayment);
        entityManager.persist(testDetailContract);

        entityManager.flush();
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회")
    void findByContractContractId() {
        // when
        List<DetailContract> detailContracts = detailContractRepository.findByContractContractId(testContract.getContractId());

        // then
        assertThat(detailContracts).hasSize(1);
        DetailContract foundDetailContract = detailContracts.get(0);
        assertThat(foundDetailContract.getContract().getContractId()).isEqualTo(testContract.getContractId());
        assertThat(foundDetailContract.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
        assertThat(foundDetailContract.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
    }

    @Test
    @DisplayName("계약서로 세부계약서 목록 조회")
    void findByContract() {
        // when
        List<DetailContract> detailContracts = detailContractRepository.findByContract(testContract);

        // then
        assertThat(detailContracts).hasSize(1);
        DetailContract foundDetailContract = detailContracts.get(0);
        assertThat(foundDetailContract.getContract()).isEqualTo(testContract);
        assertThat(foundDetailContract.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
    }

    @Test
    @DisplayName("세부계약서 저장 시 Payment도 함께 저장")
    void saveDetailContractWithPayment() {
        // given
        DetailContract newDetailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_DESIGN)
                .status(DetailContractStatus.TEMPORARY)
                .content("새로운 세부계약 내용")
                .quantity(1)
                .unitPrice(2000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .build();

        Payment newPayment = Payment.builder()
                .detailContract(newDetailContract)
                .method("신용카드")
                .condition("계약금 30%, 잔금 70%")
                .build();

        newDetailContract.setPayment(newPayment);

        // when
        DetailContract savedDetailContract = detailContractRepository.save(newDetailContract);

        // then
        assertThat(savedDetailContract.getDetailContractId()).isNotNull();
        assertThat(savedDetailContract.getPayment().getPaymentId()).isNotNull();
        assertThat(savedDetailContract.getPayment().getMethod()).isEqualTo("신용카드");
        assertThat(savedDetailContract.getPayment().getCondition()).isEqualTo("계약금 30%, 잔금 70%");
    }

    @Test
    @DisplayName("세부계약서 삭제 시 Payment도 함께 삭제")
    void deleteDetailContractCascadePayment() {
        // given
        Long detailContractId = testDetailContract.getDetailContractId();
        Long paymentId = testDetailContract.getPayment().getPaymentId();

        // when
        detailContractRepository.deleteById(detailContractId);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(detailContractRepository.findById(detailContractId)).isEmpty();
        assertThat(paymentRepository.findById(paymentId)).isEmpty();
    }

    @Test
    @DisplayName("특정 계약서의 모든 세부계약서가 존재하지 않는 경우")
    void findByContractWhenEmpty() {
        // given
        Contract emptyContract = Contract.builder()
                .admin(testAdmin)
                .correspondent(testCorrespondent)
                .name("빈 계약서")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusMonths(1))
                .method(Method.GENERAL)
                .processStatus(ProcessStatus.AWAITING_PAYMENT)
                .contractCategory(ContractCategory.ETC)
                .lastModifyUser(testAdmin)
                .build();
        entityManager.persist(emptyContract);

        // when
        List<DetailContract> detailContracts = detailContractRepository.findByContract(emptyContract);

        // then
        assertThat(detailContracts).isEmpty();
    }
}