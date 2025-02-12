package com.flash21.accounting.contract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.contract.dto.ContractRequestDto;
import com.flash21.accounting.contract.dto.ContractResponseDto;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
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
    private final UserRepository userRepository;
    private final CorrespondentRepository correspondentRepository;

    @Override
    public ContractResponseDto createContract(ContractRequestDto requestDto) {
        validateRequest(requestDto);

        // adminId를 User 객체로 변환
        User admin = userRepository.findById(requestDto.getAdminId())
                .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));

        // correspondentId를 Correspondent 객체로 변환
        Correspondent correspondent = correspondentRepository.findById(Long.valueOf(requestDto.getCorrespondentId()))
                .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));

        Contract contract = contractRepository.save(
                Contract.builder()
                        .admin(admin)
                        .headSignId(requestDto.getHeadSignId())
                        .directorSignId(requestDto.getDirectorSignId())
                        .category(requestDto.getCategory())
                        .status(requestDto.getStatus())
                        .name(requestDto.getName())
                        .contractStartDate(requestDto.getContractStartDate())
                        .contractEndDate(requestDto.getContractEndDate())
                        .workEndDate(requestDto.getWorkEndDate())
                        .categoryId(requestDto.getCategoryId())
                        .correspondent(correspondent)
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

        if (requestDto.getAdminId() != null) {
            User admin = userRepository.findById(requestDto.getAdminId())
                    .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));
            contract.setAdmin(admin);
        }

        if (requestDto.getCorrespondentId() != null) {
            Correspondent correspondent = correspondentRepository.findById(Long.valueOf(requestDto.getCorrespondentId()))
                    .orElseThrow(() -> new AccountingException(ContractErrorCode.NOT_FOUND));
            contract.setCorrespondent(correspondent);
        }

        updateFields(contract, requestDto);

        return toResponseDto(contract);
    }

    @Override
    public void deleteContract(Integer contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new AccountingException(ContractErrorCode.NOT_FOUND);
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
    }

    // ContractResponseDto로 변환할 때 Correspondent의 ID 포함
    private ContractResponseDto toResponseDto(Contract contract) {
        return new ContractResponseDto(
                contract.getContractId(),
                contract.getCategory(),
                contract.getStatus(),
                contract.getName(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getWorkEndDate(),
                contract.getCorrespondent() != null ? contract.getCorrespondent().getId() : null // Correspondent ID 포함
        );
    }

    private void validateRequest(ContractRequestDto requestDto) {
        if (requestDto.getCategoryId() == null || requestDto.getContractStartDate() == null) {
            throw new AccountingException(ContractErrorCode.MISSING_REQUIRED_FIELD);
        }
    }
}
