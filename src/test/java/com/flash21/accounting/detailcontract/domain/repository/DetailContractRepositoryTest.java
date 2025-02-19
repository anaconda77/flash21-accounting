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
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.repository.OwnerRepository;
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

    @Autowired
    private OwnerRepository ownerRepository; // OwnerRepository 주입

    private User testAdmin;
    private Correspondent testCorrespondent;
    private Contract testContract;
    private static long sequence = 1;

    @BeforeEach
    void setUp() {
        // 영속성 컨텍스트 초기화
        entityManager.clear();

        // Owner는 OwnerRepository를 통해 저장
        Owner owner = ownerRepository.save(OwnerFixture.createDefault());

        // 나머지 코드는 그대로 유지
        testAdmin = User.builder()
                .username("testAdmin_" + sequence++)
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
        testAdmin = entityManager.persist(testAdmin);

        testCorrespondent = Correspondent.builder()
                .correspondentName("테스트 거래처_" + System.currentTimeMillis())
                .businessRegNumber("123-45-67890")
                .owner(owner)
                .build();
        testCorrespondent = entityManager.persist(testCorrespondent);

        testContract = Contract.builder()
                .admin(testAdmin)
                .correspondent(testCorrespondent)
                .name("테스트 계약서")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusMonths(1))
                .lastModifyUser(testAdmin)
                .build();
        testContract = entityManager.persist(testContract);

        entityManager.flush();
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회")
    void findByContractContractId() {
        // given
        DetailContract detailContract = createAndSaveDetailContract();
        entityManager.flush();
        entityManager.clear();

        // when
        List<DetailContract> detailContracts = detailContractRepository.findByContractContractId(testContract.getContractId());

        // then
        assertThat(detailContracts).hasSize(1);
        DetailContract foundDetailContract = detailContracts.get(0);
        assertThat(foundDetailContract.getContract().getContractId()).isEqualTo(testContract.getContractId());
    }

    @Test
    @DisplayName("계약서로 세부계약서 목록 조회")
    void findByContract() {
        // given
        DetailContract detailContract = createAndSaveDetailContract();
        entityManager.flush();
        entityManager.clear();

        // when
        List<DetailContract> detailContracts = detailContractRepository.findByContract(testContract);

        // then
        assertThat(detailContracts).hasSize(1);
        DetailContract foundDetailContract = detailContracts.get(0);
        assertThat(foundDetailContract.getContract().getContractId()).isEqualTo(testContract.getContractId());
        assertThat(foundDetailContract.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
    }

    @Test
    @DisplayName("세부계약서 저장 시 Payment도 함께 저장")
    void saveDetailContractWithPayment() {
        // given
        DetailContract detailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_DESIGN)
                .status(DetailContractStatus.TEMPORARY)
                .content("새로운 세부계약 내용")
                .quantity(1)
                .unitPrice(2000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .build();

        Payment payment = Payment.builder()
                .detailContract(detailContract)
                .method("신용카드")
                .condition("계약금 30%, 잔금 70%")
                .build();

        detailContract.setPayment(payment);

        // when
        DetailContract savedDetailContract = detailContractRepository.save(detailContract);
        entityManager.flush();
        entityManager.clear();

        // then
        DetailContract foundDetailContract = detailContractRepository.findById(savedDetailContract.getDetailContractId()).orElseThrow();
        assertThat(foundDetailContract.getDetailContractId()).isNotNull();
        assertThat(foundDetailContract.getPayment().getPaymentId()).isNotNull();
        assertThat(foundDetailContract.getPayment().getMethod()).isEqualTo("신용카드");
        assertThat(foundDetailContract.getPayment().getCondition()).isEqualTo("계약금 30%, 잔금 70%");
    }

    @Test
    @DisplayName("세부계약서 삭제 시 Payment도 함께 삭제")
    void deleteDetailContractCascadePayment() {
        // given
        DetailContract detailContract = createAndSaveDetailContract();
        entityManager.flush();

        Long detailContractId = detailContract.getDetailContractId();
        Long paymentId = detailContract.getPayment().getPaymentId();

        // when
        detailContractRepository.deleteById(detailContractId);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(detailContractRepository.findById(detailContractId)).isEmpty();
        assertThat(paymentRepository.findById(paymentId)).isEmpty();
    }

    // 헬퍼 메서드
    private DetailContract createAndSaveDetailContract() {
        DetailContract detailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 세부계약 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .build();

        Payment payment = Payment.builder()
                .detailContract(detailContract)
                .method("계좌이체")
                .condition("선금 50%, 잔금 50%")
                .build();

        detailContract.setPayment(payment);
        return detailContractRepository.save(detailContract);
    }
}