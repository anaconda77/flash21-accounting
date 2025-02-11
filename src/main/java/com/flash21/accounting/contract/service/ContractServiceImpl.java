package com.flash21.accounting.contract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.contract.dto.ContractRequestDto;
import com.flash21.accounting.contract.dto.ContractResponseDto;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;  // User 조회를 위한 Repository 추가

    @Override
    public ContractResponseDto createContract(ContractRequestDto requestDto) {
        validateRequest(requestDto);

        // adminId를 User 객체로 변환 (DB에서 조회)
        User admin = userRepository.findById(requestDto.getAdminId())
                .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));

        Contract contract = contractRepository.save(
                Contract.builder()
                        .admin(admin)  // User 객체로 저장
                        .headSignId(requestDto.getHeadSignId())  // Integer 유지
                        .directorSignId(requestDto.getDirectorSignId())  // Integer 유지
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

        // adminId 검증 후 User 변환
        if (requestDto.getAdminId() != null) {
            User admin = userRepository.findById(requestDto.getAdminId())
                    .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));
            contract.setAdmin(admin);
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

    private void updateFields(Contract contract, ContractRequestDto requestDto) {
        if (requestDto.getCategory() != null) contract.setCategory(requestDto.getCategory());
        if (requestDto.getStatus() != null) contract.setStatus(requestDto.getStatus());
        if (requestDto.getName() != null) contract.setName(requestDto.getName());
        if (requestDto.getContractStartDate() != null) contract.setContractStartDate(requestDto.getContractStartDate());
        if (requestDto.getContractEndDate() != null) contract.setContractEndDate(requestDto.getContractEndDate());
        if (requestDto.getWorkEndDate() != null) contract.setWorkEndDate(requestDto.getWorkEndDate());
        if (requestDto.getCategoryId() != null) contract.setCategoryId(requestDto.getCategoryId());
        if (requestDto.getCorrespondentId() != null) contract.setCorrespondentId(requestDto.getCorrespondentId());
    }

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

    private void validateRequest(ContractRequestDto requestDto) {
        if (requestDto.getCategoryId() == null || requestDto.getContractStartDate() == null) {
            throw new AccountingException(ContractErrorCode.MISSING_REQUIRED_FIELD);
        }
    }

    private boolean isSuperAdmin() {
        return true;
    }
}
