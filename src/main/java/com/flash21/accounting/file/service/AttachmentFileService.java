package com.flash21.accounting.file.service;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.dto.respone.AttachmentFilesResponse;
import com.flash21.accounting.file.dto.respone.AttachmentFilesResponse.AttachmentFileResponse;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttachmentFileService {

    private final AttachmentFileRepository attachmentFileRepository;
    private final SystemFileService systemFileService;

    @Transactional
    public AttachmentFile saveFile(Long referenceId, Integer typeId, MultipartFile file,
        APINumber apiNumber) {
        String originalFileName = file.getOriginalFilename();
        InputStream inputStream = systemFileService.getInputStream(file);
        // 파일을 분석하여 확장자 추출
        String contentType = systemFileService.detectFileType(inputStream);
        String systemFileName = systemFileService.generateUniqueFileName(contentType);
        Path targetPath = systemFileService.getPath(systemFileName);

        // 지정된 경로에 파일 복사(저장)
        systemFileService.createFileInSystemStorage(inputStream, targetPath);

        if (systemFileService.isPosixCompliant()) {
            systemFileService.setFilePermissions(targetPath);
        }

        AttachmentFile attachmentFile = new AttachmentFile(referenceId,
            originalFileName, targetPath.toString(), contentType, apiNumber, typeId);

        return attachmentFileRepository.save(attachmentFile);
    }

    public AttachmentFilesResponse getFiles(Long referenceId, APINumber apiNumber, Integer typeId) {
        if (apiNumber == null) {
            throw AccountingException.of(FileErrorCode.MISSING_ID);
        }

        // TypeId로 구분이 필요없는 API의 첨부파일인지?
        if (!APINumber.isNecessaryTypeId(apiNumber)) {
            return getFiles(referenceId, apiNumber);
        }

        List<AttachmentFile> candidates = attachmentFileRepository.findByReferenceId(referenceId);
        MultipartFile multipartFile = candidates.stream()
            .filter(attachmentFile -> checkApiIdAndTypeId(attachmentFile, apiNumber, typeId))
            .map(systemFileService::findFileInSystem)
            .findFirst()
            .orElseThrow(() -> AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR));

        return new AttachmentFilesResponse(List.of((AttachmentFileResponse.of(multipartFile))));
    }

    public AttachmentFilesResponse getFiles(Long referenceId, APINumber apiNumber) {
        List<AttachmentFile> candidates = attachmentFileRepository.findByReferenceId(referenceId);
        List<AttachmentFileResponse> files = candidates.stream()
            .filter(attachmentFile -> checkApiId(attachmentFile, apiNumber))
            .map(systemFileService::findFileInSystem)
            .map(AttachmentFileResponse::of)
            .toList();
        return new AttachmentFilesResponse(files);
    }


    private boolean checkApiId(AttachmentFile attachmentFile, APINumber apiNumber) {
        return attachmentFile.getApinumber().equals(apiNumber);
    }

    private boolean checkApiIdAndTypeId(AttachmentFile attachmentFile, APINumber apiNumber,
        Integer typeId) {
        if (typeId == null) {
            throw AccountingException.of(FileErrorCode.MISSING_ID);
        }
        return attachmentFile.getApinumber().equals(apiNumber) && attachmentFile.getTypeId()
            .equals(typeId);
    }


}
