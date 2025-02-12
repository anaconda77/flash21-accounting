package com.flash21.accounting.detailcontract.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDetailContractRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CreateDetailContractRequest 생성 성공 테스트")
    void createSuccess() {
        // given
        CreateDetailContractRequest request = com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest.builder()
                .contractId(1L)
                .contractType("일반")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .lastModifyUser("admin")
                .build();

        // when
        Set<ConstraintViolation<CreateDetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("필수 필드 누락 시 validation 실패 테스트")
    void createFailDueToMissingRequiredFields() {
        // given
        CreateDetailContractRequest request = com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest.builder()
                .contractType("일반")
                // contractId 누락
                // contractStatus 누락
                .build();

        // when
        Set<ConstraintViolation<CreateDetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("수량, 단가, 금액이 0 이하일 경우 validation 실패 테스트")
    void createFailDueToInvalidNumbers() {
        // given
        CreateDetailContractRequest request = com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest.builder()
                .contractId(1L)
                .contractType("일반")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발")
                .quantity(0)  // invalid
                .unitPrice(-1000)  // invalid
                .supplyPrice(-1000)  // invalid
                .totalPrice(0)  // invalid
                .lastModifyUser("admin")
                .build();

        // when
        Set<ConstraintViolation<CreateDetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.size()).isEqualTo(4);
    }
}
