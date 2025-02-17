package com.flash21.accounting.contract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flash21.accounting.contract.dto.request.ContractRequestDto;
import com.flash21.accounting.contract.dto.response.ContractResponseDto;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.service.ContractService;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.sign.entity.Sign;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ContractControllerTest {

    @InjectMocks
    private ContractController contractController;

    @Mock
    private ContractService contractService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User admin;
    private Correspondent correspondent;
    private Sign writerSign, headSign, directorSign;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contractController)
                .setControllerAdvice(new GlobalExceptionHandler())  // 예외 핸들러 추가
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate 직렬화 지원


        correspondent = Correspondent.builder()
                .id(1L)
                .correspondentName("테스트 업체")
                .businessRegNumber("123-45-67890")
                .address("서울시")
                .detailedAddress("강남구")
                .build();

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

        // Sign 생성
        writerSign = Sign.builder().signId(1L).user(admin).signType("작성자").signImage("sign1.png").build();
        headSign = Sign.builder().signId(2L).user(admin).signType("부장").signImage("sign2.png").build();
        directorSign = Sign.builder().signId(3L).user(admin).signType("이사").signImage("sign3.png").build();
    }

    @Test
    @DisplayName("계약서 생성 API 테스트")
    void createContractTest() throws Exception {
        // Given
        ContractRequestDto request = ContractRequestDto.builder()
                .adminId(admin.getId())
                .writerSignId(1)
                .headSignId(1)
                .directorSignId(1)
                .contractCategory(ContractCategory.NONE)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("메인콘텐트")
                .correspondentId(1)
                .build();

        ContractResponseDto response = ContractResponseDto.builder()
                .contractId(1L)
                .admin(admin)
                .lastModifyUser(admin)
                .registerDate(LocalDate.now())
                .contractCategory(ContractCategory.NONE)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("메인콘텐트")
                .correspondent(correspondent)
                .build();

        given(contractService.createContract(any(ContractRequestDto.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/contract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId").exists())
                .andExpect(jsonPath("$.name").value("테스트 계약"));
    }

    @Test
    @DisplayName("특정 계약서 조회 API 테스트")
    void getContractByIdTest() throws Exception {
        // Given
        Long contractId = 1L;
        ContractResponseDto response = ContractResponseDto.builder()
                .contractId(contractId)
                .admin(admin)
                .lastModifyUser(admin)
                .registerDate(LocalDate.now())
                .contractCategory(ContractCategory.NONE)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("메인콘텐트")
                .correspondent(correspondent)
                .build();

        given(contractService.getContractById(contractId)).willReturn(response);

        // When & Then
        mockMvc.perform(get("/api/contract/{contractId}", contractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId").value(contractId))
                .andExpect(jsonPath("$.name").value("테스트 계약"));
    }

    @Test
    @DisplayName("계약서 수정 API 테스트")
    void updateContractTest() throws Exception {
        // Given
        Long contractId = 1L;
        ContractRequestDto request = ContractRequestDto.builder()
                .adminId(admin.getId())
                .writerSignId(1)
                .headSignId(1)
                .directorSignId(1)
                .contractCategory(ContractCategory.NONE)
                .processStatus(ProcessStatus.DONE)
                .method(Method.BID)
                .name("수정된 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(15))
                .workEndDate(LocalDate.now().plusDays(45))
                .correspondentId(1)
                .mainContractContent("메인콘텐트")
                .build();

        ContractResponseDto response = ContractResponseDto.builder()
                .contractId(contractId)
                .admin(admin)
                .lastModifyUser(admin)
                .registerDate(LocalDate.now())
                .contractCategory(ContractCategory.NONE)
                .processStatus(ProcessStatus.DONE)
                .method(Method.BID)
                .name("수정된 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(15))
                .workEndDate(LocalDate.now().plusDays(45))
                .correspondent(correspondent)
                .build();

        given(contractService.updateContract(any(Long.class), any(ContractRequestDto.class))).willReturn(response);

        // When & Then
        mockMvc.perform(put("/api/contract/{contractId}", contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractId").value(contractId))
                .andExpect(jsonPath("$.name").value("수정된 계약"))
                .andExpect(jsonPath("$.processStatus").value(ProcessStatus.DONE.name()));
    }

    @Test
    @DisplayName("계약서 삭제 API 테스트")
    void deleteContractTest() throws Exception {
        // Given
        Long contractId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/contract/{contractId}", contractId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("계약서 전체 조회 API 테스트")
    void getAllContractsTest() throws Exception {
        // Given
        List<ContractResponseDto> contractList = Arrays.asList(
                ContractResponseDto.builder()
                        .contractId(1L)
                        .admin(admin)
                        .contractCategory(ContractCategory.NONE)
                        .processStatus(ProcessStatus.CONTRACTED)
                        .method(Method.GENERAL)
                        .name("테스트 계약")
                        .contractStartDate(LocalDate.now())
                        .contractEndDate(LocalDate.now().plusDays(30))
                        .workEndDate(LocalDate.now().plusDays(60))
                        .correspondent(correspondent)
                        .mainContractContent("메인콘텐트")
                        .build()
        );

        given(contractService.getAllContracts()).willReturn(contractList);

        // When & Then
        mockMvc.perform(get("/api/contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contractId").exists())
                .andExpect(jsonPath("$[0].name").value("테스트 계약"));
    }
}
