package com.flash21.accounting.detailcontract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.detailcontract.service.DetailContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DetailContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DetailContractService detailContractService;

    @Autowired
    private ObjectMapper objectMapper;

    private DetailContractRequest testRequest;
    private DetailContractResponse testResponse;
    private DetailContractUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testRequest = DetailContractRequest.builder()
                .contractId(1L)
                .status(DetailContractStatus.TEMPORARY)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .content("테스트 세부계약 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .build();

        testResponse = DetailContractResponse.builder()
                .detailContractId(1L)
                .contractId(1L)
                .detailContractCategory(DetailContractCategory.WEBSITE_CONSTRUCTION)
                .status(DetailContractStatus.TEMPORARY)
                .content("테스트 세부계약 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .paymentMethod("계좌이체")
                .paymentCondition("선금 50%, 잔금 50%")
                .build();

        testUpdateRequest = DetailContractUpdateRequest.builder()
                .status(DetailContractStatus.ONGOING)
                .content("수정된 내용")
                .build();
    }

    @Test
    @DisplayName("세부계약서 생성 - 성공")
    void createDetailContract_Success() throws Exception {
        given(detailContractService.createDetailContract(any(DetailContractRequest.class)))
                .willReturn(testResponse);

        mockMvc.perform(post("/api/detail-contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detailContractId").value(testResponse.getDetailContractId()))
                .andExpect(jsonPath("$.status").value(testResponse.getStatus().toString()))
                .andExpect(jsonPath("$.detailContractCategory").value(testResponse.getDetailContractCategory().toString()));

        verify(detailContractService).createDetailContract(any(DetailContractRequest.class));
    }

    @Test
    @DisplayName("세부계약서 단건 조회 - 성공")
    void getDetailContract_Success() throws Exception {
        given(detailContractService.getDetailContract(1L))
                .willReturn(testResponse);

        mockMvc.perform(get("/api/detail-contract/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailContractId").value(testResponse.getDetailContractId()))
                .andExpect(jsonPath("$.status").value(testResponse.getStatus().toString()))
                .andExpect(jsonPath("$.content").value(testResponse.getContent()));

        verify(detailContractService).getDetailContract(1L);
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회 - 성공")
    void getDetailContractsByContractId_Success() throws Exception {
        List<DetailContractResponse> responses = List.of(testResponse);
        given(detailContractService.getDetailContractsByContractId(1L))
                .willReturn(responses);

        mockMvc.perform(get("/api/detail-contract/contracts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].detailContractId").value(testResponse.getDetailContractId()))
                .andExpect(jsonPath("$[0].status").value(testResponse.getStatus().toString()))
                .andExpect(jsonPath("$[0].content").value(testResponse.getContent()));

        verify(detailContractService).getDetailContractsByContractId(1L);
    }

    @Test
    @DisplayName("세부계약서 수정 - 성공")
    void updateDetailContract_Success() throws Exception {
        DetailContractResponse updatedResponse = DetailContractResponse.builder()
                .detailContractId(1L)
                .contractId(1L)
                .status(DetailContractStatus.ONGOING)
                .content("수정된 내용")
                .build();

        given(detailContractService.updateDetailContract(eq(1L), any(DetailContractUpdateRequest.class)))
                .willReturn(updatedResponse);

        mockMvc.perform(put("/api/detail-contract/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailContractId").value(updatedResponse.getDetailContractId()))
                .andExpect(jsonPath("$.status").value(updatedResponse.getStatus().toString()))
                .andExpect(jsonPath("$.content").value(updatedResponse.getContent()));

        verify(detailContractService).updateDetailContract(eq(1L), any(DetailContractUpdateRequest.class));
    }

    @Test
    @DisplayName("세부계약서 삭제 - 성공")
    void deleteDetailContract_Success() throws Exception {
        doNothing().when(detailContractService).deleteDetailContract(1L);

        mockMvc.perform(delete("/api/detail-contract/1"))
                .andExpect(status().isNoContent());

        verify(detailContractService).deleteDetailContract(1L);
    }

    @Test
    @DisplayName("세부계약서 생성 요청 검증 실패")
    void createDetailContract_ValidationFail() throws Exception {
        DetailContractRequest invalidRequest = DetailContractRequest.builder()
                .contractId(null)
                .detailContractCategory(null)
                .content("테스트 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .build();

        mockMvc.perform(post("/api/detail-contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
