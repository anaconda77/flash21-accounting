package com.flash21.accounting.detailcontract.dto;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import com.flash21.accounting.outsourcing.domain.entity.OutsourcingStatus;
import com.flash21.accounting.outsourcing.dto.response.OutsourcingResponse;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DetailContractDtoTest {

    private Validator validator;
    private User admin;
    private Contract contract;
    private DetailContract detailContract;
    private Payment payment;
    private Outsourcing outsourcing;
    private Correspondent correspondent;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

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
                .hasOutsourcing(true)
                .build();

        payment = Payment.builder()
                .paymentId(1L)
                .detailContract(detailContract)
                .method("계좌이체")
                .condition("선금 50%, 잔금 50%")
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
    @DisplayName("DetailContractRequest 유효성 검증 - 성공")
    void validateDetailContractRequest_Success() {
        // given
        DetailContractRequest request = DetailContractRequest.builder()
                .contractId(1L)
                .status("임시")
                .detailContractCategory("웹사이트 구축")
                .content("웹사이트 구축 세부계약")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .isOutsourcing(false)
                .build();

        // when
        Set<ConstraintViolation<DetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("DetailContractRequest 유효성 검증 - 필수 값 누락")
    void validateDetailContractRequest_Required_Fields() {
        // given
        DetailContractRequest request = DetailContractRequest.builder()
                .contractId(null)  // 필수 값 누락
                .status("")       // 빈 문자열
                .detailContractCategory("")  // 빈 문자열
                .content("")      // 빈 문자열
                .build();

        // when
        Set<ConstraintViolation<DetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(8);  // contractId, status, category, content, quantity, unitPrice, supplyPrice, totalPrice 검증 실패
    }

    @Test
    @DisplayName("DetailContractRequest 유효성 검증 - 잘못된 값")
    void validateDetailContractRequest_Invalid_Values() {
        // given
        DetailContractRequest request = DetailContractRequest.builder()
                .contractId(1L)
                .status("임시")
                .detailContractCategory("웹사이트 구축")
                .content("테스트")
                .quantity(-1)      // 음수
                .unitPrice(0)      // 0
                .supplyPrice(-1000) // 음수
                .totalPrice(0)     // 0
                .build();

        // when
        Set<ConstraintViolation<DetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(4);  // quantity, unitPrice, supplyPrice, totalPrice 검증 실패
    }

    @Test
    @DisplayName("DetailContractResponse 변환 - 외주계약 없음")
    void convertToDetailContractResponse_WithoutOutsourcing() {
        // given
        detailContract.setHasOutsourcing(false);

        // when
        DetailContractResponse response = DetailContractResponse.from(detailContract, payment, null);

        // then
        assertThat(response.getDetailContractId()).isEqualTo(detailContract.getDetailContractId());
        assertThat(response.getContractId()).isEqualTo(contract.getContractId());
        assertThat(response.getDetailContractCategory()).isEqualTo(detailContract.getDetailContractCategory());
        assertThat(response.getStatus()).isEqualTo(detailContract.getStatus());
        assertThat(response.getContent()).isEqualTo(detailContract.getContent());
        assertThat(response.getQuantity()).isEqualTo(detailContract.getQuantity());
        assertThat(response.getUnitPrice()).isEqualTo(detailContract.getUnitPrice());
        assertThat(response.getSupplyPrice()).isEqualTo(detailContract.getSupplyPrice());
        assertThat(response.getTotalPrice()).isEqualTo(detailContract.getTotalPrice());
        assertThat(response.getPaymentMethod()).isEqualTo(payment.getMethod());
        assertThat(response.getPaymentCondition()).isEqualTo(payment.getCondition());
        assertThat(response.getOutsourcing()).isNull();
    }

    @Test
    @DisplayName("DetailContractResponse 변환 - 외주계약 포함")
    void convertToDetailContractResponse_WithOutsourcing() {
        // given
        detailContract.setHasOutsourcing(true);

        // when
        DetailContractResponse response = DetailContractResponse.from(detailContract, payment, outsourcing);

        // then
        assertThat(response.getDetailContractId()).isEqualTo(detailContract.getDetailContractId());
        assertThat(response.getContractId()).isEqualTo(contract.getContractId());
        assertThat(response.getDetailContractCategory()).isEqualTo(detailContract.getDetailContractCategory());
        assertThat(response.getStatus()).isEqualTo(detailContract.getStatus());
        assertThat(response.getContent()).isEqualTo(detailContract.getContent());
        assertThat(response.getQuantity()).isEqualTo(detailContract.getQuantity());
        assertThat(response.getUnitPrice()).isEqualTo(detailContract.getUnitPrice());
        assertThat(response.getSupplyPrice()).isEqualTo(detailContract.getSupplyPrice());
        assertThat(response.getTotalPrice()).isEqualTo(detailContract.getTotalPrice());
        assertThat(response.getPaymentMethod()).isEqualTo(payment.getMethod());
        assertThat(response.getPaymentCondition()).isEqualTo(payment.getCondition());
        assertThat(response.getOutsourcing()).isNotNull();
        assertThat(response.getOutsourcing().getOutsourcingId()).isEqualTo(outsourcing.getOutsourcingId());
    }
}