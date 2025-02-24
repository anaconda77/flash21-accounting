package com.flash21.accounting.outsourcing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flash21.accounting.common.GlobalExceptionHandler;
import com.flash21.accounting.outsourcing.domain.entity.OutsourcingStatus;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingRequest;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingUpdateRequest;
import com.flash21.accounting.outsourcing.dto.response.OutsourcingResponse;
import com.flash21.accounting.outsourcing.service.OutsourcingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OutsourcingControllerTest {

    @InjectMocks
    private OutsourcingController outsourcingController;

    @Mock
    private OutsourcingService outsourcingService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OutsourcingRequest request;
    private OutsourcingResponse response;
    private OutsourcingUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(outsourcingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        request = OutsourcingRequest.builder()
                .correspondentId(1L)
                .status("임시")
                .content("외주 개발")
                .quantity(1)
                .unitPrice(800000)
                .supplyPrice(800000)
                .totalPrice(880000)
                .build();

        response = OutsourcingResponse.builder()
                .outsourcingId(1L)
                .correspondentId(1L)
                .detailContractId(1L)
                .status(OutsourcingStatus.TEMPORARY)
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


    @Test
    @DisplayName("외주계약 생성 API")
    void createOutsourcing() throws Exception {
        // given
        given(outsourcingService.createOutsourcing(eq(1L), any(OutsourcingRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/outsourcing/detail-contracts/{detailContractId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.outsourcingId").exists())
                .andExpect(jsonPath("$.content").value("외주 개발"))
                .andExpect(jsonPath("$.status").value("TEMPORARY"));
    }

    @Test
    @DisplayName("외주계약 생성 API - 유효성 검증 실패")
    void createOutsourcing_ValidationFail() throws Exception {
        // given
        OutsourcingRequest invalidRequest = OutsourcingRequest.builder()
                .correspondentId(null)  // 필수 값 누락
                .status("")
                .content("")
                .quantity(-1)      // 음수 값
                .unitPrice(0)      // 0원
                .build();

        // when & then
        mockMvc.perform(post("/api/outsourcing/detail-contracts/{detailContractId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("세부계약서 ID로 외주계약 조회 API")
    void getOutsourcingByDetailContractId() throws Exception {
        // given
        given(outsourcingService.getOutsourcingByDetailContractId(1L))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/outsourcing/detail-contracts/{detailContractId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outsourcingId").value(1L))
                .andExpect(jsonPath("$.content").value("외주 개발"));
    }

    @Test
    @DisplayName("외주계약 수정 API - 전체 수정")
    void updateOutsourcing() throws Exception {
        // given
        OutsourcingResponse updatedResponse = OutsourcingResponse.builder()
                .outsourcingId(1L)
                .correspondentId(1L)
                .detailContractId(1L)
                .status(OutsourcingStatus.ONGOING)
                .content("수정된 외주 개발")
                .quantity(2)
                .unitPrice(900000)
                .supplyPrice(1800000)
                .totalPrice(1980000)
                .build();

        given(outsourcingService.updateOutsourcing(eq(1L), any(OutsourcingUpdateRequest.class)))
                .willReturn(updatedResponse);

        // when & then
        mockMvc.perform(put("/api/outsourcing/{outsourcingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONGOING"))
                .andExpect(jsonPath("$.content").value("수정된 외주 개발"))
                .andExpect(jsonPath("$.totalPrice").value(1980000));
    }

    @Test
    @DisplayName("외주계약 수정 API - 상태만 변경")
    void updateOutsourcing_StatusOnly() throws Exception {
        // given
        OutsourcingUpdateRequest statusOnlyRequest = OutsourcingUpdateRequest.builder()
                .status("진행")
                .build();

        OutsourcingResponse updatedResponse = OutsourcingResponse.builder()
                .outsourcingId(1L)
                .correspondentId(1L)
                .detailContractId(1L)
                .status(OutsourcingStatus.ONGOING)
                .content("외주 개발")  // 기존 값 유지
                .quantity(1)           // 기존 값 유지
                .unitPrice(800000)     // 기존 값 유지
                .supplyPrice(800000)   // 기존 값 유지
                .totalPrice(880000)    // 기존 값 유지
                .build();

        given(outsourcingService.updateOutsourcing(eq(1L), any(OutsourcingUpdateRequest.class)))
                .willReturn(updatedResponse);

        // when & then
        mockMvc.perform(put("/api/outsourcing/{outsourcingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusOnlyRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONGOING"))
                .andExpect(jsonPath("$.content").value("외주 개발"))  // 기존 값 유지 확인
                .andExpect(jsonPath("$.totalPrice").value(880000));   // 기존 값 유지 확인
    }

    @Test
    @DisplayName("외주계약 삭제 API")
    void deleteOutsourcing() throws Exception {
        // given
        doNothing().when(outsourcingService).deleteOutsourcing(1L);

        // when & then
        mockMvc.perform(delete("/api/outsourcing/{outsourcingId}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(outsourcingService).deleteOutsourcing(1L);
    }
}