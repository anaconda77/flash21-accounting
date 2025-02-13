package com.flash21.accounting.file.dto.respone;

import com.flash21.accounting.common.exception.aop.FileOperation;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record AttachmentFilesResponse(
    List<AttachmentFileResponse> files
) {
    public record AttachmentFileResponse(
        String fileName,
        String contentType,
        Long fileSize,
        byte[] content
    ) {

        @FileOperation
        public static AttachmentFileResponse of(MultipartFile file) {
            try {
                return new AttachmentFileResponse(
                    file.getName(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
