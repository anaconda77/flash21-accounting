package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.category.repository.CategoryRepository;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import com.flash21.accounting.file.service.AttachmentFileService;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.Role;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.domain.repository.OutsourcingRepository;
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.UpdateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.response.CreateDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.GetDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.UpdateDetailContractResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DetailContractServiceTest {

    @Mock
    private DetailContractRepository detailContractRepository;

    @Mock
    private OutsourcingRepository outsourcingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private DetailContractService detailContractService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AttachmentFileRepository attachmentFileRepository;

    @Mock
    private AttachmentFileService attachmentFileService;


    private CreateDetailContractRequest createRequest;
    private UpdateDetailContractRequest updateRequest;
    private DetailContract detailContract;
    private Contract contract;

    @BeforeEach
    void setUp() {
        contract = Contract.builder()
                .contractCategory(ContractCategory.ETC)
                .name("테스트 계약")
                .mainContractContent("테스트")
                .lastModifyUser(createUser())
                .registerDate(LocalDate.now())
                .contractStartDate(LocalDate.now())
                .processStatus(ProcessStatus.CONTRACTED) // 변경된 ENUM 사용
                .method(Method.GENERAL) // 변경된 ENUM 사용
                .admin(createUser())
                .correspondent(createCorrespondent())
                .build();

        createRequest = CreateDetailContractRequest.builder()
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

        updateRequest = UpdateDetailContractRequest.builder()
                .contractType("일반")
                .contractStatus("완료")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발 수정")
                .quantity(2)
                .unitPrice(1000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .lastModifyUser("admin")
                .build();

        detailContract = DetailContract.builder()
                .contract(contract)
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
    }

    private User createUser() {
        return User.builder()
                .username("admin")
                .password("password")
                .name("관리자")
                .phoneNumber("010-1234-5678")
                .email("admin@test.com")
                .address("서울시")
                .addressDetail("강남구")
                .role(Role.ROLE_ADMIN)
                .grade("A")
                .companyPhoneNumber("02-1234-5678")
                .companyFaxNumber("02-1234-5679")
                .build();
    }

    private Correspondent createCorrespondent() {
        return Correspondent.builder()
                .correspondentName("테스트 업체")
                .businessRegNumber("123-45-67890")
                .address("서울시")
                .build();
    }


    @Test
    @DisplayName("세부계약서 생성 성공")
    void createDetailContractSuccess() {
        // given
        Category category = new Category(1L, "테스트 카테고리");
        Contract savedContract = Contract.builder()
                .contractCategory(ContractCategory.ETC)
                .name("테스트 계약")
                .mainContractContent("테스트")
                .lastModifyUser(createUser())
                .registerDate(LocalDate.now())
                .contractStartDate(LocalDate.now())
                .processStatus(ProcessStatus.CONTRACTED) // 변경된 ENUM 사용
                .method(Method.GENERAL) // 변경된 ENUM 사용
                .admin(createUser())
                .correspondent(createCorrespondent())
                .build();

        // Contract 조회 Mock 설정
        given(contractRepository.findById(1L)).willReturn(Optional.of(savedContract));

        // 첨부파일 Mock 설정
        MultipartFile mockFile = mock(MultipartFile.class);
        createRequest.setFiles(List.of(mockFile));

        AttachmentFile mockAttachmentFile = new AttachmentFile(
                1L, // referenceId
                "test.pdf", // fileName
                "/path/to/file", // fileSource
                "application/pdf", // fileContentType
                APINumber.OUTSOURCING, // apiNumber
                null // typeId
        );

        given(attachmentFileService.saveFile(
                any(),
                isNull(),
                any(MultipartFile.class),
                eq(APINumber.OUTSOURCING)
        )).willReturn(mockAttachmentFile);

        DetailContract savedDetailContract = DetailContract.builder()
                .contract(savedContract)
                .contractType(createRequest.getContractType())
                .contractStatus(createRequest.getContractStatus())
                .largeCategory(createRequest.getLargeCategory())
                .smallCategory(createRequest.getSmallCategory())
                .content(createRequest.getContent())
                .quantity(createRequest.getQuantity())
                .unitPrice(createRequest.getUnitPrice())
                .supplyPrice(createRequest.getSupplyPrice())
                .totalPrice(createRequest.getTotalPrice())
                .lastModifyUser(createRequest.getLastModifyUser())
                .build();

        given(detailContractRepository.save(any(DetailContract.class))).willReturn(savedDetailContract);


        // when
        CreateDetailContractResponse response = detailContractService.createDetailContract(createRequest);

        // then
        assertThat(response).isNotNull();
        verify(contractRepository).findById(1L);
        verify(detailContractRepository).save(any(DetailContract.class));
    }


    @Test
    @DisplayName("세부계약서 조회 성공")
    void getDetailContractSuccess() {
        // given
        Long detailContractId = 1L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.of(detailContract));

        List<AttachmentFile> mockAttachmentFiles = List.of(
                new AttachmentFile(
                        detailContractId,  // referenceId
                        "test.pdf",        // fileName
                        "/path/to/file",   // fileSource
                        "application/pdf", // fileContentType
                        APINumber.OUTSOURCING, // apiNumber
                        null               // typeId
                )
        );

        given(attachmentFileRepository.findByReferenceId(detailContractId))
                .willReturn(mockAttachmentFiles);

        // when
        var response = detailContractService.getDetailContract(detailContractId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContractType()).isEqualTo("일반");
        assertThat(response.getContent()).isEqualTo("웹 개발");

        // 첨부파일 검증 추가
        assertThat(response.getAttachmentFiles()).hasSize(1);
        assertThat(response.getAttachmentFiles().get(0).getFileName()).isEqualTo("test.pdf");
    }

    @Test
    @DisplayName("존재하지 않는 세부계약서 조회 시 예외 발생")
    void getDetailContractNotFound() {
        // given
        Long detailContractId = 999L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> detailContractService.getDetailContract(detailContractId))
                .isInstanceOf(AccountingException.class);
    }

    @Test
    @DisplayName("상위계약서 ID로 세부계약서들 조회 성공")
    void getDetailContractsByParentContractIdSuccess() {
        Category category = new Category(1L, "테스트 카테고리");
        // given
        Long parentContractId = 1L;
        Contract parentContract =  Contract.builder()
                .contractCategory(ContractCategory.ETC)
                .name("테스트 계약")
                .mainContractContent("테스트")
                .lastModifyUser(createUser())
                .registerDate(LocalDate.now())
                .contractStartDate(LocalDate.now())
                .processStatus(ProcessStatus.CONTRACTED) // 변경된 ENUM 사용
                .method(Method.GENERAL) // 변경된 ENUM 사용
                .admin(createUser())
                .correspondent(createCorrespondent())
                .build();

        DetailContract detailContract1 = DetailContract.builder()
                .contract(parentContract)
                .contractType("일반")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발 A")
                .quantity(1)
                .unitPrice(1000000)
                .supplyPrice(1000000)
                .totalPrice(1100000)
                .lastModifyUser("admin")
                .build();

        DetailContract detailContract2 = DetailContract.builder()
                .contract(parentContract)
                .contractType("외주")
                .contractStatus("진행중")
                .largeCategory("IT")
                .smallCategory("개발")
                .content("웹 개발 B")
                .quantity(1)
                .unitPrice(2000000)
                .supplyPrice(2000000)
                .totalPrice(2200000)
                .lastModifyUser("admin")
                .build();

        // 불필요한 Contract 조회 Mock 제거
        given(detailContractRepository.findByContract_ContractId(parentContractId))
                .willReturn(List.of(detailContract1, detailContract2));

        // 첨부파일 Mock 설정 추가
        AttachmentFile mockFile1 = new AttachmentFile(
                1L,                    // referenceId
                "test1.pdf",          // fileName
                "/path/to/file1",     // fileSource
                "application/pdf",     // fileContentType
                APINumber.OUTSOURCING, // apiNumber
                null                  // typeId
        );

        AttachmentFile mockFile2 = new AttachmentFile(
                2L,                    // referenceId
                "test2.pdf",          // fileName
                "/path/to/file2",     // fileSource
                "application/pdf",     // fileContentType
                APINumber.OUTSOURCING, // apiNumber
                null                  // typeId
        );

        // 각 DetailContract의 ID에 대한 첨부파일 Mock 설정
        given(attachmentFileRepository.findByReferenceId(any()))
                .willReturn(List.of(mockFile1))
                .willReturn(List.of(mockFile2));

        // when
        List<GetDetailContractResponse> responses = detailContractService.getDetailContractByContractId(parentContractId);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("content")
                .containsExactlyInAnyOrder("웹 개발 A", "웹 개발 B");

        // 첨부파일 검증 추가
        assertThat(responses.get(0).getAttachmentFiles()).hasSize(1);
        assertThat(responses.get(1).getAttachmentFiles()).hasSize(1);
        assertThat(responses.get(0).getAttachmentFiles().get(0).getFileName()).isEqualTo("test1.pdf");
        assertThat(responses.get(1).getAttachmentFiles().get(0).getFileName()).isEqualTo("test2.pdf");

        verify(detailContractRepository).findByContract_ContractId(parentContractId);
        verify(detailContractRepository).findByContract_ContractId(parentContractId); verify(attachmentFileRepository, times(2)).findByReferenceId(any());

    }

    @Test
    @DisplayName("존재하지 않는 상위계약서 ID로 조회 시 예외 발생")
    void getDetailContractsByParentContractIdNotFound() {
        // given
        Long nonExistentParentId = 999L;
        given(detailContractRepository.findByContract_ContractId(nonExistentParentId))
                .willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() ->
                detailContractService.getDetailContractByContractId(nonExistentParentId))
                .isInstanceOf(AccountingException.class)
                .isInstanceOf(AccountingException.class)
                .hasFieldOrPropertyWithValue("errorCode", DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND);
    }

    @Test
    @DisplayName("세부계약서 수정 성공")
    void updateDetailContractSuccess() throws IOException {
        // given
        Long detailContractId = 1L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.of(detailContract));

        // 첨부파일 Mock 설정
        MultipartFile mockNewFile = mock(MultipartFile.class);
        updateRequest.setNewFiles(List.of(mockNewFile));

        AttachmentFile mockAttachmentFile = new AttachmentFile(
                detailContractId,
                "new_file.pdf",
                "/path/to/new/file",
                "application/pdf",
                APINumber.OUTSOURCING,
                null
        );

        given(attachmentFileService.saveFile(
                eq(detailContractId),
                isNull(),
                any(MultipartFile.class),
                eq(APINumber.OUTSOURCING)
        )).willReturn(mockAttachmentFile);

        // when
        UpdateDetailContractResponse response = detailContractService.updateDetailContract(detailContractId, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("수정이 완료되었습니다");
        verify(detailContractRepository).findById(detailContractId);
        verify(attachmentFileService).saveFile(
                eq(detailContractId),
                isNull(),
                any(MultipartFile.class),
                eq(APINumber.OUTSOURCING)
        );
    }

    @Test
    @DisplayName("존재하지 않는 세부계약서 수정 시 예외 발생")
    void updateDetailContractNotFound() {
        // given
        Long detailContractId = 999L;
        given(detailContractRepository.findById(detailContractId)).willReturn(Optional.empty());

        // 첨부파일 설정
        MultipartFile mockNewFile = mock(MultipartFile.class);
        updateRequest.setNewFiles(List.of(mockNewFile));

        // when & then
        assertThatThrownBy(() -> detailContractService.updateDetailContract(detailContractId, updateRequest))
                .isInstanceOf(AccountingException.class)
                .hasFieldOrPropertyWithValue("errorCode", DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND);

        // 파일 저장 시도하지 않았음을 검증
        verify(attachmentFileService, never()).saveFile(any(), any(), any(), any());
    }
}
