package com.flash21.accounting.contract.controller;

import com.flash21.accounting.contract.dto.ContractRequestDto;
import com.flash21.accounting.contract.dto.ContractResponseDto;
import com.flash21.accounting.contract.service.ContractServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractServiceImpl contractService;

    // ✅ 계약서 등록 (POST)
    @PostMapping
    public ResponseEntity<ContractResponseDto> createContract(@RequestBody ContractRequestDto requestDto) {
        ContractResponseDto responseDto = contractService.createContract(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // ✅ 특정 계약서 조회 (GET)
    @GetMapping("/{contractId}")
    public ResponseEntity<ContractResponseDto> getContractById(@PathVariable Integer contractId) {
        ContractResponseDto responseDto = contractService.getContractById(contractId);
        return ResponseEntity.ok(responseDto);
    }

    // ✅ 모든 계약서 조회 (GET)
    @GetMapping
    public ResponseEntity<List<ContractResponseDto>> getAllContracts() {
        List<ContractResponseDto> responseDtos = contractService.getAllContracts();
        return ResponseEntity.ok(responseDtos);
    }

    // ✅ 계약서 수정 (PUT)
    @PutMapping("/{contractId}")
    public ResponseEntity<ContractResponseDto> updateContract(
            @PathVariable Integer contractId,
            @Valid @RequestBody ContractRequestDto requestDto) {
        ContractResponseDto responseDto = contractService.updateContract(contractId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // ✅ 계약서 삭제 (DELETE)
    @DeleteMapping("/{contractId}")
    public ResponseEntity<Void> deleteContract(@PathVariable Integer contractId) {
        contractService.deleteContract(contractId);
        return ResponseEntity.noContent().build();
    }
}
