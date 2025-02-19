package com.flash21.accounting.contract.integration;

import com.flash21.accounting.contract.dto.request.ContractRequestDto;
import com.flash21.accounting.contract.dto.response.ContractResponseDto;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Rollback(false)
class ContractIntegrationTest {

    @LocalServerPort
    private int port;


    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorrespondentRepository correspondentRepository;

    @Autowired
    private ContractRepository contractRepository;

    private Long savedContractId;

    @BeforeEach
    void setUp() {
        User admin = userRepository.save(User.builder()
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
                .build());

        // correspondent_name을 유니크하게 변경
        String uniqueCorrespondentName = "테스트 업체_" + System.currentTimeMillis();

        Correspondent correspondent = correspondentRepository.save(Correspondent.builder()
                .correspondentName(uniqueCorrespondentName)
                .businessRegNumber("123-45-67890")
                .address("서울시")
                .detailedAddress("강남구")
                .build());

        // 계약 저장
        Contract contract = contractRepository.save(Contract.builder()
                .admin(admin)
                .lastModifyUser(admin)
                .contractCategory(ContractCategory.DEVELOP)
                .correspondent(correspondent)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .registerDate(LocalDate.now())
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("테스트")
                .build());

        // 계약이 제대로 저장되었는지 로그 추가
        System.out.println("저장된 계약 ID: " + contract.getContractId());

        // 계약 ID 저장
        savedContractId = contract.getContractId();

        // 계약 ID가 정상적으로 저장되었는지 검증
        if (savedContractId == null) {
            throw new IllegalStateException("계약이 정상적으로 저장되지 않았습니다.");
        }

        baseUrl = "http://localhost:" + port + "/api/contract";
    }



    @Test
    @DisplayName("계약서를 성공적으로 생성한다")
    void createContractTest() {
        // Given
        ContractRequestDto request = ContractRequestDto.builder()
                .adminId(1L)
                .contractCategory(ContractCategory.DEVELOP)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("플랫폼 개발 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusMonths(6))
                .workEndDate(LocalDate.now().plusMonths(9))
                .mainContractContent("계약 내용")
                .correspondentId(1)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContractRequestDto> requestEntity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<ContractResponseDto> responseEntity = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ContractResponseDto.class
        );

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getName()).isEqualTo("플랫폼 개발 계약");
    }

    @Test
    @DisplayName("저장된 계약서를 ID로 조회할 수 있어야 한다")
    void getContractByIdTest() {
        // Given
        Long contractId = 1L;

        // When
        ResponseEntity<ContractResponseDto> responseEntity = restTemplate.getForEntity(
                baseUrl + "/" + savedContractId,
                ContractResponseDto.class
        );

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getContractId()).isEqualTo(contractId);
    }

    @Test
    @DisplayName("계약서를 성공적으로 수정한다")
    void updateContractTest() {
        // Given
        Long contractId = 1L;
        ContractRequestDto updateRequest = ContractRequestDto.builder()
                .adminId(1L)
                .contractCategory(ContractCategory.DEVELOP)
                .processStatus(ProcessStatus.BILLING)
                .method(Method.BID)
                .name("수정된 계약")
                .contractStartDate(LocalDate.now().plusDays(1))
                .contractEndDate(LocalDate.now().plusMonths(6))
                .workEndDate(LocalDate.now().plusMonths(9))
                .mainContractContent("수정된 계약 내용")
                .correspondentId(1)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ContractRequestDto> requestEntity = new HttpEntity<>(updateRequest, headers);

        // When
        ResponseEntity<ContractResponseDto> responseEntity = restTemplate.exchange(
                baseUrl + "/" + savedContractId,
                HttpMethod.PUT,
                requestEntity,
                ContractResponseDto.class
        );

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getName()).isEqualTo("수정된 계약");
    }

    @Test
    @DisplayName("계약서를 삭제하면 조회되지 않아야 한다")
    void deleteContractTest() {
        // Given
        Long contractId = 1L;

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + savedContractId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ContractResponseDto> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + savedContractId,
                ContractResponseDto.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
