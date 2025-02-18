package com.flash21.accounting.detailcontract.dto;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DetailContractDtoTest {
    private Validator validator;
    private Contract testContract;
    private DetailContract testDetailContract;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // 테스트용 Contract 객체 생성
        testContract = Contract.builder()
                .contractId(1L)
                .name("테스트 계약서")
                .build();

        // 테스트용 DetailContract 객체 생성
        testDetailContract = DetailContract.builder()
                .contract(testContract)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .build();

        // 테스트용 Payment 객체 생성
        testPayment = Payment.builder()
                .detailContract(testDetailContract)
                .method("계좌이체")
                .condition("선금 50%, 잔금 50%")
                .build();

        testDetailContract.setPayment(testPayment);
    }

    @Test
    @DisplayName("DetailContractRequest 유효성 검증 - 성공")
    void validateDetailContractRequest_Success() {
        // given
        DetailContractRequest request = DetailContractRequest.builder()
                .contractId(1L)
                .status(DetailContractStatus.TEMPORARY)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .content("테스트 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .build();

        // when
        Set<ConstraintViolation<DetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("DetailContractRequest 유효성 검증 - 필수 필드 누락")
    void validateDetailContractRequest_Required_Fields() {
        // given
        DetailContractRequest request = DetailContractRequest.builder()
                .contractId(null)  // 필수 필드 누락
                .status(null)      // 필수 필드 누락
                .detailContractCategory(null)
                .content("")  // 빈 문자열
                .quantity(-1)  // 음수
                .unitPrice(0)  // 0
                .supplyPrice(-1000)  // 음수
                .totalPrice(0)  // 0
                .build();

        // when
        Set<ConstraintViolation<DetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(8);  // 8개의 제약 조건 위반

        // 위반된 필드명들을 Set으로 수집
        Set<String> violatedFields = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        // 각 필드별 위반 여부 확인
        assertThat(violatedFields).contains(
                "contractId",
                "status",
                "detailContractCategory",
                "content",
                "quantity",
                "unitPrice",
                "supplyPrice",
                "totalPrice"
        );
    }

    @Test
    @DisplayName("DetailContractUpdateRequest 부분 업데이트 검증")
    void validateDetailContractUpdateRequest_PartialUpdate() {
        // given
        DetailContractUpdateRequest request = DetailContractUpdateRequest.builder()
                .status(DetailContractStatus.ONGOING)
                .content("수정된 내용")
                .build();

        // when & then
        // UpdateRequest는 모든 필드가 nullable이므로 유효성 검증이 필요 없음
        assertThat(request.getStatus()).isEqualTo(DetailContractStatus.ONGOING);
        assertThat(request.getContent()).isEqualTo("수정된 내용");
        assertThat(request.getQuantity()).isNull();
        assertThat(request.getUnitPrice()).isNull();
    }

    @Test
    @DisplayName("DetailContractResponse 변환 검증")
    void validateDetailContractResponse_Conversion() {
        // when
        DetailContractResponse response = DetailContractResponse.from(testDetailContract);

        // then
        assertThat(response.getContractId()).isEqualTo(testContract.getContractId());
        assertThat(response.getDetailContractCategory()).isEqualTo(DetailContractCategory.WEBSITE_CONSTRUCTION);
        assertThat(response.getStatus()).isEqualTo(DetailContractStatus.TEMPORARY);
        assertThat(response.getContent()).isEqualTo("테스트 내용");
        assertThat(response.getQuantity()).isEqualTo(1);
        assertThat(response.getUnitPrice()).isEqualTo(1000000);
        assertThat(response.getSupplyPrice()).isEqualTo(1000000);
        assertThat(response.getTotalPrice()).isEqualTo(1100000);
        assertThat(response.getPaymentMethod()).isEqualTo("계좌이체");
        assertThat(response.getPaymentCondition()).isEqualTo("선금 50%, 잔금 50%");
    }
}