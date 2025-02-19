package com.flash21.accounting.detailcontract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.detailcontract.service.DetailContractService;
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.owner.domain.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DetailContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DetailContractService detailContractService;

    private String token;
    private DetailContractRequest testRequest;
    private DetailContractResponse testResponse;
    private DetailContractUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() throws Exception {
        System.out.println("=== 시작: 사용자 로그인 또는 회원가입 ===");

        MvcResult loginResult = performLogin("testuser", "password123");
        System.out.println("로그인 응답 상태: " + loginResult.getResponse().getStatus());
        System.out.println("로그인 응답 본문: " + loginResult.getResponse().getContentAsString());

        if (loginResult.getResponse().getStatus() != 200) {
            System.out.println("로그인 실패: 사용자 등록 시도 중...");
            registerUser("testuser", "password123");
            loginResult = performLogin("testuser", "password123");
            System.out.println("재로그인 응답 상태: " + loginResult.getResponse().getStatus());
            System.out.println("재로그인 응답 본문: " + loginResult.getResponse().getContentAsString());
        }

        token = loginResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("발급된 토큰: " + token);
        System.out.println("=== 종료: 사용자 로그인 또는 회원가입 ===");

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

    private void registerUser(String username, String password) throws Exception {
        String requestBody = "{" +
                "\"username\": \"" + username + "\"," +
                "\"password\": \"" + password + "\"," +
                "\"name\": \"John Doe\"," +
                "\"phoneNumber\": \"010-1234-5678\"," +
                "\"email\": \"john@example.com\"," +
                "\"address\": \"Seoul\"," +
                "\"addressDetail\": \"Apt 101\"," +
                "\"role\": \"ROLE_ADMIN\"," +
                "\"grade\": \"Grade A\"," +
                "\"companyPhoneNumber\": \"02-1234-5678\"," +
                "\"companyFaxNumber\": \"02-8765-4321\"}";

        System.out.println("회원가입 요청 본문: " + requestBody);

        MvcResult result = mockMvc.perform(post("/application/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        System.out.println("회원가입 응답 상태: " + result.getResponse().getStatus());
        System.out.println("회원가입 응답 본문: " + result.getResponse().getContentAsString());
    }

    private MvcResult performLogin(String username, String password) throws Exception {
        return mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", password))
                .andReturn();
    }

    @Test
    @DisplayName("세부계약서 생성 - 성공")
    void createDetailContract_Success() throws Exception {
        Owner owner = OwnerFixture.createDefault(); // OwnerFixture 사용

        given(detailContractService.createDetailContract(any(DetailContractRequest.class)))
                .willReturn(testResponse);

        mockMvc.perform(post("/api/detail-contract")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detailContractId").value(testResponse.getDetailContractId()))
                .andExpect(jsonPath("$.status").value(testResponse.getStatus().name()))
                .andExpect(jsonPath("$.detailContractCategory").value(testResponse.getDetailContractCategory().name()));
    }

    @Test
    @DisplayName("세부계약서 단건 조회 - 성공")
    void getDetailContract_Success() throws Exception {
        given(detailContractService.getDetailContract(1L))
                .willReturn(testResponse);

        mockMvc.perform(get("/api/detail-contract/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailContractId").value(testResponse.getDetailContractId()))
                .andExpect(jsonPath("$.status").value(testResponse.getStatus().name()))
                .andExpect(jsonPath("$.content").value(testResponse.getContent()));
    }

    @Test
    @DisplayName("계약서 ID로 세부계약서 목록 조회 - 성공")
    void getDetailContractsByContractId_Success() throws Exception {
        List<DetailContractResponse> responses = List.of(testResponse);
        given(detailContractService.getDetailContractsByContractId(1L))
                .willReturn(responses);

        mockMvc.perform(get("/api/detail-contract/contracts/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].detailContractId").value(testResponse.getDetailContractId()))
                .andExpect(jsonPath("$[0].status").value(testResponse.getStatus().name()))
                .andExpect(jsonPath("$[0].content").value(testResponse.getContent()));
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
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailContractId").value(updatedResponse.getDetailContractId()))
                .andExpect(jsonPath("$.status").value(updatedResponse.getStatus().name()))
                .andExpect(jsonPath("$.content").value(updatedResponse.getContent()));
    }

    @Test
    @DisplayName("세부계약서 삭제 - 성공")
    void deleteDetailContract_Success() throws Exception {
        doNothing().when(detailContractService).deleteDetailContract(1L);

        mockMvc.perform(delete("/api/detail-contract/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("세부계약서 생성 요청 검증 실패")
    void createDetailContract_ValidationFail() throws Exception {
        DetailContractRequest invalidRequest = DetailContractRequest.builder()
                .contractId(null)
                .status(null)
                .detailContractCategory(null)
                .content("")
                .quantity(-1)
                .unitPrice(0)
                .supplyPrice(-1000)
                .totalPrice(0)
                .build();

        mockMvc.perform(post("/api/detail-contract")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}