package com.flash21.accounting.contract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.contract.dto.ContractRequestDto;
import com.flash21.accounting.contract.dto.ContractResponseDto;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public ContractResponseDto createContract(ContractRequestDto requestDto) {
        if (requestDto.getCategoryId() == null) {
            throw new AccountingException(ContractErrorCode.MISSING_REQUIRED_FIELD);
        }
        if (requestDto.getContractStartDate() == null) {
            throw new AccountingException(ContractErrorCode.MISSING_REQUIRED_FIELD);
        }

        Contract contract = Contract.builder()
                .adminId(requestDto.getAdminId())
                .headSignId(requestDto.getHeadSignId())
                .directorSignId(requestDto.getDirectorSignId())
                .category(requestDto.getCategory())
                .status(requestDto.getStatus())
                .name(requestDto.getName())
                .contractStartDate(requestDto.getContractStartDate())
                .contractEndDate(requestDto.getContractEndDate())
                .workEndDate(requestDto.getWorkEndDate())
                .categoryId(requestDto.getCategoryId())
                .correspondentId(requestDto.getCorrespondentId())
                .build();

        Contract savedContract = contractRepository.save(contract);
        return new ContractResponseDto(savedContract.getContractId(), savedContract.getCategory(),
                savedContract.getStatus(), savedContract.getName(),
                savedContract.getContractStartDate(), savedContract.getContractEndDate(),
                savedContract.getWorkEndDate());
    }

    @Override
    public ContractResponseDto getContractById(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));

        return new ContractResponseDto(contract.getContractId(), contract.getCategory(),
                contract.getStatus(), contract.getName(),
                contract.getContractStartDate(), contract.getContractEndDate(),
                contract.getWorkEndDate());
    }

    @Override
    public ContractResponseDto updateContract(Integer contractId, ContractRequestDto requestDto) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));

        if (!isAuthorized(requestDto.getAdminId())) {
            throw new AccountingException(ContractErrorCode.FORBIDDEN);
        }

        if (requestDto.getName() != null) contract.setName(requestDto.getName());
        if (requestDto.getContractStartDate() != null) contract.setContractStartDate(requestDto.getContractStartDate());
        if (requestDto.getContractEndDate() != null) contract.setContractEndDate(requestDto.getContractEndDate());
        if (requestDto.getWorkEndDate() != null) contract.setWorkEndDate(requestDto.getWorkEndDate());
        if (requestDto.getCategoryId() != null) contract.setCategoryId(requestDto.getCategoryId());
        if (requestDto.getCorrespondentId() != null) contract.setCorrespondentId(requestDto.getCorrespondentId());

        Contract updatedContract = contractRepository.save(contract);

        return new ContractResponseDto(
                updatedContract.getContractId(), updatedContract.getCategory(),
                updatedContract.getStatus(), updatedContract.getName(),
                updatedContract.getContractStartDate(), updatedContract.getContractEndDate(),
                updatedContract.getWorkEndDate());
    }

    @Override
    public void deleteContract(Integer contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new AccountingException(ContractErrorCode.NOT_FOUND);
        }

        if (!isSuperAdmin()) {
            throw new AccountingException(ContractErrorCode.SUPER_ADMIN_ONLY);
        }

        contractRepository.deleteById(contractId);
    }

    @Override
    public List<ContractResponseDto> getAllContracts() {
        return contractRepository.findAll().stream()
                .map(contract -> new ContractResponseDto(contract.getContractId(), contract.getCategory(),
                        contract.getStatus(), contract.getName(),
                        contract.getContractStartDate(), contract.getContractEndDate(),
                        contract.getWorkEndDate()))
                .collect(Collectors.toList());
    }

    private boolean isAuthorized(Integer adminId) {
        // TODO: 관리자 권한 체크 로직 구현 필요
        return true;
    }

    private boolean isSuperAdmin() {
        // TODO: 슈퍼 관리자 권한 체크 로직 구현 필요
        return true;
    }
}
