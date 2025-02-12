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

public class UpdateDetailContractRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("UpdateDetailContractRequest 수정 성공 테스트")
    void updateSuccess() {
        // given
        UpdateDetailContractRequest request = UpdateDetailContractRequest.builder()
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
        Set<ConstraintViolation<UpdateDetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("필수 필드 누락 시 validation 실패 테스트")
    void updateFailDueToMissingRequiredFields() {
        // given
        UpdateDetailContractRequest request = UpdateDetailContractRequest.builder()
                .contractType("일반")
                // contractStatus 누락
                // largeCategory 누락
                .build();

        // when
        Set<ConstraintViolation<UpdateDetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("수량, 단가, 금액이 0 이하일 경우 validation 실패 테스트")
    void updateFailDueToInvalidNumbers() {
        // given
        UpdateDetailContractRequest request = UpdateDetailContractRequest.builder()
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
        Set<ConstraintViolation<UpdateDetailContractRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations.size()).isEqualTo(4);
    }
}
