package com.flash21.accounting.outsourcing.controller;

import com.flash21.accounting.outsourcing.dto.request.OutsourcingRequest;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingUpdateRequest;
import com.flash21.accounting.outsourcing.dto.response.OutsourcingResponse;
import com.flash21.accounting.outsourcing.service.OutsourcingService;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outsourcing")
public class OutsourcingController {
    private final OutsourcingService outsourcingService;

    @PostMapping("/detail-contracts/{detailContractId}")
    public ResponseEntity<OutsourcingResponse> createOutsourcing(
            @PathVariable Long detailContractId,
            @Valid @RequestBody OutsourcingRequest request) {
        OutsourcingResponse response = outsourcingService.createOutsourcing(detailContractId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{outsourcingId}")
    public ResponseEntity<OutsourcingResponse> updateOutsourcing(
            @PathVariable Long outsourcingId,
            @Valid @RequestBody OutsourcingUpdateRequest request) {
        OutsourcingResponse response = outsourcingService.updateOutsourcing(outsourcingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{outsourcingId}")
    public ResponseEntity<Void> deleteOutsourcing(@PathVariable Long outsourcingId) {
        outsourcingService.deleteOutsourcing(outsourcingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detail-contracts/{detailContractId}")
    public ResponseEntity<OutsourcingResponse> getOutsourcingByDetailContractId(
            @PathVariable Long detailContractId) {
        OutsourcingResponse response = outsourcingService.getOutsourcingByDetailContractId(detailContractId);
        return ResponseEntity.ok(response);
    }
}
