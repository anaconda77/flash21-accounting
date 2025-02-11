package com.flash21.accounting.contract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.contract.dto.ContractRequestDto;
import com.flash21.accounting.contract.dto.ContractResponseDto;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import jakarta.transaction.Transactional;
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
        validateRequest(requestDto);

        Contract contract = contractRepository.save(
                Contract.builder()
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
                        .build()
        );

        return toResponseDto(contract);
    }

    @Override
    public ContractResponseDto getContractById(Integer contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));
        return toResponseDto(contract);
    }

    @Override
    @Transactional
    public ContractResponseDto updateContract(Integer contractId, ContractRequestDto requestDto) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));

        if (!isAuthorized(requestDto.getAdminId())) {
            throw new AccountingException(ContractErrorCode.FORBIDDEN);
        }

        updateFields(contract, requestDto);

        return toResponseDto(contract);
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
        return contractRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 필드 업데이트 로직을 별도 메서드로 분리 (Dirty Checking)
    private void updateFields(Contract contract, ContractRequestDto requestDto) {
        if (requestDto.getCategory() != null && !requestDto.getCategory().equals(contract.getCategory())) {
            contract.setCategory(requestDto.getCategory());
        }
        if (requestDto.getStatus() != null && !requestDto.getStatus().equals(contract.getStatus())) {
            contract.setStatus(requestDto.getStatus());
        }
        if (requestDto.getName() != null) contract.setName(requestDto.getName());
        if (requestDto.getContractStartDate() != null) contract.setContractStartDate(requestDto.getContractStartDate());
        if (requestDto.getContractEndDate() != null) contract.setContractEndDate(requestDto.getContractEndDate());
        if (requestDto.getWorkEndDate() != null) contract.setWorkEndDate(requestDto.getWorkEndDate());
        if (requestDto.getCategoryId() != null) contract.setCategoryId(requestDto.getCategoryId());
        if (requestDto.getCorrespondentId() != null) contract.setCorrespondentId(requestDto.getCorrespondentId());
    }

    // DTO 변환 로직을 따로 빼서 중복 제거
    private ContractResponseDto toResponseDto(Contract contract) {
        return new ContractResponseDto(
                contract.getContractId(),
                contract.getCategory(),
                contract.getStatus(),
                contract.getName(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getWorkEndDate()
        );
    }

    // 입력값 검증 (필수 필드 확인)
    private void validateRequest(ContractRequestDto requestDto) {
        if (requestDto.getCategoryId() == null || requestDto.getContractStartDate() == null) {
            throw new AccountingException(ContractErrorCode.MISSING_REQUIRED_FIELD);
        }
    }

    private boolean isAuthorized(Integer adminId) {
        // 권한 체크 로직이 있다면 여기에 추가
        return true;
    }

    private boolean isSuperAdmin() {
        // 관리자 체크 로직이 있다면 여기에 추가
        return true;
    }
}
