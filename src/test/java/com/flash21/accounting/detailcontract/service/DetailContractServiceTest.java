package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DetailContractServiceTest {
    @InjectMocks
    private DetailContractServiceImpl detailContractService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private DetailContractRepository detailContractRepository;

    private User testAdmin;
    private Correspondent testCorrespondent;
    private Contract testContract;
    private DetailContractRequest testRequest;

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

        // 테스트 거래처 생성
        testCorrespondent = Correspondent.builder()
                .correspondentName("테스트 거래처")
                .businessRegNumber("123-45-67890")
                .owner(OwnerFixture.createDefault())
                .build();

        // 테스트 계약서 생성
        testContract = Contract.builder()
                .admin(testAdmin)
                .correspondent(testCorrespondent)
                .name("테스트 계약서")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusMonths(1))
                .lastModifyUser(testAdmin)
                .build();

        // 테스트 요청 데이터 생성
        testRequest = DetailContractRequest.builder()
                .contractId(1L)
                .detailContractCategory("웹사이트 구축")
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
        // given
        given(contractRepository.findById(any(Long.class)))
                .willReturn(Optional.of(testContract));

        DetailContract savedDetailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content(testRequest.getContent())
                .quantity(testRequest.getQuantity())
                .unitPrice(testRequest.getUnitPrice())
                .supplyPrice(testRequest.getSupplyPrice())
                .totalPrice(testRequest.getTotalPrice())
                .build();
        Payment payment = Payment.builder()
                .detailContract(savedDetailContract)
                .method(testRequest.getPaymentMethod())
                .condition(testRequest.getPaymentCondition())
                .build();

        savedDetailContract.setPayment(payment);

        given(detailContractRepository.save(any(DetailContract.class)))
                .willReturn(savedDetailContract);

        // when
        var response = detailContractService.createDetailContract(testRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        assertThat(response.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
        assertThat(response.getPaymentMethod()).isEqualTo(testRequest.getPaymentMethod());
        assertThat(response.getPaymentCondition()).isEqualTo(testRequest.getPaymentCondition());
        verify(contractRepository).findById(any(Long.class));
        verify(detailContractRepository).save(any(DetailContract.class));
    }

    @Test
    @DisplayName("세부계약서 생성 - 잘못된 카테고리 입력시 실패")
    void createDetailContract_InvalidCategory() {
        // given
        testRequest = DetailContractRequest.builder()
                .contractId(1L)
                .detailContractCategory("존재하지 않는 카테고리")
                .content("테스트 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .build();

        given(contractRepository.findById(any(Long.class)))
                .willReturn(Optional.of(testContract));

        // when & then
        assertThatThrownBy(() -> detailContractService.createDetailContract(testRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 생성 - 계약서가 존재하지 않는 경우 실패")
    void createDetailContract_ContractNotFound() {
        // given
        given(contractRepository.findById(any(Long.class)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> detailContractService.createDetailContract(testRequest))
                .isInstanceOf(AccountingException.class);

        verify(contractRepository).findById(any(Long.class));
    }


    @Test
    @DisplayName("세부계약서 단건 조회 - 성공")
    void getDetailContract_Success() {
        // given
        DetailContract detailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 내용")
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

        given(detailContractRepository.findById(1L))
                .willReturn(Optional.of(detailContract));

        // when
        var response = detailContractService.getDetailContract(1L);

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
        // given
        given(detailContractRepository.findById(any(Long.class)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> detailContractService.getDetailContract(1L))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 상태 변경 - TEMPORARY에서 ONGOING으로 변경 성공")
    void updateDetailContract_StatusChange_Success() {
        // given
        DetailContract detailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 내용")
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

        given(detailContractRepository.findById(1L))
                .willReturn(Optional.of(detailContract));

        var updateRequest = DetailContractUpdateRequest.builder()
                .status(DetailContractStatus.ONGOING)
                .build();

        // when
        var response = detailContractService.updateDetailContract(1L, updateRequest);

        // then
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.ONGOING);
        assertThat(response.getPaymentMethod()).isEqualTo("계좌이체");
        assertThat(response.getPaymentCondition()).isEqualTo("선금 50%, 잔금 50%");
    }

    @Test
    @DisplayName("세부계약서 상태 변경 - 잘못된 상태 변경 시도시 실패")
    void updateDetailContract_InvalidStatusChange() {
        // given
        DetailContract detailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 내용")
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

        given(detailContractRepository.findById(1L))
                .willReturn(Optional.of(detailContract));

        var updateRequest = DetailContractUpdateRequest.builder()
                .status(DetailContractStatus.DONE) // TEMPORARY에서 바로 DONE으로 변경 시도
                .build();

        // when & then
        assertThatThrownBy(() -> detailContractService.updateDetailContract(1L, updateRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 상태 변경 - CANCELED 상태에서 수정 시도시 실패")
    void updateDetailContract_CanceledStatusUpdate() {
        // given
        DetailContract detailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.CANCELED)
                .content("테스트 내용")
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

        given(detailContractRepository.findById(1L))
                .willReturn(Optional.of(detailContract));

        var updateRequest = DetailContractUpdateRequest.builder()
                .content("내용 수정")
                .build();

        // when & then
        assertThatThrownBy(() -> detailContractService.updateDetailContract(1L, updateRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 삭제 - 성공")
    void deleteDetailContract_Success() {
        // given
        DetailContract detailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 내용")
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

        given(detailContractRepository.findById(1L))
                .willReturn(Optional.of(detailContract));

        // when
        detailContractService.deleteDetailContract(1L);

        // then
        verify(detailContractRepository).delete(detailContract);
    }
}
