package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.category.repository.CategoryRepository;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.Outsourcing;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class DetailContractRepositoryTest {

    @Autowired
    private DetailContractRepository detailContractRepository;

    @Autowired
    private OutsourcingRepository outsourcingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorrespondentRepository correspondentRepository;

    @Autowired
    private CategoryRepository categoryRepository; // 추가: CategoryRepository
    @Autowired
    private AttachmentFileRepository attachmentFileRepository;

    @Test
    @DisplayName("세부계약서 저장 시 외주/지불정보도 함께 저장되어야 한다")
    void saveDetailContractWithOutsourcingAndPayment() {
        // given
        Contract contract = createAndSaveContract();
        DetailContract detailContract = createDetailContract(contract);
        Outsourcing outsourcing = createOutsourcing(detailContract);
        Payment payment = createPayment(detailContract);

        detailContract.getOutsourcings().add(outsourcing);
        detailContract.getPayments().add(payment);

        // when
        DetailContract savedContract = detailContractRepository.save(detailContract);

        // then
        assertThat(savedContract.getDetailContractId()).isNotNull();
        assertThat(savedContract.getOutsourcings()).hasSize(1);
        assertThat(savedContract.getPayments()).hasSize(1);
    }

    @Test
    @DisplayName("상위계약서 ID로 여러 세부계약서 조회 테스트")
    void findMultipleDetailContractsByContractId() {
        // given
        Contract contract = createAndSaveContract();
        DetailContract detailContract1 = createDetailContract(contract, "일반", "웹 개발 A");
        DetailContract detailContract2 = createDetailContract(contract, "외주", "웹 개발 B");
        detailContractRepository.saveAll(List.of(detailContract1, detailContract2));

        // when
        List<DetailContract> foundContracts = detailContractRepository.findByContract_ContractId(contract.getContractId());

        // then
        assertThat(foundContracts).hasSize(2);
        assertThat(foundContracts).extracting("content")
                .containsExactlyInAnyOrder("웹 개발 A", "웹 개발 B");
    }

    @Test
    @DisplayName("세부계약서 저장 시 첨부파일 연관관계 테스트")
    void saveDetailContractWithAttachmentFiles() {
        // given
        Contract contract = createAndSaveContract();
        DetailContract detailContract = createDetailContract(contract);
        DetailContract savedContract = detailContractRepository.save(detailContract);

        AttachmentFile attachmentFile = new AttachmentFile(
                detailContract.getDetailContractId(),
                "test.pdf",
                "/path/to/file",
                "application/pdf",
                APINumber.OUTSOURCING,
                null
        );

        // when
        AttachmentFile savedFile = attachmentFileRepository.save(attachmentFile);

        // then
        assertThat(savedContract.getDetailContractId()).isNotNull();
        List<AttachmentFile> foundFiles = attachmentFileRepository
                .findByReferenceId(savedContract.getDetailContractId());
        assertThat(foundFiles).hasSize(1);
        assertThat(foundFiles.get(0).getFileName()).isEqualTo("test.pdf");
    }

    private Contract createAndSaveContract() {
        User savedUser = userRepository.save(createUser());
        Correspondent savedCorrespondent = correspondentRepository.save(createCorrespondent());

        Contract contract = Contract.builder()
                .contractCategory(ContractCategory.NONE)
                .name("테스트 계약")
                .mainContractContent("테스트")
                .lastModifyUser(savedUser)
                .registerDate(LocalDate.now())
                .contractStartDate(LocalDate.now())
                .processStatus(ProcessStatus.CONTRACTED) // 변경된 ENUM 사용
                .method(Method.GENERAL) // 변경된 ENUM 사용
                .admin(savedUser)
                .correspondent(savedCorrespondent)
                .build();
        return contractRepository.save(contract);
    }

    private User createUser() {
        return User.builder()
                .username("testuser")
                .password("password")
                .name("Test User")
                .phoneNumber("010-1234-5678")
                .email("test@example.com")
                .address("Test Address")
                .addressDetail("Test Detail")
                .role(Role.ROLE_ADMIN)
                .grade("A")
                .companyPhoneNumber("02-1234-5678")
                .companyFaxNumber("02-1234-5679")
                .build();
    }

    private Correspondent createCorrespondent() {
        return Correspondent.builder()
                .correspondentName("Test Company")
                .businessRegNumber("123-45-67890")
                .address("Test Address")
                .detailedAddress("Test Detailed Address")
                .build();
    }

    private DetailContract createDetailContract(Contract contract) {
        return DetailContract.builder()
                .contract(contract)
                .contractType("일반계약")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("테스트")
                .quantity(1)
                .unitPrice(1000)
                .supplyPrice(1000)
                .totalPrice(1100)
                .lastModifyUser("tester")
                .build();
    }

    private DetailContract createDetailContract(Contract contract, String type, String content) {
        return DetailContract.builder()
                .contract(contract)
                .contractType(type)
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content(content)
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .lastModifyUser("tester")
                .build();
    }

    private Outsourcing createOutsourcing(DetailContract detailContract) {
        return Outsourcing.builder()
                .detailContract(detailContract)
                .outsourcingName("외주1")
                .content("외주내용")
                .quantity(1)
                .unitPrice(1000)
                .supplyPrice(1000)
                .totalAmount(1100)
                .build();
    }

    private Payment createPayment(DetailContract detailContract) {
        return Payment.builder()
                .detailContract(detailContract)
                .method("카드")
                .condition("선결제")
                .build();
    }
}




