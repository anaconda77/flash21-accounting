package com.flash21.accounting.file.service;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.aop.FileOperation;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttachmentFileService {

    private final AttachmentFileRepository attachmentFileRepository;
    private final SystemFileService systemFileService;

    @Transactional
    @FileOperation
    public AttachmentFile saveFile(Long referenceId, Integer typeId, MultipartFile file,
        APINumber apiNumber) throws IOException {
        String originalFileName = file.getOriginalFilename();
        // 파일을 분석하여 확장자 추출
        String contentType = systemFileService.detectFileType(file.getInputStream());
        String systemFileName = systemFileService.generateUniqueFileName(contentType);
        Path targetPath = systemFileService.getPath(systemFileName);

        Files.copy(
            file.getInputStream(),
            targetPath,
            StandardCopyOption.REPLACE_EXISTING
        );

        if (systemFileService.isPosixCompliant()) {
            systemFileService.setFilePermissions(targetPath);
        }

        AttachmentFile attachmentFile = new AttachmentFile(referenceId,
            originalFileName, targetPath.toString(), contentType, apiNumber, typeId);

        return attachmentFileRepository.save(attachmentFile);
    }

    @FileOperation
    public List<MultipartFile> getFiles(Long referenceId, APINumber apiNumber) throws IOException {
        List<AttachmentFile> candidates = attachmentFileRepository.findByReferenceId(referenceId);
        return candidates.stream()
            .filter(attachmentFile -> attachmentFile.getApinumber().equals(apiNumber))
            .map(attachmentFile -> {
                try {
                    return systemFileService.findFileInSystem(attachmentFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .toList();
    }


}
