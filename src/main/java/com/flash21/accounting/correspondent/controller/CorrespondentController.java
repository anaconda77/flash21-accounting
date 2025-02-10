package com.flash21.accounting.correspondent.controller;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.correspondent.service.CorrespondentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/correspondent")
@RequiredArgsConstructor
public class CorrespondentController {

    private final CorrespondentService correspondentService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CorrespondentResponse> createCorrespondent(
        @RequestPart("json") @Valid CorrespondentRequest correspondentRequest,
        @RequestPart(name = "file", required = false) MultipartFile file) {
        return new ResponseEntity<>(correspondentService.createCorrespondent(correspondentRequest, file),
        HttpStatus.CREATED);
    }


}
