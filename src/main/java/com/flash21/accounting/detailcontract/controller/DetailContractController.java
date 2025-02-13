package com.flash21.accounting.detailcontract.controller;

import com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.UpdateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.response.CreateDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.GetDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.UpdateDetailContractResponse;
import com.flash21.accounting.detailcontract.service.DetailContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detail-contract")
public class DetailContractController {
    private final DetailContractService detailContractService;

    // 새로운 세부계약서 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateDetailContractResponse> createDetailContract(
            @Valid @RequestPart(value = "request") CreateDetailContractRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        request.setFiles(files);
        CreateDetailContractResponse response = detailContractService.createDetailContract(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 세부계약서 ID로 조회
    @GetMapping("/{detailContractId}")
    public ResponseEntity<GetDetailContractResponse> getDetailContract(
            @PathVariable Long detailContractId
    ) {
        GetDetailContractResponse response = detailContractService.getDetailContract(detailContractId);
        return ResponseEntity.ok(response);
    }

    // 기존 세부계약서 수정
    @PutMapping(value = "/{detailContractId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateDetailContractResponse> updateDetailContract(
            @PathVariable Long detailContractId,
            @Valid @RequestPart(value = "request") UpdateDetailContractRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        request.setNewFiles(files);
        UpdateDetailContractResponse response = detailContractService.updateDetailContract(detailContractId, request);
        return ResponseEntity.ok(response);
    }

    // 상위 계약서 id로 세부계약서 조회
    @GetMapping("/by-contract/{contractId}")
    public ResponseEntity<List<GetDetailContractResponse>> getDetailContractByContractId(
            @PathVariable Long contractId
    ) {
        List<GetDetailContractResponse> responses = detailContractService.getDetailContractByContractId(contractId);
        return ResponseEntity.ok(responses);
    }

}
