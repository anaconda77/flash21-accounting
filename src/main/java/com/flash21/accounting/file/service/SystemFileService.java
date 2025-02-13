package com.flash21.accounting.file.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import com.flash21.accounting.file.domain.AttachmentFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;
import org.apache.tika.Tika;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SystemFileService {

    private final String baseDirectory;

    public SystemFileService() {
        this.baseDirectory = getOsSpecificBaseDirectory();
    }

    public Path getPath(String fileName) {
        return Paths.get(baseDirectory, fileName);
    }

    public InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR, e);
        }
    }

    public void createFileInSystemStorage(InputStream inputStream, Path fileSource) {
        try {
            Files.copy(
                inputStream,
                fileSource,
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR, e);
        }
    }

    public MultipartFile findFileInSystem(AttachmentFile attachmentFile) {
        File file = new File(attachmentFile.getFileSource());
        try {
            FileInputStream input = new FileInputStream(file);
            return new MockMultipartFile(
                attachmentFile.getFileName(),
                attachmentFile.getFileName(),
                attachmentFile.getFileContentType(),
                input
            );
        } catch (IOException e) {
            throw AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR, e);
        }
    }

    // 날짜+랜덤 문자열+확장자 형식으로 로컬 스토리지에 저장함
    public String generateUniqueFileName(String contentType) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        String extension = getExtensionFromContentType(contentType);

        return String.format("%s_%s.%s", timestamp, randomString, extension);
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "application/pdf" -> "pdf";
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "application/msword" -> "doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                "docx";
            case "application/vnd.ms-excel" -> "xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx";
            case "text/plain" -> "txt";
            case "text/csv" -> "csv";
            case "application/zip" -> "zip";
            // 기타 필요한 타입들 추가
            default -> throw AccountingException.of(FileErrorCode.UNSUPPORTED_OS);
        };
    }

    public String detectFileType(InputStream inputStream) {
        // Apache Tika 등을 사용하여 실제 파일 내용 기반으로 타입 탐지
        try {
            Tika tika = new Tika();
            return tika.detect(inputStream);
        } catch (IOException e) {
            throw AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR, e);
        }

    }

    public String getOsSpecificBaseDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return System.getProperty("user.home") + "/accounting/files/";
        } else if (os.contains("linux")) {
            return "/var/accounting/files/";
        } else if (os.contains("windows")) {
            return System.getProperty("user.home") + "\\accounting\\files\\";
        } else {
            throw AccountingException.of(FileErrorCode.UNSUPPORTED_OS);
        }
    }

    public void createBaseDirectory() {
        Path directory = Paths.get(baseDirectory);
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);

                // POSIX 호환 시스템(Linux, macOS)에서만 권한 설정
                if (isPosixCompliant()) {
                    setFilePermissions(directory);
                }
            }
        } catch (IOException e) {
            throw AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR, e);
        }
    }

    public boolean isPosixCompliant() {
        return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
    }

    public void setFilePermissions(Path path) {
        try {
            if (isPosixCompliant()) {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.setPosixFilePermissions(path, permissions);
            }
        } catch (IOException e) {
            throw AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR, e);
        }
    }

    public void deleteFile(String fileSource) {
        Path filePath = Paths.get(fileSource);

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                throw AccountingException.of(FileErrorCode.NOT_FOUND_FILE);
            }

        } catch (DirectoryNotEmptyException e) {
            throw AccountingException.of(FileErrorCode.CANNOT_DELETE_DIRECTORY);
        } catch (SecurityException e) {
            throw AccountingException.of(FileErrorCode.ACCESS_REFUSED);
        } catch (IOException e) {
            throw AccountingException.of(FileErrorCode.FILE_PROCESSING_ERROR, e);
        }
    }
}
