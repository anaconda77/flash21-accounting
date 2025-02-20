package com.flash21.accounting.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.flash21.accounting.file.domain.APINumber;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.dto.respone.AttachmentFilesResponse;
import com.flash21.accounting.file.dto.respone.AttachmentFilesResponse.AttachmentFileResponse;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import com.flash21.accounting.fixture.file.AttachmentFileFixture;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttachmentFileServiceTest {

    @Autowired
    AttachmentFileService attachmentFileService;
    @Autowired
    SystemFileService systemFileService;
    AttachmentFile attachmentFile;
    @Autowired
    AttachmentFileRepository attachmentFileRepository;

    @BeforeEach
    void setUp() {
        attachmentFile = attachmentFileRepository.save(AttachmentFileFixture.createDefault());
    }

    @DisplayName("로컬 스토리지에서 파일 가져오기 테스트, 파일명: demo.png")
    @Test
    void getAttachmentFile() throws IOException {
        AttachmentFilesResponse files = attachmentFileService.getFiles(attachmentFile.getReferenceId(),
            attachmentFile.getApinumber());
        AttachmentFileResponse multipartFile = files.files().getFirst();

        assertThat(multipartFile.fileName()).isEqualTo("demo");
        assertThat(multipartFile.contentType()).isEqualTo("image/png");
        assertThat(multipartFile.fileSize()).isEqualTo(812106L);
    }

    @DisplayName("로컬 스토리지에 파일 저장하기 테스트, 저장할 파일명: newFile.txt")
    @Test
    void createAttachmentFile() throws IOException {
        String originalFilename = "newFile";
        String content = "test content";
        MockMultipartFile file = new MockMultipartFile(
            originalFilename,
            originalFilename,
            MediaType.TEXT_PLAIN_VALUE,
            content.getBytes()
        );

        AttachmentFile createdAttachmentFile = attachmentFileService.saveFile(2L, null, file,
            APINumber.CONTRACT);;
        try {
            AttachmentFilesResponse files = attachmentFileService.getFiles(
                createdAttachmentFile.getReferenceId(), createdAttachmentFile.getApinumber());
            assertThat(files.files().size()).isEqualTo(1);

            AttachmentFileResponse multipartFile = files.files().getFirst();
            assertThat(multipartFile.fileName()).isEqualTo("newFile");
            assertThat(multipartFile.contentType()).isEqualTo("text/plain");
        } finally {
            boolean deleteFile = new File(createdAttachmentFile.getFileSource()).delete();
            assertThat(deleteFile).isTrue();
        }
    }

    @DisplayName("파일 삭제 테스트, 저장할 파일명: newFile.txt")
    @Test
    void deleteAttachmentFile() throws IOException {
        String originalFilename = "newFile";
        String content = "test content";
        MockMultipartFile file = new MockMultipartFile(
            originalFilename,
            originalFilename,
            MediaType.TEXT_PLAIN_VALUE,
            content.getBytes()
        );

        AttachmentFile createdAttachmentFile = attachmentFileService.saveFile(2L, null, file,
            APINumber.CONTRACT);;
        attachmentFileService.deleteFileNotHavingTypeId(createdAttachmentFile.getReferenceId(), createdAttachmentFile.getFileName(), APINumber.CONTRACT);
        // 파일을 삭제하였으므로 존재하면 안 됨
        assertFalse(isExistingFile(createdAttachmentFile.getFileSource()));
    }


    // 추가로 디렉토리 검사도 필요하다면
    public boolean isExistingFile(String path) {
        Path targetPath = Paths.get(path);
        return Files.exists(targetPath);
    }

}