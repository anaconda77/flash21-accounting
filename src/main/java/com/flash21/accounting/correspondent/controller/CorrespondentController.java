package com.flash21.accounting.correspondent.controller;

import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.correspondent.service.CorrespondentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/correspondent")
@RequiredArgsConstructor
public class CorrespondentController implements CorrespondentSpecification {

    private final CorrespondentService correspondentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CorrespondentResponse> createCorrespondent(
        @RequestPart("json") @Valid CorrespondentRequest correspondentRequest,
        @RequestPart(name = "businessRegNumberImage", required = false) MultipartFile businessRegNumberImage,
        @RequestPart(name = "bankBookImage", required = false) MultipartFile bankBookImage) {
        return new ResponseEntity<>(correspondentService.createCorrespondent(correspondentRequest, businessRegNumberImage, bankBookImage),
        HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CorrespondentResponse>> getCorrespondents(
        @RequestParam(required = false, name = "condition") String condition,     // 검색 조건명 (correspondentName, businessRegNumber, managerName)
        @RequestParam(required = false, name = "value") String value
    ) {
        return ResponseEntity.ok(correspondentService.getCorrespondents(condition, value));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateCorrespondent(
        @PathVariable(name = "id") Long correspondentId,
        @RequestPart("json") @Valid CorrespondentRequest correspondentRequest,
        @RequestPart(name = "businessRegNumberImage", required = false) MultipartFile businessRegNumberImage,
        @RequestPart(name = "bankBookImage", required = false) MultipartFile bankBookImage) {
        correspondentService.updateCorrespondent(correspondentId, correspondentRequest, businessRegNumberImage, bankBookImage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCorrespondent(@PathVariable(name = "id") Long correspondentId) {
        correspondentService.deleteCorrespondent(correspondentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
