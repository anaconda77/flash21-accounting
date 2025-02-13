package com.flash21.accounting.file.controller;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.file.dto.respone.AttachmentFileResponse;
import com.flash21.accounting.file.service.AttachmentFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class AttachmentFileController {

    private final AttachmentFileService attachmentFileService;


    @GetMapping
    public ResponseEntity<AttachmentFileResponse> downloadFile(
        @RequestParam("referenceId") Long referenceId,
        @RequestParam("apiId") Integer apiId,
        @RequestParam(required = false, name = "typeId") Integer typeId
    ) {

        return new ResponseEntity<>(attachmentFileService.getFiles(referenceId, APINumber.getAPINumber(apiId), typeId), HttpStatus.OK);

    }
}
