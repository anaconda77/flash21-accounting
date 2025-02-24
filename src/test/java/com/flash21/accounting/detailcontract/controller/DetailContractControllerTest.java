package com.flash21.accounting.detailcontract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.detailcontract.service.DetailContractService;
import com.flash21.accounting.common.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DetailContractControllerTest {
    @InjectMocks
    private DetailContractController detailContractController;

    @Mock
    private DetailContractService detailContractService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DetailContractRequest request;
    private DetailContractResponse response;
    private DetailContractUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(detailContractController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        request = DetailContractRequest.builder()
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

        response = DetailContractResponse.builder()
                .detailContractId(1L)
                .contractId(1L)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("웹사이트 구축 세부계약")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .build();

        updateRequest = DetailContractUpdateRequest.builder()
                .status("진행")
                .content("수정된 웹사이트 구축")
                .quantity(2)
                .unitPrice(1500000)
                .supplyPrice(3000000)
                .totalPrice(3300000)
                .build();
    }

    @Test
    @DisplayName("세부계약서 생성 API 테스트")
    void createDetailContract() throws Exception {
        // given
        given(detailContractService.createDetailContract(any(DetailContractRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/detail-contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detailContractId").exists())
                .andExpect(jsonPath("$.content").value("웹사이트 구축 세부계약"))
                .andExpect(jsonPath("$.status").value("TEMPORARY"));
    }

    @Test
    @DisplayName("세부계약서 생성 API - 유효성 검증 실패")
    void createDetailContract_ValidationFail() throws Exception {
        // given
        DetailContractRequest invalidRequest = DetailContractRequest.builder()
                .contractId(null)  // 필수 값 누락
                .status("")
                .detailContractCategory("")
                .content("")
                .quantity(-1)      // 음수 값
                .unitPrice(0)      // 0원
                .build();

        // when & then
        mockMvc.perform(post("/api/detail-contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("세부계약서 단건 조회 API 테스트")
    void getDetailContract() throws Exception {
        // given
        given(detailContractService.getDetailContract(1L))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/detail-contract/{detailContractId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailContractId").value(1L))
                .andExpect(jsonPath("$.content").value("웹사이트 구축 세부계약"));
    }

    @Test
    @DisplayName("계약서별 세부계약서 목록 조회 API 테스트")
    void getDetailContractsByContractId() throws Exception {
        // given
        List<DetailContractResponse> responses = Arrays.asList(response);
        given(detailContractService.getDetailContractsByContractId(1L))
                .willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/detail-contract/contracts/{contractId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].detailContractId").value(1L))
                .andExpect(jsonPath("$[0].content").value("웹사이트 구축 세부계약"));
    }

    @Test
    @DisplayName("세부계약서 수정 API 테스트")
    void updateDetailContract() throws Exception {
        // given
        DetailContractResponse updatedResponse = DetailContractResponse.builder()
                .detailContractId(1L)
                .contractId(1L)
                .status(DetailContractStatus.ONGOING)
                .content("수정된 웹사이트 구축")
                .quantity(2)
                .unitPrice(1500000)
                .supplyPrice(3000000)
                .totalPrice(3300000)
                .build();

        given(detailContractService.updateDetailContract(eq(1L), any(DetailContractUpdateRequest.class)))
                .willReturn(updatedResponse);

        // when & then
        mockMvc.perform(put("/api/detail-contract/{detailContractId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONGOING"))
                .andExpect(jsonPath("$.content").value("수정된 웹사이트 구축"))
                .andExpect(jsonPath("$.totalPrice").value(3300000));
    }

    @Test
    @DisplayName("세부계약서 삭제 API 테스트")
    void deleteDetailContract() throws Exception {
        // given
        doNothing().when(detailContractService).deleteDetailContract(1L);

        // when & then
        mockMvc.perform(delete("/api/detail-contract/{detailContractId}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(detailContractService).deleteDetailContract(1L);
    }

    @Test
    @DisplayName("세부계약서 수정 API - 부분 수정 테스트")
    void updateDetailContract_PartialUpdate() throws Exception {
        // given
        DetailContractUpdateRequest partialUpdate = DetailContractUpdateRequest.builder()
                .status("진행")
                .content("부분 수정된 내용")
                .build();

        DetailContractResponse partialResponse = DetailContractResponse.builder()
                .detailContractId(1L)
                .contractId(1L)
                .status(DetailContractStatus.ONGOING)
                .content("부분 수정된 내용")
                .quantity(1)  // 기존 값 유지
                .unitPrice(1000000)  // 기존 값 유지
                .supplyPrice(1000000)  // 기존 값 유지
                .totalPrice(1100000)  // 기존 값 유지
                .build();

        given(detailContractService.updateDetailContract(eq(1L), any(DetailContractUpdateRequest.class)))
                .willReturn(partialResponse);

        // when & then
        mockMvc.perform(put("/api/detail-contract/{detailContractId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONGOING"))
                .andExpect(jsonPath("$.content").value("부분 수정된 내용"))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.totalPrice").value(1100000));
    }
}