package com.flash21.accounting.detailcontract.controller;

import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.request.StatusUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.detailcontract.service.DetailContractService;
import com.flash21.accounting.detailcontract.service.DetailContractServiceImpl;
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

    @PostMapping
    public ResponseEntity<DetailContractResponse> createDetailContract(
            @Valid @RequestBody DetailContractRequest request) {
        DetailContractResponse response = detailContractService.createDetailContract(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단건 세부계약서
    @GetMapping("/{detailContractId}")
    public ResponseEntity<DetailContractResponse> getDetailContract(
            @PathVariable Long detailContractId) {
        DetailContractResponse response = detailContractService.getDetailContract(detailContractId);
        return ResponseEntity.ok(response);
    }

    // 계약서Id로 모든 세부계약서 조회
    @GetMapping("/contracts/{contractId}")
    public ResponseEntity<List<DetailContractResponse>> getDetailContractsByContractId(
            @PathVariable Long contractId) {
        List<DetailContractResponse> responses = detailContractService.getDetailContractsByContractId(contractId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{detailContractId}")
    public ResponseEntity<DetailContractResponse> updateDetailContract(
            @PathVariable Long detailContractId,
            @Valid @RequestBody  DetailContractUpdateRequest request) {
        DetailContractResponse response = detailContractService.updateDetailContract(detailContractId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{detailContractId}")
    public ResponseEntity<Void> deleteDetailContract(
            @PathVariable Long detailContractId) {
        detailContractService.deleteDetailContract(detailContractId);
        return ResponseEntity.noContent().build();
    }
}
