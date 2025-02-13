package com.flash21.accounting.detailcontract;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.category.repository.CategoryRepository;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.entity.Status;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest;
import com.flash21.accounting.detailcontract.service.DetailContractService;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DetailContractFileIntegrationTest {

    @Autowired
    private DetailContractService detailContractService;

    @Autowired
    private AttachmentFileRepository attachmentFileRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorrespondentRepository correspondentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("세부계약서 생성 시 첨부파일이 실제로 저장되어야 한다")
    void createDetailContractWithFileTest() throws Exception {
        // given
        Contract contract = createAndSaveTestContract();

        // 테스트용 MultipartFile 생성
        MockMultipartFile testFile = new MockMultipartFile(
                "files",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        // CreateDetailContractRequest 생성
        CreateDetailContractRequest request = CreateDetailContractRequest.builder()
                .contractId(contract.getContractId())
                .contractType("일반")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("테스트 내용")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .lastModifyUser("admin")
                .build();
        request.setFiles(List.of(testFile));

        // when
        var response = detailContractService.createDetailContract(request);

        // then
        // 1. DB에 파일 정보가 저장되었는지 확인
        List<AttachmentFile> savedFiles = attachmentFileRepository.findByReferenceId(response.getContractId());
        assertThat(savedFiles).isNotEmpty();
        AttachmentFile savedFile = savedFiles.get(0);
        assertThat(savedFile.getFileContentType()).isEqualTo("text/plain");
        assertThat(savedFile.getApinumber()).isEqualTo(APINumber.OUTSOURCING);

        // 2. 실제 파일이 디스크에 저장되었는지 확인
        File physicalFile = new File(savedFile.getFileSource());
        assertThat(physicalFile.exists()).isTrue();

        // 3. 파일 내용이 올바른지 확인
        String content = Files.readString(Path.of(savedFile.getFileSource()));
        assertThat(content).isEqualTo("Hello, World!");
    }

    private Contract createAndSaveTestContract() {
        User savedUser = userRepository.save(createUser());
        Correspondent savedCorrespondent = correspondentRepository.save(createCorrespondent());
        Category category = categoryRepository.save(new Category(null, "테스트 카테고리"));

        Contract contract = Contract.builder()
                .category(category)
                .name("테스트 계약")
                .contractStartDate(LocalDate.now())
                .status(Status.ONGOING)
                .processStatus(ProcessStatus.CONTRACTED)
                .method(Method.GENERAL)
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
}
