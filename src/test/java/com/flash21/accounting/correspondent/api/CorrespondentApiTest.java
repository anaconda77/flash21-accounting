package com.flash21.accounting.correspondent.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.common.interceptor.MultipartFileFieldNameValidationInterceptor;
import com.flash21.accounting.common.util.jwt.JWTUtil;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.file.domain.APINumber;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.dto.respone.AttachmentFilesResponse;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import com.flash21.accounting.file.service.AttachmentFileService;
import com.flash21.accounting.file.service.SystemFileService;
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.fixture.correspondent.CorrespondentFixture;
import com.flash21.accounting.fixture.user.UserFixture;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.repository.OwnerRepository;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=sgsdgsgsdgadsfewrewrewfsdvsdvgewrwerrwerwasdv"
})
@Transactional
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class CorrespondentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private CorrespondentRepository correspondentRepository;

    @Autowired
    private AttachmentFileRepository attachmentFileRepository;


    private Owner owner;
    private User user;
    private String accessToken;
    private Correspondent correspondent;


    MockMultipartFile businessRegNumberImage;
    AttachmentFile attachment1;
    MockMultipartFile bankBookImage;
    AttachmentFile attachment2;

    @BeforeEach
    void setUp() throws IOException {
        user = userRepository.save(UserFixture.createDefault());
        owner = ownerRepository.save(OwnerFixture.createDefault());
        correspondent = correspondentRepository.save(
            CorrespondentFixture.createWithAllSearchConditions(owner));
        accessToken = jwtUtil.createJwt(user.getUsername(), "admin", 10000L);
        businessRegNumberImage = new MockMultipartFile(
            "image1", //name
            "image1" + "." + "txt", //originalFilename
            "multipart/form-data",
            objectMapper.writeValueAsString("").getBytes(StandardCharsets.UTF_8)
        );
        bankBookImage = new MockMultipartFile(
            "image2", //name
            "image2" + "." + "txt", //originalFilename
            "multipart/form-data",
            objectMapper.writeValueAsString("").getBytes(StandardCharsets.UTF_8)
        );
        attachment1 = attachmentFileService.saveFile(correspondent.getId(),
            1, businessRegNumberImage, APINumber.CORRESPONDENT);
        attachment2 = attachmentFileService.saveFile(correspondent.getId(),
            2, bankBookImage, APINumber.CORRESPONDENT);
    }


    @DisplayName("첨부파일의 request part명이 잘못될 경우 예외를 던지는지 검증 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"businessRegNumberImag", "businessregNumberImage"})
    void invalidRequestPartName(String partName) throws Exception {

        String json = """
            {
            	"correspondentName" : "String22",
            	"ownerId": 1,
            	"managerName":"String",
            	"managerPosition":"String",
                "managerPhoneNumber":"String",
            	"managerEmail":"String",
            	"taxEmail":"String",
            	"businessRegNumber":"String",
            	"address":"String",
            	"detailedAddress":"String",
            	"memo":"String",
            	"categoryName" : "수영장"
            }
            """;
        MockMultipartFile jsonfile = new MockMultipartFile("json", "",
            "application/json", json.getBytes());
        MockMultipartFile newBusinessRegNumberImage = new MockMultipartFile(
            partName, //name
            "image3" + "." + "txt", //originalFilename
            "multipart/form-data",
            objectMapper.writeValueAsString("").getBytes(StandardCharsets.UTF_8)
        );


        mockMvc.perform(multipart(HttpMethod.PUT, "/api/correspondent/{id}", 1)
                .file(newBusinessRegNumberImage)
                .file(jsonfile)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .requestAttr("json", json)
            ).andExpect(status().isBadRequest())
            .andDo(print());
    }

    @DisplayName("새로운 첨부파일과 함께 거래처 수정 시 기존 것을 완전히 대체하는지 검증 테스트")
    @Test
    void updateCorrespondent() throws Exception {
        String json = """
            {
            	"correspondentName" : "String22",
            	"ownerId": 1,
            	"managerName":"String",
            	"managerPosition":"String",
                "managerPhoneNumber":"String",
            	"managerEmail":"String",
            	"taxEmail":"String",
            	"businessRegNumber":"String",
            	"address":"String",
            	"detailedAddress":"String",
            	"memo":"String",
            	"categoryName" : "수영장"
            }
            """;
        MockMultipartFile jsonfile = new MockMultipartFile("json", "",
            "application/json", json.getBytes());
        MockMultipartFile newBusinessRegNumberImage = new MockMultipartFile(
            "businessRegNumberImage", //name
            "image3" + "." + "txt", //originalFilename
            "multipart/form-data",
            objectMapper.writeValueAsString("").getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile newBankBookImage = new MockMultipartFile(
            "bankBookImage", //name
            "image4" + "." + "txt", //originalFilename
            "multipart/form-data",
            objectMapper.writeValueAsString("").getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/correspondent/{id}", 1)
                .file(newBusinessRegNumberImage)
                .file(newBankBookImage)
                .file(jsonfile)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .requestAttr("json", json)
            ).andExpect(status().isOk())
            .andDo(print());

        AttachmentFilesResponse businessRegResponses = attachmentFileService.getFiles(correspondent.getId(),
            APINumber.CORRESPONDENT, 1);
        AttachmentFilesResponse bankBookResponses = attachmentFileService.getFiles(correspondent.getId(),
            APINumber.CORRESPONDENT, 2);
        List<AttachmentFile> businessRegFiles = attachmentFileRepository.findByReferenceId(
                correspondent.getId())
            .stream()
            .filter(attachmentFile -> attachmentFile.getApinumber().equals(APINumber.CORRESPONDENT)
                && attachmentFile.getTypeId().equals(1))
            .toList();
        List<AttachmentFile> bankBookFiles = attachmentFileRepository.findByReferenceId(
                correspondent.getId())
            .stream()
            .filter(attachmentFile -> attachmentFile.getApinumber().equals(APINumber.CORRESPONDENT)
                && attachmentFile.getTypeId().equals(2))
            .toList();

        assertThat(businessRegFiles.size()).isEqualTo(1);
        assertThat(businessRegResponses.files().size()).isEqualTo(1);
        assertThat(businessRegResponses.files().getFirst().fileName()).isEqualTo("image3.txt");
        assertThat(businessRegResponses.files().getFirst().contentType()).isEqualTo("text/plain");

        assertThat(bankBookFiles.size()).isEqualTo(1);
        assertThat(bankBookResponses.files().size()).isEqualTo(1);
        assertThat(bankBookResponses.files().getFirst().fileName()).isEqualTo("image4.txt");
        assertThat(bankBookResponses.files().getFirst().contentType()).isEqualTo("text/plain");
    }




    @AfterEach
    void tearDown() {
        attachmentFileService.deleteAllFiles(correspondent.getId(), APINumber.CORRESPONDENT);
    }

}
