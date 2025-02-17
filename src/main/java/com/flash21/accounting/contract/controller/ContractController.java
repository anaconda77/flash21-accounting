package com.flash21.accounting.contract.controller;

import com.flash21.accounting.contract.dto.request.ContractRequestDto;
import com.flash21.accounting.contract.dto.response.ContractResponseDto;
import com.flash21.accounting.contract.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    // @Operation --> Swagger 위해
    @Operation(summary = "계약서 생성", description = "새로운 계약서를 등록합니다.")
    @PostMapping
    public ResponseEntity<ContractResponseDto> createContract(
            @Valid @RequestBody ContractRequestDto requestDto) {
        return ResponseEntity.ok(contractService.createContract(requestDto));
    }

    @Operation(summary = "모든 계약서 조회", description = "등록된 모든 계약서를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ContractResponseDto>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    @Operation(summary = "특정 계약서 조회", description = "ID를 기반으로 특정 계약서를 조회합니다.")
    @GetMapping("/{contractId}")
    public ResponseEntity<ContractResponseDto> getContractById(
            @Parameter(description = "조회할 계약서의 ID") @PathVariable Long contractId) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }

    @Operation(summary = "계약서 수정", description = "ID를 기반으로 계약서를 수정합니다.")
    @PutMapping("/{contractId}")
    public ResponseEntity<ContractResponseDto> updateContract(
            @Parameter(description = "수정할 계약서의 ID") @PathVariable Long contractId,
            @Valid @RequestBody ContractRequestDto requestDto) {
        return ResponseEntity.ok(contractService.updateContract(contractId, requestDto));
    }

    @Operation(summary = "계약서 삭제", description = "ID를 기반으로 계약서를 삭제합니다.")
    @DeleteMapping("/{contractId}")
    public ResponseEntity<Void> deleteContract(
            @Parameter(description = "삭제할 계약서의 ID") @PathVariable Long contractId) {
        contractService.deleteContract(contractId);
        return ResponseEntity.noContent().build();
    }
}
