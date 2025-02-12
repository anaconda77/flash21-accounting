package com.flash21.accounting.detailcontract.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.common.GlobalExceptionHandler;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.UpdateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.response.CreateDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.GetDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.UpdateDetailContractResponse;
import com.flash21.accounting.detailcontract.service.DetailContractService;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class DetailContractControllerTest {
    @InjectMocks
    private DetailContractController detailContractController;

    @Mock
    private DetailContractService detailContractService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(detailContractController)
                .setControllerAdvice(new GlobalExceptionHandler())  // 글로벌 예외 핸들러 추가
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("세부계약서 생성 API 테스트")
    void createDetailContractTest() throws Exception {
        // given
        CreateDetailContractRequest request = CreateDetailContractRequest.builder()
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

        given(detailContractService.createDetailContract(any(CreateDetailContractRequest.class)))
                .willReturn(CreateDetailContractResponse.of(1L));

        // when & then
        mockMvc.perform(post("/api/detail-contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.contractId").exists());
    }

    @Test
    @DisplayName("세부계약서 조회 API 테스트")
    void getDetailContractTest() throws Exception {
        // given
        Long detailContractId = 1L;
        GetDetailContractResponse response = GetDetailContractResponse.builder()
                .detailContractId(detailContractId)
                .contractType("일반")
                .content("웹 개발")
                .build();

        given(detailContractService.getDetailContract(detailContractId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/detail-contract/{detailContractId}", detailContractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailContractId").value(detailContractId))
                .andExpect(jsonPath("$.contractType").value("일반"))
                .andExpect(jsonPath("$.content").value("웹 개발"));
    }

    @Test
    @DisplayName("세부계약서 수정 API 테스트")
    void updateDetailContractTest() throws Exception {
        // given
        Long detailContractId = 1L;
        UpdateDetailContractRequest request = UpdateDetailContractRequest.builder()
                .contractType("일반")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발 수정")
                .quantity(2)
                .unitPrice(1000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .lastModifyUser("admin")
                .build();

        given(detailContractService.updateDetailContract(eq(detailContractId), any(UpdateDetailContractRequest.class)))
                .willReturn(UpdateDetailContractResponse.success());

        // when & then
        mockMvc.perform(put("/api/detail-contract/{detailContractId}", detailContractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("수정이 완료되었습니다"));
    }

    @Test
    @DisplayName("상위계약서 ID로 세부계약서들 조회 API 테스트")
    void getDetailContractsByParentContractIdTest() throws Exception {
        // given
        Long parentContractId = 1L;
        List<GetDetailContractResponse> responses = List.of(
                GetDetailContractResponse.builder()
                        .detailContractId(1L)
                        .contractId(parentContractId)
                        .contractType("일반")
                        .content("웹 개발 A")
                        .build(),
                GetDetailContractResponse.builder()
                        .detailContractId(2L)
                        .contractId(parentContractId)
                        .contractType("외주")
                        .content("웹 개발 B")
                        .build()
        );

        given(detailContractService.getDetailContractByContractId(parentContractId))
                .willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/detail-contract/by-contract/{contractId}", parentContractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].contractId").value(parentContractId))
                .andExpect(jsonPath("$[0].content").value("웹 개발 A"))
                .andExpect(jsonPath("$[1].contractId").value(parentContractId))
                .andExpect(jsonPath("$[1].content").value("웹 개발 B"));
    }

    @Test
    @DisplayName("존재하지 않는 상위계약서 ID로 조회 시 404 응답")
    void getDetailContractsByParentContractIdNotFoundTest() throws Exception {
        // given
        Long nonExistentParentId = 999L;
        given(detailContractService.getDetailContractByContractId(nonExistentParentId))
                .willThrow(new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/detail-contract/by-contract/{contractId}", nonExistentParentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND.code()))
                .andExpect(jsonPath("$.message").value(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND.message()))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("유효하지 않은 입력값으로 세부계약서 생성 시 실패")
    void createDetailContractWithInvalidInputTest() throws Exception {
        // given
        CreateDetailContractRequest request = CreateDetailContractRequest.builder()
                .contractId(1L)
                // required fields missing
                .build();

        // when & then
        mockMvc.perform(post("/api/detail-contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
