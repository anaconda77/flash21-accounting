package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.domain.repository.OutsourcingRepository;
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.UpdateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.response.CreateDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.GetDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.UpdateDetailContractResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DetailContractServiceTest {

    @Mock
    private DetailContractRepository detailContractRepository;

    @Mock
    private OutsourcingRepository outsourcingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private DetailContractService detailContractService;

    private CreateDetailContractRequest createRequest;
    private UpdateDetailContractRequest updateRequest;
    private DetailContract detailContract;

    @BeforeEach
    void setUp() {
        createRequest = CreateDetailContractRequest.builder()
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

        updateRequest = UpdateDetailContractRequest.builder()
                .contractType("일반")
                .contractStatus("완료")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발 수정")
                .quantity(2)
                .unitPrice(1000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .lastModifyUser("admin")
                .build();

        detailContract = DetailContract.builder()
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
    }

    @Test
    @DisplayName("세부계약서 생성 성공")
    void createDetailContractSuccess() {
        // given
        given(detailContractRepository.save(any(DetailContract.class))).willReturn(detailContract);

        // when
        CreateDetailContractResponse response = detailContractService.createDetailContract(createRequest);

        // then
        assertThat(response).isNotNull();
        verify(detailContractRepository).save(any(DetailContract.class));
    }

    @Test
    @DisplayName("세부계약서 조회 성공")
    void getDetailContractSuccess() {
        // given
        Long detailContractId = 1L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.of(detailContract));

        // when
        var response = detailContractService.getDetailContract(detailContractId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContractType()).isEqualTo("일반");
        assertThat(response.getContent()).isEqualTo("웹 개발");
    }

    @Test
    @DisplayName("존재하지 않는 세부계약서 조회 시 예외 발생")
    void getDetailContractNotFound() {
        // given
        Long detailContractId = 999L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> detailContractService.getDetailContract(detailContractId))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("상위계약서 ID로 세부계약서들 조회 성공")
    void getDetailContractsByParentContractIdSuccess() {
        // given
        Long parentContractId = 1L;
        DetailContract detailContract1 = DetailContract.builder()
                .contractId(parentContractId)
                .contractType("일반")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발 A")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .lastModifyUser("admin")
                .build();

        DetailContract detailContract2 = DetailContract.builder()
                .contractId(parentContractId)
                .contractType("외주")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발 B")
                .quantity(1)
                .unitPrice(2000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .lastModifyUser("admin")
                .build();

        given(detailContractRepository.findByContractId(parentContractId))
                .willReturn(List.of(detailContract1, detailContract2));

        // when
        List<GetDetailContractResponse> responses = detailContractService.getDetailContractByContractId(parentContractId);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("contractId")
                .containsOnly(parentContractId);
        assertThat(responses).extracting("content")
                .containsExactlyInAnyOrder("웹 개발 A", "웹 개발 B");
    }

    @Test
    @DisplayName("존재하지 않는 상위계약서 ID로 조회 시 예외 발생")
    void getDetailContractsByParentContractIdNotFound() {
        // given
        Long nonExistentParentId = 999L;
        given(detailContractRepository.findByContractId(nonExistentParentId))
                .willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() ->
                detailContractService.getDetailContractByContractId(nonExistentParentId))
                .isInstanceOf(AccountingException.class)
                .isInstanceOf(AccountingException.class)
                .hasFieldOrPropertyWithValue("errorCode", DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND);
    }

    @Test
    @DisplayName("세부계약서 수정 성공")
    void updateDetailContractSuccess() {
        // given
        Long detailContractId = 1L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.of(detailContract));

        // when
        UpdateDetailContractResponse response = detailContractService.updateDetailContract(detailContractId, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("수정이 완료되었습니다");
        verify(detailContractRepository).findById(detailContractId);
    }

    @Test
    @DisplayName("존재하지 않는 세부계약서 수정 시 예외 발생")
    void updateDetailContractNotFound() {
        // given
        Long detailContractId = 999L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> detailContractService.updateDetailContract(detailContractId, updateRequest))
                .isInstanceOf(AccountingException.class);
    }
}
