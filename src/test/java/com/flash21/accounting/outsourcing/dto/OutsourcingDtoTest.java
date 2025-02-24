package com.flash21.accounting.outsourcing.dto;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import com.flash21.accounting.outsourcing.domain.entity.OutsourcingStatus;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingRequest;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingUpdateRequest;
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

class OutsourcingDtoTest {

    private Validator validator;
    private User admin;
    private Contract contract;
    private DetailContract detailContract;
    private Correspondent correspondent;
    private Outsourcing outsourcing;

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
                .correspondentName("외주업체")
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
    @DisplayName("OutsourcingRequest 유효성 검증 - 성공")
    void validateOutsourcingRequest_Success() {
        // given
        OutsourcingRequest request = OutsourcingRequest.builder()
                .correspondentId(1L)
                .status("임시")
                .content("외주 개발")
                .quantity(1)
                .unitPrice(800000)
                .supplyPrice(800000)
                .totalPrice(880000)
                .build();

        // when
        Set<ConstraintViolation<OutsourcingRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("OutsourcingRequest 유효성 검증 - 필수 값 누락")
    void validateOutsourcingRequest_Required_Fields() {
        // given
        OutsourcingRequest request = OutsourcingRequest.builder()
                .correspondentId(null)  // 필수 값 누락
                .status("")            // 빈 문자열
                .content("")           // 빈 문자열
                .build();

        // when
        Set<ConstraintViolation<OutsourcingRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(7);  // correspondentId, status, content, quantity, unitPrice, supplyPrice, totalPrice
    }

    @Test
    @DisplayName("OutsourcingRequest 유효성 검증 - 잘못된 값")
    void validateOutsourcingRequest_Invalid_Values() {
        // given
        OutsourcingRequest request = OutsourcingRequest.builder()
                .correspondentId(1L)
                .status("임시")
                .content("외주 개발")
                .quantity(-1)      // 음수
                .unitPrice(0)      // 0
                .supplyPrice(-1000) // 음수
                .totalPrice(0)     // 0
                .build();

        // when
        Set<ConstraintViolation<OutsourcingRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(4);  // quantity, unitPrice, supplyPrice, totalPrice 검증 실패
    }

    @Test
    @DisplayName("OutsourcingResponse 변환 테스트")
    void testOutsourcingResponseConversion() {
        // when
        OutsourcingResponse response = OutsourcingResponse.from(outsourcing);

        // then
        assertThat(response.getOutsourcingId()).isEqualTo(outsourcing.getOutsourcingId());
        assertThat(response.getCorrespondentId()).isEqualTo(correspondent.getId());
        assertThat(response.getDetailContractId()).isEqualTo(detailContract.getDetailContractId());
        assertThat(response.getStatus()).isEqualTo(outsourcing.getStatus());
        assertThat(response.getContent()).isEqualTo(outsourcing.getContent());
        assertThat(response.getQuantity()).isEqualTo(outsourcing.getQuantity());
        assertThat(response.getUnitPrice()).isEqualTo(outsourcing.getUnitPrice());
        assertThat(response.getSupplyPrice()).isEqualTo(outsourcing.getSupplyPrice());
        assertThat(response.getTotalPrice()).isEqualTo(outsourcing.getTotalPrice());
    }

    @Test
    @DisplayName("OutsourcingUpdateRequest 부분 업데이트 테스트")
    void testOutsourcingUpdateRequest_PartialUpdate() {
        // given
        OutsourcingUpdateRequest request = OutsourcingUpdateRequest.builder()
                .status("진행")
                .content("수정된 외주 개발")
                .build();

        // when
        outsourcing.updateOutsourcing(request);

        // then
        assertThat(outsourcing.getStatus()).isEqualTo(OutsourcingStatus.ONGOING);
        assertThat(outsourcing.getContent()).isEqualTo("수정된 외주 개발");
        // 다른 필드들은 변경되지 않아야 함
        assertThat(outsourcing.getQuantity()).isEqualTo(1);
        assertThat(outsourcing.getUnitPrice()).isEqualTo(800000);
        assertThat(outsourcing.getSupplyPrice()).isEqualTo(800000);
        assertThat(outsourcing.getTotalPrice()).isEqualTo(880000);
    }

    @Test
    @DisplayName("OutsourcingUpdateRequest 전체 업데이트 테스트")
    void testOutsourcingUpdateRequest_FullUpdate() {
        // given
        OutsourcingUpdateRequest request = OutsourcingUpdateRequest.builder()
                .status("진행")
                .content("수정된 외주 개발")
                .quantity(2)
                .unitPrice(900000)
                .supplyPrice(1800000)
                .totalPrice(1980000)
                .build();

        // when
        outsourcing.updateOutsourcing(request);

        // then
        assertThat(outsourcing.getStatus()).isEqualTo(OutsourcingStatus.ONGOING);
        assertThat(outsourcing.getContent()).isEqualTo("수정된 외주 개발");
        assertThat(outsourcing.getQuantity()).isEqualTo(2);
        assertThat(outsourcing.getUnitPrice()).isEqualTo(900000);
        assertThat(outsourcing.getSupplyPrice()).isEqualTo(1800000);
        assertThat(outsourcing.getTotalPrice()).isEqualTo(1980000);
    }
}