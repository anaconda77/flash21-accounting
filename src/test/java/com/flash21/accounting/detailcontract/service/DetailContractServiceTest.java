package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.outsourcing.domain.repository.OutsourcingRepository;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetailContractServiceTest {

    @InjectMocks
    private DetailContractServiceImpl detailContractService;

    @Mock
    private DetailContractRepository detailContractRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OutsourcingRepository outsourcingRepository;

    private User admin;
    private Contract contract;
    private DetailContract detailContract;
    private Payment payment;
    private Correspondent correspondent;
    private DetailContractRequest request;
    private DetailContractUpdateRequest updateRequest;

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

        request = DetailContractRequest.builder()
                .contractId(1L)
                .status("임시")
                .detailContractCategory("웹사이트 구축")
                .content("웹사이트 구축")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .isOutsourcing(false)
                .build();

        updateRequest = DetailContractUpdateRequest.builder()
                .status("진행")
                .content("수정된 웹사이트 구축")
                .quantity(2)
                .unitPrice(1500000)
                .supplyPrice(3000000)
                .totalPrice(3300000)
                .paymentMethod("카드")
                .paymentCondition("선결제")
                .build();
    }

    @Test
    @DisplayName("세부계약서 생성 - 성공")
    void createDetailContract_Success() {
        // given
        given(contractRepository.findById(1L)).willReturn(Optional.of(contract));
        given(detailContractRepository.save(any(DetailContract.class))).willReturn(detailContract);
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);
        given(paymentRepository.findByDetailContractId(any())).willReturn(Optional.of(payment));

        // when
        DetailContractResponse response = detailContractService.createDetailContract(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDetailContractId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo("웹사이트 구축");
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        verify(detailContractRepository).save(any(DetailContract.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("세부계약서 생성 - 실패 (계약서 없음)")
    void createDetailContract_Fail_ContractNotFound() {
        // given
        given(contractRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> detailContractService.createDetailContract(request))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 조회 - 성공")
    void getDetailContract_Success() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));
        given(paymentRepository.findByDetailContractId(1L)).willReturn(Optional.of(payment));

        // when
        DetailContractResponse response = detailContractService.getDetailContract(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDetailContractId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo("웹사이트 구축");
    }

    @Test
    @DisplayName("계약서별 세부계약서 목록 조회 - 성공")
    void getDetailContractsByContractId_Success() {
        // given
        given(detailContractRepository.findByContractContractId(1L))
                .willReturn(List.of(detailContract));
        given(paymentRepository.findByDetailContractId(1L))
                .willReturn(Optional.of(payment));

        // when
        List<DetailContractResponse> responses = detailContractService.getDetailContractsByContractId(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getDetailContractId()).isEqualTo(1L);
        assertThat(responses.get(0).getContent()).isEqualTo("웹사이트 구축");
    }

    @Test
    @DisplayName("세부계약서 수정 - 전체 항목 수정 성공")
    void updateDetailContract_AllFields_Success() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));
        given(paymentRepository.findByDetailContractId(1L)).willReturn(Optional.of(payment));

        // when
        DetailContractResponse response = detailContractService.updateDetailContract(1L, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("수정된 웹사이트 구축");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualTo(3300000);
        assertThat(response.getPaymentMethod()).isEqualTo("카드");
        assertThat(response.getPaymentCondition()).isEqualTo("선결제");
    }

    @Test
    @DisplayName("세부계약서 수정 - 상태만 수정 성공")
    void updateDetailContract_StatusOnly_Success() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));
        given(paymentRepository.findByDetailContractId(1L)).willReturn(Optional.of(payment));

        DetailContractUpdateRequest statusOnlyRequest = DetailContractUpdateRequest.builder()
                .status("진행")
                .build();

        // when
        DetailContractResponse response = detailContractService.updateDetailContract(1L, statusOnlyRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.ONGOING);
        // 다른 필드들은 그대로 유지
        assertThat(response.getContent()).isEqualTo("웹사이트 구축");
        assertThat(response.getQuantity()).isEqualTo(1);
        assertThat(response.getTotalPrice()).isEqualTo(1100000);
    }

    @Test
    @DisplayName("세부계약서 수정 - 결제 정보만 수정 성공")
    void updateDetailContract_PaymentOnly_Success() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));
        given(paymentRepository.findByDetailContractId(1L)).willReturn(Optional.of(payment));

        DetailContractUpdateRequest paymentOnlyRequest = DetailContractUpdateRequest.builder()
                .paymentMethod("카드")
                .paymentCondition("선결제")
                .build();

        // when
        DetailContractResponse response = detailContractService.updateDetailContract(1L, paymentOnlyRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentMethod()).isEqualTo("카드");
        assertThat(response.getPaymentCondition()).isEqualTo("선결제");
        // 다른 필드들은 그대로 유지
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        assertThat(response.getContent()).isEqualTo("웹사이트 구축");
        assertThat(response.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("세부계약서 수정 - 금액 정보만 수정 성공")
    void updateDetailContract_PriceOnly_Success() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));
        given(paymentRepository.findByDetailContractId(1L)).willReturn(Optional.of(payment));

        DetailContractUpdateRequest priceOnlyRequest = DetailContractUpdateRequest.builder()
                .quantity(2)
                .unitPrice(1500000)
                .supplyPrice(3000000)
                .totalPrice(3300000)
                .build();

        // when
        DetailContractResponse response = detailContractService.updateDetailContract(1L, priceOnlyRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getUnitPrice()).isEqualTo(1500000);
        assertThat(response.getSupplyPrice()).isEqualTo(3000000);
        assertThat(response.getTotalPrice()).isEqualTo(3300000);
        // 다른 필드들은 그대로 유지
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        assertThat(response.getContent()).isEqualTo("웹사이트 구축");
        assertThat(response.getPaymentMethod()).isEqualTo("계좌이체");
    }

    @Test
    @DisplayName("세부계약서 수정 - 실패 (취소된 계약)")
    void updateDetailContract_Fail_WhenCanceled() {
        // given
        detailContract.setStatus(DetailContractStatus.CANCELED);
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));

        // when & then
        assertThatThrownBy(() -> detailContractService.updateDetailContract(1L, updateRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("세부계약서 삭제 - 성공")
    void deleteDetailContract_Success() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));
        given(paymentRepository.findByDetailContractId(1L)).willReturn(Optional.of(payment));

        // when
        detailContractService.deleteDetailContract(1L);

        // then
        verify(detailContractRepository).delete(detailContract);
        verify(paymentRepository).delete(payment);
    }
}