package com.flash21.accounting.owner.controller;

import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.dto.request.OwnerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.owner.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OwnerRepository ownerRepository;

    private String token;
    private OwnerRequest ownerRequest;

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

        ownerRequest = new OwnerRequest(
                "John Doe",
                "010-1234-5678",
                "john@example.com",
                "02-987-6543"
        );

        createOwner();
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
        System.out.println("로그인 요청 본문 (x-www-form-urlencoded 형식): username=" + username + ", password=" + password);

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", password))
                .andReturn();

        System.out.println("로그인 시도 완료: 상태 " + result.getResponse().getStatus());
        return result;
    }

    @Test
    @DisplayName("POST /api/owner - Create Owner with Valid Token")
    void createOwner() throws Exception {
        System.out.println("=== 시작: Create Owner API 호출 ===");

        MvcResult result = mockMvc.perform(post("/api/owner")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andReturn();

        System.out.println("Create Owner 응답 상태: " + result.getResponse().getStatus());
        System.out.println("Create Owner 응답 본문: " + result.getResponse().getContentAsString());
        System.out.println("=== 종료: Create Owner API 호출 ===");
    }

    @Test
    @DisplayName("GET /api/owner - Get All Owners with Valid Token")
    void getAllOwners() throws Exception {
        System.out.println("=== 시작: Get All Owners API 호출 ===");

        MvcResult result = mockMvc.perform(get("/api/owner")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))))
                .andReturn();

        System.out.println("Get All Owners 응답 상태: " + result.getResponse().getStatus());
        System.out.println("Get All Owners 응답 본문: " + result.getResponse().getContentAsString());
        System.out.println("=== 종료: Get All Owners API 호출 ===");
    }

    @Test
    @DisplayName("GET /api/owner/{id} - Get Owner By ID with Valid Token")
    void getOwnerById() throws Exception {
        System.out.println("=== 시작: Get Owner By ID API 호출 ===");

        MvcResult result = mockMvc.perform(get("/api/owner/{id}", 6L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andReturn();

        System.out.println("Get Owner By ID 응답 상태: " + result.getResponse().getStatus());
        System.out.println("Get Owner By ID 응답 본문: " + result.getResponse().getContentAsString());
        System.out.println("=== 종료: Get Owner By ID API 호출 ===");
    }

    @Test
    @DisplayName("PUT /api/owner/{id} - Update Owner with Valid Token")
    void updateOwner() throws Exception {
        System.out.println("=== 시작: Update Owner API 호출 ===");

        MvcResult result = mockMvc.perform(put("/api/owner/{id}", 2L)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andReturn();

        System.out.println("Update Owner 응답 상태: " + result.getResponse().getStatus());
        System.out.println("Update Owner 응답 본문: " + result.getResponse().getContentAsString());
        System.out.println("=== 종료: Update Owner API 호출 ===");
    }

    @Test
    @DisplayName("DELETE /api/owner/{id} - Delete Owner with Valid Token")
    void deleteOwner() throws Exception {
        System.out.println("=== 시작: Delete Owner API 호출 ===");

        MvcResult result = mockMvc.perform(delete("/api/owner/{id}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Delete Owner 응답 상태: " + result.getResponse().getStatus());
        System.out.println("Delete Owner 응답 본문: " + result.getResponse().getContentAsString());
        System.out.println("=== 종료: Delete Owner API 호출 ===");
    }
}