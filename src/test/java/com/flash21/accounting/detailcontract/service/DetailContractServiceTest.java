package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.repository.OwnerRepository;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.core.service.OpenAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class DetailContractServiceTest {
    @Autowired
    private DetailContractServiceImpl detailContractService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private DetailContractRepository detailContractRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorrespondentRepository correspondentRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private User testAdmin;
    private Correspondent testCorrespondent;
    private Contract testContract;
    private DetailContractRequest testRequest;
    @Autowired
    private OpenAPIService openAPIService;

    @BeforeEach
    void setUp() {
        // 테스트 어드민 생성
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
        testAdmin = userRepository.save(testAdmin);

        // Owner 생성
        Owner owner = OwnerFixture.createDefault();
        owner = ownerRepository.save(owner);

        // 테스트 거래처 생성
        testCorrespondent = Correspondent.builder()
                .correspondentName("테스트 거래처_" + System.currentTimeMillis()) // unique 제약조건을 위한 unique한 이름
                .businessRegNumber("123-45-67890")
                .owner(owner)
                .managerName("홍길동")
                .managerPosition("과장")
                .managerPhoneNumber("010-1234-5678")
                .managerEmail("manager@test.com")
                .taxEmail("tax@test.com")
                .address("서울시 강남구")
                .detailedAddress("테스트빌딩 1층")
                .memo("테스트 메모")
                .correspondentCategory(CorrespondentCategory.HEALTH)
                .build();
        testCorrespondent = correspondentRepository.save(testCorrespondent);

        // 테스트 계약서 생성
        testContract = Contract.builder()
                .admin(testAdmin)
                .correspondent(testCorrespondent)
                .name("테스트 계약서")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusMonths(1))
                .lastModifyUser(testAdmin)
                .build();
        testContract = contractRepository.save(testContract);

        // 테스트 요청 데이터 생성
        testRequest = DetailContractRequest.builder()
                .contractId(testContract.getContractId())
                .detailContractCategory("웹사이트 구축")
                .status("임시")
                .content("테스트 세부계약 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .build();
    }

    @Test
    @DisplayName("세부계약서 생성 - 성공")
    void createDetailContract_Success() {
        // when
        var response = detailContractService.createDetailContract(testRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDetailContractId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        assertThat(response.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
        assertThat(response.getPaymentMethod()).isEqualTo("계좌이체");
        assertThat(response.getPaymentCondition()).isEqualTo("선금 50%, 잔금 50%");

        // DB 검증
        var savedDetailContract = detailContractRepository.findById(response.getDetailContractId()).orElseThrow();
        assertThat(savedDetailContract.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        assertThat(savedDetailContract.getPayment()).isNotNull();
        assertThat(savedDetailContract.getPayment().getMethod()).isEqualTo("계좌이체");
    }


    @Test
    @DisplayName("세부계약서 생성 - 잘못된 카테고리 입력시 실패")
    void createDetailContract_InvalidCategory() {
        // given
        testRequest = DetailContractRequest.builder()
                .contractId(testContract.getContractId())
                .detailContractCategory("존재하지 않는 카테고리")
                .status("존재하지 않는 상태")
                .content("테스트 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .build();

        // when & then
        assertThatThrownBy(() -> detailContractService.createDetailContract(testRequest))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("존재하지 않은 카테고리입니다.");
    }

    @Test
    @DisplayName("세부계약서 생성 - 계약서가 존재하지 않는 경우 실패")
    void createDetailContract_ContractNotFound() {
        // given
        testRequest.setContractId(9999L);

        // when & then
        assertThatThrownBy(() -> detailContractService.createDetailContract(testRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 단건 조회 - 성공")
    void getDetailContract_Success() {
        // given
        var created = detailContractService.createDetailContract(testRequest);

        // when
        var response = detailContractService.getDetailContract(created.getDetailContractId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        assertThat(response.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
        assertThat(response.getPaymentMethod()).isEqualTo("계좌이체");
        assertThat(response.getPaymentCondition()).isEqualTo("선금 50%, 잔금 50%");

    }

    @Test
    @DisplayName("세부계약서 단건 조회 - 존재하지 않는 경우")
    void getDetailContract_NotFound() {
        // when & then
        assertThatThrownBy(() -> detailContractService.getDetailContract(9999L))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회 - 성공")
    void getDetailContractsByContractId_Success() {
        // given
        // 첫 번째 세부계약서 생성
        var firstDetailContract = detailContractService.createDetailContract(testRequest);

        // 두 번째 세부계약서 생성
        DetailContractRequest secondRequest = DetailContractRequest.builder()
                .contractId(testContract.getContractId())
                .detailContractCategory("디자인")
                .status("임시")
                .content("두 번째 세부계약 내용")
                .quantity(1)
                .unitPrice(2000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .paymentMethod("신용카드")
                .paymentCondition("계약금 30%, 잔금 70%")
                .build();
        var secondDetailContract = detailContractService.createDetailContract(secondRequest);

        // when
        var responses = detailContractService.getDetailContractsByContractId(testContract.getContractId());

        // then
        assertThat(responses).hasSize(2);

        // 첫 번째 세부계약서 검증
        var firstResponse = responses.stream()
                .filter(r -> r.getDetailContractId().equals(firstDetailContract.getDetailContractId()))
                .findFirst()
                .orElseThrow();
        assertThat(firstResponse.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
        assertThat(firstResponse.getContent()).isEqualTo("테스트 세부계약 내용");
        assertThat(firstResponse.getPaymentMethod()).isEqualTo("계좌이체");

        // 두 번째 세부계약서 검증
        var secondResponse = responses.stream()
                .filter(r -> r.getDetailContractId().equals(secondDetailContract.getDetailContractId()))
                .findFirst()
                .orElseThrow();
        assertThat(secondResponse.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_DESIGN);
        assertThat(secondResponse.getContent()).isEqualTo("두 번째 세부계약 내용");
        assertThat(secondResponse.getPaymentMethod()).isEqualTo("신용카드");
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회 - 계약서가 존재하지 않는 경우")
    void getDetailContractsByContractId_ContractNotFound() {
        //when&then
        assertThatThrownBy(() -> detailContractService.getDetailContractsByContractId(9999L))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("존재하지 않는 상위계약서입니다");
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회 - 세부계약서가 없는 경우")
    void getDetailContractsByContractId_EmptyList() {
        // when
        var responses = detailContractService.getDetailContractsByContractId(testContract.getContractId());

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("세부계약서 상태 변경 - TEMPORARY에서 ONGOING으로 변경 성공")
    void updateDetailContract_StatusChange_Success() {
       //given
        var created = detailContractService.createDetailContract(testRequest);

        var updateRequest = DetailContractUpdateRequest.builder()
                .status("진행")
                .detailContractCategory("웹사이트 구축")
                .build();

        //when
        var response = detailContractService.updateDetailContract(created.getDetailContractId(), updateRequest);

        //then
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.ONGOING);

        //db검증
        var updatedDetailContract = detailContractRepository.findById(created.getDetailContractId()).orElseThrow();
        assertThat(updatedDetailContract.getStatus()).isEqualTo(DetailContractStatus.ONGOING);
        assertThat(response.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);

    }

    @Test
    @DisplayName("세부계약서 상태 변경 - 잘못된 상태 변경 시도시 실패")
    void updateDetailContract_InvalidStatusChange() {
        // given
        var created = detailContractService.createDetailContract(testRequest);

        var updateRequest = DetailContractUpdateRequest.builder()
                .status("완료") // TEMPORARY에서 바로 DONE으로 변경 시도
                .build();

        // when & then
        assertThatThrownBy(() -> detailContractService.updateDetailContract(created.getDetailContractId(), updateRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 상태 변경 - CANCELED 상태에서 수정 시도시 실패")
    void updateDetailContract_CanceledStatusUpdate() {
        // given
        var created = detailContractService.createDetailContract(testRequest);

        // CANCELED 상태로 변경
        var cancelRequest = DetailContractUpdateRequest.builder()
                .status("취소")
                .build();
        detailContractService.updateDetailContract(created.getDetailContractId(), cancelRequest);

        // 수정 시도
        var updateRequest = DetailContractUpdateRequest.builder()
                .content("내용 수정")
                .build();

        // when & then
        assertThatThrownBy(() -> detailContractService.updateDetailContract(created.getDetailContractId(), updateRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 수정 - 내용만 변경 성공")
    void updateDetailContract_ContentOnly() {
        // given
        var created = detailContractService.createDetailContract(testRequest);

        var updateRequest = DetailContractUpdateRequest.builder()
                .content("수정된 내용")
                .build();

        // when
        var response = detailContractService.updateDetailContract(created.getDetailContractId(), updateRequest);

        // then
        assertThat(response.getContent()).isEqualTo("수정된 내용");
        assertThat(response.getStatus()).isEqualTo(created.getStatus());  // 다른 필드는 변경되지 않음
        assertThat(response.getQuantity()).isEqualTo(created.getQuantity());
        assertThat(response.getUnitPrice()).isEqualTo(created.getUnitPrice());
    }

    @Test
    @DisplayName("세부계약서 수정 - 수량과 단가만 변경 성공")
    void updateDetailContract_QuantityAndUnitPrice() {
        // given
        var created = detailContractService.createDetailContract(testRequest);

        var updateRequest = DetailContractUpdateRequest.builder()
                .quantity(2)
                .unitPrice(2000000)
                .build();

        // when
        var response = detailContractService.updateDetailContract(created.getDetailContractId(), updateRequest);

        // then
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getUnitPrice()).isEqualTo(2000000);
        assertThat(response.getStatus()).isEqualTo(created.getStatus());  // 다른 필드는 변경되지 않음
        assertThat(response.getContent()).isEqualTo(created.getContent());
        assertThat(response.getDetailContractCategory()).isEqualTo(created.getDetailContractCategory());
    }

    @Test
    @DisplayName("세부계약서 수정 - 결제 조건만 변경 성공")
    void updateDetailContract_PaymentConditionOnly() {
        // given
        var created = detailContractService.createDetailContract(testRequest);
        assertThat(created.getPaymentMethod()).isEqualTo("계좌이체");
        assertThat(created.getPaymentCondition()).isEqualTo("선금 50%, 잔금 50%");

        var updateRequest = DetailContractUpdateRequest.builder()
                .paymentCondition("계약금 30%, 잔금 70%")
                .build();

        // when
        var response = detailContractService.updateDetailContract(created.getDetailContractId(), updateRequest);

        // then
        assertThat(response.getPaymentMethod()).isEqualTo("계좌이체");  // 기존 값 유지
        assertThat(response.getPaymentCondition()).isEqualTo("계약금 30%, 잔금 70%");  // 새로운 값으로 변경
        assertThat(response.getStatus()).isEqualTo(created.getStatus());
        assertThat(response.getContent()).isEqualTo(created.getContent());
    }

    @Test
    @DisplayName("세부계약서 수정 - 공급가액과 합계금액만 변경 성공")
    void updateDetailContract_PricesOnly() {
        // given
        var created = detailContractService.createDetailContract(testRequest);

        var updateRequest = DetailContractUpdateRequest.builder()
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .build();

        // when
        var response = detailContractService.updateDetailContract(created.getDetailContractId(), updateRequest);

        // then
        assertThat(response.getSupplyPrice()).isEqualTo(2000000);
        assertThat(response.getTotalPrice()).isEqualTo(2200000);
        assertThat(response.getQuantity()).isEqualTo(created.getQuantity());  // 다른 필드는 변경되지 않음
        assertThat(response.getUnitPrice()).isEqualTo(created.getUnitPrice());
        assertThat(response.getStatus()).isEqualTo(created.getStatus());
    }

    @Test
    @DisplayName("세부계약서 삭제 - 성공")
    void deleteDetailContract_Success() {
        // given
        var created = detailContractService.createDetailContract(testRequest);
        Long detailContractId = created.getDetailContractId();

        // when
        detailContractService.deleteDetailContract(detailContractId);

        // then
        assertThat(detailContractRepository.findById(detailContractId)).isEmpty();

        //연관된 payment 삭제확인
        var payments = paymentRepository.findAll();
        assertThat(payments).isEmpty();
    }
}
