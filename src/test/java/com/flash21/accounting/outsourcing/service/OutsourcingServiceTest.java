package com.flash21.accounting.outsourcing.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import com.flash21.accounting.outsourcing.domain.entity.OutsourcingStatus;
import com.flash21.accounting.outsourcing.domain.repository.OutsourcingRepository;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingRequest;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingUpdateRequest;
import com.flash21.accounting.outsourcing.dto.response.OutsourcingResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutsourcingServiceTest {

    @InjectMocks
    private OutsourcingServiceImpl outsourcingService;

    @Mock
    private OutsourcingRepository outsourcingRepository;

    @Mock
    private DetailContractRepository detailContractRepository;

    @Mock
    private CorrespondentRepository correspondentRepository;

    private User admin;
    private Contract contract;
    private DetailContract detailContract;
    private Correspondent correspondent;
    private Outsourcing outsourcing;
    private OutsourcingRequest request;
    private OutsourcingUpdateRequest updateRequest;

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
                .hasOutsourcing(false)
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


        request = OutsourcingRequest.builder()
                .correspondentId(1L)
                .status("임시")
                .content("외주 개발")
                .quantity(1)
                .unitPrice(800000)
                .supplyPrice(800000)
                .totalPrice(880000)
                .build();

        updateRequest = OutsourcingUpdateRequest.builder()
                .status("진행")
                .content("수정된 외주 개발")
                .quantity(2)
                .unitPrice(900000)
                .supplyPrice(1800000)
                .totalPrice(1980000)
                .build();
    }

    private void setDetailContractId(DetailContract detailContract, Long id) {
        try {
            var field = DetailContract.class.getDeclaredField("detailContractId");
            field.setAccessible(true);
            field.set(detailContract, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setOutsourcingId(Outsourcing outsourcing, Long id) {
        try {
            var field = Outsourcing.class.getDeclaredField("outsourcingId");
            field.setAccessible(true);
            field.set(outsourcing, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("외주계약 생성 - 성공")
    void createOutsourcing_Success() {
        // given
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));
        given(correspondentRepository.findById(1L)).willReturn(Optional.of(correspondent));
        given(outsourcingRepository.save(any(Outsourcing.class))).willReturn(outsourcing);

        // when
        OutsourcingResponse response = outsourcingService.createOutsourcing(1L, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOutsourcingId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(OutsourcingStatus.TEMPORARY);
        verify(outsourcingRepository).save(any(Outsourcing.class));
    }

    @Test
    @DisplayName("외주계약 생성 - 실패 (이미 외주계약 존재)")
    void createOutsourcing_Failed_AlreadyExists() {
        // given
        detailContract.setHasOutsourcing(true);
        given(detailContractRepository.findById(1L)).willReturn(Optional.of(detailContract));

        // when & then
        assertThatThrownBy(() -> outsourcingService.createOutsourcing(1L, request))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("외주계약 조회 - 성공")
    void getOutsourcing_Success() {
        // given
        given(outsourcingRepository.findByDetailContractDetailContractId(1L))
                .willReturn(Optional.of(outsourcing));

        // when
        OutsourcingResponse response = outsourcingService.getOutsourcingByDetailContractId(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOutsourcingId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo("외주 개발");
    }

    @Test
    @DisplayName("외주계약 전체 수정 - 성공")
    void updateOutsourcing_Success() {
        // given
        given(outsourcingRepository.findById(1L)).willReturn(Optional.of(outsourcing));

        // when
        OutsourcingResponse response = outsourcingService.updateOutsourcing(1L, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("수정된 외주 개발");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualTo(1980000);
    }

    @Test
    @DisplayName("외주계약 부분 수정 - 상태만 변경")
    void updateOutsourcing_StatusOnly() {
        // given
        given(outsourcingRepository.findById(1L)).willReturn(Optional.of(outsourcing));
        OutsourcingUpdateRequest statusOnlyRequest = OutsourcingUpdateRequest.builder()
                .status("진행")
                .build();

        // when
        OutsourcingResponse response = outsourcingService.updateOutsourcing(1L, statusOnlyRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OutsourcingStatus.ONGOING);
        assertThat(response.getContent()).isEqualTo("외주 개발");  // 기존 값 유지
        assertThat(response.getQuantity()).isEqualTo(1);  // 기존 값 유지
    }

    @Test
    @DisplayName("외주계약 부분 수정 - 금액 정보만 변경")
    void updateOutsourcing_PriceOnly() {
        // given
        given(outsourcingRepository.findById(1L)).willReturn(Optional.of(outsourcing));
        OutsourcingUpdateRequest priceOnlyRequest = OutsourcingUpdateRequest.builder()
                .quantity(2)
                .unitPrice(900000)
                .supplyPrice(1800000)
                .totalPrice(1980000)
                .build();

        // when
        OutsourcingResponse response = outsourcingService.updateOutsourcing(1L, priceOnlyRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OutsourcingStatus.TEMPORARY);  // 기존 값 유지
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualTo(1980000);
    }

    @Test
    @DisplayName("외주계약 수정 - 실패 (취소된 계약)")
    void updateOutsourcing_Failed_WhenCanceled() {
        // given
        outsourcing.setStatus(OutsourcingStatus.CANCELED);
        given(outsourcingRepository.findById(1L)).willReturn(Optional.of(outsourcing));

        // when & then
        assertThatThrownBy(() -> outsourcingService.updateOutsourcing(1L, updateRequest))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("외주계약 삭제 - 성공")
    void deleteOutsourcing_Success() {
        // given
        given(outsourcingRepository.findById(1L)).willReturn(Optional.of(outsourcing));

        // when
        outsourcingService.deleteOutsourcing(1L);

        // then
        verify(outsourcingRepository).delete(outsourcing);
        assertThat(detailContract.isHasOutsourcing()).isFalse();
    }
}