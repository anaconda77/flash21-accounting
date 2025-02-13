package com.flash21.accounting.contract.service;

import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.category.repository.CategoryRepository;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.contract.dto.ContractRequestDto;
import com.flash21.accounting.contract.dto.ContractResponseDto;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.entity.Status;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.sign.entity.Sign;
import com.flash21.accounting.sign.repository.SignRepository;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @InjectMocks
    private ContractServiceImpl contractService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CorrespondentRepository correspondentRepository;

    @Mock
    private SignRepository signRepository;

    private User admin;
    private Category category;
    private Correspondent correspondent;
    private Contract contract;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("IT 서비스")
                .build();

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

        contract = Contract.builder()
                .admin(admin)
                .category(category)
                .correspondent(correspondent)
                .status(Status.ONGOING)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .build();
    }

    @Test
    @DisplayName("계약서를 성공적으로 생성한다")
    void createContractSuccess() {
        // Given
        ContractRequestDto request = ContractRequestDto.builder()
                .adminId(1L)
                .writerSignId(1)
                .headSignId(2)
                .directorSignId(3)
                .categoryId(1L)
                .status(Status.ONGOING)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .correspondentId(1)
                .build();

        // 서명(Sign) 객체 생성
        Sign writerSign = Sign.builder().signId(1L).user(admin).signType("작성자").signImage("sign1.png").build();
        Sign headSign = Sign.builder().signId(2L).user(admin).signType("부장").signImage("sign2.png").build();
        Sign directorSign = Sign.builder().signId(3L).user(admin).signType("이사").signImage("sign3.png").build();

        // 필요한 데이터 Mock 설정 추가
        given(userRepository.findById(1L)).willReturn(Optional.of(admin));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(correspondentRepository.findById(1L)).willReturn(Optional.of(correspondent));

        // 서명(Sign) 객체를 Mock으로 설정
        given(signRepository.findById(1)).willReturn(Optional.of(writerSign));
        given(signRepository.findById(2)).willReturn(Optional.of(headSign));
        given(signRepository.findById(3)).willReturn(Optional.of(directorSign));

        given(contractRepository.save(any(Contract.class))).willReturn(contract);

        // When
        ContractResponseDto response = contractService.createContract(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("테스트 계약");
        verify(contractRepository).save(any(Contract.class));
    }


    @Test
    @DisplayName("존재하지 않는 ID로 계약 조회 시 예외 발생")
    void getContractByIdNotFound() {
        // Given
        given(contractRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> contractService.getContractById(999L))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining(ContractErrorCode.CONTRACT_NOT_FOUND.message());
    }

    @Test
    @DisplayName("ID를 통해 계약서를 조회한다")
    void getContractByIdSuccess() {
        // Given
        given(contractRepository.findById(1L)).willReturn(Optional.of(contract));

        // When
        ContractResponseDto response = contractService.getContractById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("테스트 계약");
    }

    @Test
    @DisplayName("전체 계약서를 조회한다")
    void getAllContractsSuccess() {
        // Given
        given(contractRepository.findAll()).willReturn(List.of(contract));

        // When
        List<ContractResponseDto> responseList = contractService.getAllContracts();

        // Then
        assertThat(responseList).isNotEmpty();
        assertThat(responseList.get(0).getName()).isEqualTo("테스트 계약");
    }

    @Test
    @DisplayName("계약서를 성공적으로 수정한다")
    void updateContractSuccess() {
        // Given
        ContractRequestDto request = ContractRequestDto.builder()
                .status(Status.DONE)
                .processStatus(ProcessStatus.BILLING)
                .method(Method.BID)
                .name("수정된 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(45))
                .workEndDate(LocalDate.now().plusDays(75))
                .categoryId(1L)
                .correspondentId(1)
                .build();

        given(contractRepository.findById(1L)).willReturn(Optional.of(contract));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(correspondentRepository.findById(1L)).willReturn(Optional.of(correspondent));

        // When
        ContractResponseDto response = contractService.updateContract(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.DONE);
        assertThat(response.getProcessStatus()).isEqualTo(ProcessStatus.BILLING);
        verify(contractRepository).findById(1L);
    }

    @Test
    @DisplayName("계약서를 삭제하면 조회되지 않아야 한다")
    void deleteContractSuccess() {
        // Given
        given(contractRepository.existsById(1L)).willReturn(true);

        // When
        contractService.deleteContract(1L);

        // Then
        verify(contractRepository).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 계약서 삭제 시 예외 발생")
    void deleteContractNotFound() {
        // Given
        given(contractRepository.existsById(999L)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> contractService.deleteContract(999L))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining(ContractErrorCode.CONTRACT_NOT_FOUND.message());
    }
}
