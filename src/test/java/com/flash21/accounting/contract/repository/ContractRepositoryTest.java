package com.flash21.accounting.contract.repository;

import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.category.repository.CategoryRepository;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ContractRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CorrespondentRepository correspondentRepository;

    @Test
    @DisplayName("계약서를 저장하면 ID가 생성되어야 한다")
    void saveContract() {
        // Given
        User admin = createAndSaveUser();
        Correspondent correspondent = createAndSaveCorrespondent();

        Contract contract = Contract.builder()
                .admin(admin)
                .lastModifyUser(admin)
                .registerDate(LocalDate.now())
                .contractCategory(ContractCategory.NONE)
                .correspondent(correspondent)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("테스트")
                .build();

        // When
        Contract savedContract = contractRepository.save(contract);

        // Then
        assertThat(savedContract.getContractId()).isNotNull();
        assertThat(savedContract.getName()).isEqualTo("테스트 계약");
    }

    @Test
    @DisplayName("저장된 계약서를 ID로 조회할 수 있어야 한다")
    void findById() {
        // Given
        User admin = createAndSaveUser();
        Correspondent correspondent = createAndSaveCorrespondent();

        Contract contract = contractRepository.save(Contract.builder()
                .admin(admin)
                .lastModifyUser(admin)
                .registerDate(LocalDate.now())
                .contractCategory(ContractCategory.NONE)
                .correspondent(correspondent)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("테스트")
                .build());

        // When
        Optional<Contract> foundContract = contractRepository.findById(contract.getContractId());

        // Then
        assertThat(foundContract).isPresent();
        assertThat(foundContract.get().getName()).isEqualTo("테스트 계약");
    }

    @Test
    @DisplayName("진행 중인 계약서를 조회할 수 있어야 한다")
    void findByStatus() {
        // Given
        User admin = createAndSaveUser();
        Category category = createAndSaveCategory();
        Correspondent correspondent = createAndSaveCorrespondent();

        contractRepository.save(Contract.builder()
                .admin(admin)
                .lastModifyUser(admin)
                .registerDate(LocalDate.now())
                .contractCategory(ContractCategory.NONE)
                .correspondent(correspondent)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("진행 중 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("테스트")
                .build());

        // When
        List<Contract> contracts = contractRepository.findAll();

        // Then
        assertThat(contracts).isNotEmpty();
    }

    @Test
    @DisplayName("계약서를 삭제하면 조회되지 않아야 한다")
    void deleteContract() {
        // Given
        User admin = createAndSaveUser();
        Category category = createAndSaveCategory();
        Correspondent correspondent = createAndSaveCorrespondent();

        Contract contract = contractRepository.save(Contract.builder()
                .admin(admin)
                .lastModifyUser(admin)
                .registerDate(LocalDate.now())
                .contractCategory(ContractCategory.NONE)
                .correspondent(correspondent)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
                .name("삭제할 계약")
                .contractStartDate(LocalDate.now())
                .contractEndDate(LocalDate.now().plusDays(30))
                .workEndDate(LocalDate.now().plusDays(60))
                .mainContractContent("테스트")
                .build());

        // When
        contractRepository.delete(contract);
        Optional<Contract> foundContract = contractRepository.findById(contract.getContractId());

        // Then
        assertThat(foundContract).isEmpty();
    }

    private User createAndSaveUser() {
        User user = User.builder()
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
        return userRepository.save(user);
    }

    private Category createAndSaveCategory() {
        Category category = Category.builder()
                .name("IT 서비스")
                .build();
        return categoryRepository.save(category);
    }

    private Correspondent createAndSaveCorrespondent() {
        Correspondent correspondent = Correspondent.builder()
                .correspondentName("테스트 업체")
                .businessRegNumber("123-45-67890")
                .address("서울시")
                .detailedAddress("강남구")
                .build();
        return correspondentRepository.save(correspondent);
    }
}
