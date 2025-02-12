package com.flash21.accounting.contract.service;

import com.flash21.accounting.contract.dto.ContractRequestDto;
import com.flash21.accounting.contract.dto.ContractResponseDto;

import java.util.List;

public interface ContractService {
    ContractResponseDto createContract(ContractRequestDto requestDto);
    ContractResponseDto getContractById(Long contractId);
    ContractResponseDto updateContract(Long contractId, ContractRequestDto requestDto);
    void deleteContract(Long contractId);
    List<ContractResponseDto> getAllContracts();
}
