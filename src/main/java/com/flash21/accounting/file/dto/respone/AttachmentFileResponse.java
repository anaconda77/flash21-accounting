package com.flash21.accounting.file.dto.respone;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record AttachmentFileResponse(
    List<MultipartFile> files
) {
}
