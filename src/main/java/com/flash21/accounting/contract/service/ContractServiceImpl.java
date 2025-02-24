package com.flash21.accounting.contract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.*;
import com.flash21.accounting.contract.dto.request.ContractRequestDto;
import com.flash21.accounting.contract.dto.response.ContractResponseDto;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.sign.entity.Sign;
import com.flash21.accounting.sign.repository.SignRepository;
import com.flash21.accounting.user.User;
import com.flash21.accounting.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final CorrespondentRepository correspondentRepository;
    private final SignRepository signRepository;

    @Override
    public ContractResponseDto createContract(ContractRequestDto requestDto) {
        validateRequest(requestDto);

        // adminId를 User 객체로 변환
        User admin = userRepository.findById(requestDto.getAdminId())
                .orElseThrow(() -> new AccountingException(UserErrorCode.USER_NOT_FOUND));

        // correspondentId를 Correspondent 객체로 변환
        Correspondent correspondent = correspondentRepository.findById(Long.valueOf(requestDto.getCorrespondentId()))
                .orElseThrow(() -> new AccountingException(CorrespondentErrorCode.NOT_FOUND_CORRESPONDENT));

        // writerSignId를 Sign 객체로 변환
        Sign writerSign = requestDto.getWriterSignId() != null ?
                signRepository.findById(requestDto.getWriterSignId())
                        .orElseThrow(() -> new AccountingException(SignErrorCode.SIGN_NOT_FOUND))
                : null;

        // headSignId를 Sign 객체로 변환
        Sign headSign = requestDto.getHeadSignId() != null ?
                signRepository.findById(requestDto.getHeadSignId())
                        .orElseThrow(() -> new AccountingException(SignErrorCode.SIGN_NOT_FOUND))
                : null;

        // directorSignId를 Sign 객체로 변환
        Sign directorSign = requestDto.getDirectorSignId() != null ?
                signRepository.findById(requestDto.getDirectorSignId())
                        .orElseThrow(() -> new AccountingException(SignErrorCode.SIGN_NOT_FOUND))
                : null;

        // ProcessStatus 검증 후 변환
        ProcessStatus processStatus = parseProcessStatus(requestDto.getProcessStatus().toString());
        Method method = parseMethod(requestDto.getMethod().toString());
        ContractCategory contractCategory = parseContractCategory(requestDto.getContractCategory().toString());

        Contract contract = contractRepository.save(
                Contract.builder()
                        .admin(admin)
                        .lastModifyUser(admin) // 가장 마지막 수정인을 현 user로
                        .writerSign(writerSign)
                        .headSign(headSign)
                        .directorSign(directorSign)
                        .contractCategory(contractCategory)
                        .processStatus(processStatus)
                        .method(method)
                        .name(requestDto.getName())
                        .contractStartDate(requestDto.getContractStartDate())
                        .contractEndDate(requestDto.getContractEndDate())
                        .workEndDate(requestDto.getWorkEndDate())
                        .mainContractContent(requestDto.getMainContractContent())
                        .correspondent(correspondent)
                        .build()
        );

        return toResponseDto(contract);
    }

    @Override
    public ContractResponseDto getContractById(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AccountingException(ContractErrorCode.CONTRACT_NOT_FOUND));
        return toResponseDto(contract);
    }

    @Override
    @Transactional
    public List<ContractResponseDto> findContractWithin30Days(){
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        return contractRepository.findContractsEndingWithinDates(today, endDate)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContractResponseDto updateContract(Long contractId, ContractRequestDto requestDto) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AccountingException(ContractErrorCode.CONTRACT_NOT_FOUND));

        if (requestDto.getAdminId() != null) {
            User admin = userRepository.findById(requestDto.getAdminId())
                    .orElseThrow(() -> new AccountingException(ContractErrorCode.USER_UNAUTHORIZED));
            contract.setLastModifyUser(admin); // 마지막 수정한 사용자 업데이트
        }

        User modifier = userRepository.findById(requestDto.getAdminId())
                .orElseThrow(() -> new AccountingException(UserErrorCode.USER_NOT_FOUND));
        contract.setLastModifyUser(modifier);


        if (requestDto.getCorrespondentId() != null) {
            Correspondent correspondent = correspondentRepository.findById(Long.valueOf(requestDto.getCorrespondentId()))
                    .orElseThrow(() -> new AccountingException(ContractErrorCode.CONTRACT_NOT_FOUND));
            contract.setCorrespondent(correspondent);
        }

        updateFields(contract, requestDto);

        return toResponseDto(contract);
    }

    @Override
    public void deleteContract(Long contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new AccountingException(ContractErrorCode.CONTRACT_NOT_FOUND);
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
        if (requestDto.getProcessStatus() != null) contract.setProcessStatus(parseProcessStatus(requestDto.getProcessStatus().toString()));
        if (requestDto.getMethod() != null) contract.setMethod(parseMethod(requestDto.getMethod().toString()));
        if (requestDto.getName() != null) contract.setName(requestDto.getName());
        if (requestDto.getContractStartDate() != null) contract.setContractStartDate(requestDto.getContractStartDate());
        if (requestDto.getContractEndDate() != null) contract.setContractEndDate(requestDto.getContractEndDate());
        if (requestDto.getWorkEndDate() != null) contract.setWorkEndDate(requestDto.getWorkEndDate());
        if (requestDto.getContractCategory() != null) contract.setContractCategory(parseContractCategory(requestDto.getContractCategory().toString()));
        if (requestDto.getCorrespondentId() != null) {
            Correspondent correspondent = correspondentRepository.findById(Long.valueOf(requestDto.getCorrespondentId()))
                    .orElseThrow(() -> new AccountingException(CorrespondentErrorCode.NOT_FOUND_CORRESPONDENT));
            contract.setCorrespondent(correspondent);
        }
        if (requestDto.getMainContractContent() != null) contract.setMainContractContent(requestDto.getMainContractContent());


    }

    // ContractResponseDto로 변환할 때 Correspondent의 ID 포함
    private ContractResponseDto toResponseDto(Contract contract) {
        return new ContractResponseDto(
                contract.getContractId(),
                contract.getAdmin(),
                contract.getLastModifyUser(),
                contract.getContractCategory(),
                contract.getProcessStatus(),
                contract.getMethod(),
                contract.getName(),
                contract.getRegisterDate(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getWorkEndDate(),
                contract.getCorrespondent(),
                contract.getMainContractContent()
        );
    }

    private void validateRequest(ContractRequestDto requestDto) {
            if (requestDto.getAdminId() == null) {
                throw new AccountingException(ContractErrorCode.REQUIRED_FIELD_MISSING);
            }
            if (requestDto.getCorrespondentId() == null) {
                throw new AccountingException(ContractErrorCode.REQUIRED_FIELD_MISSING);
            }
            if (requestDto.getContractStartDate() == null) {
                throw new AccountingException(ContractErrorCode.REQUIRED_FIELD_MISSING);
            }
            if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
                throw new AccountingException(ContractErrorCode.INVALID_CONTRACT_NAME);
            }
    }

    private ProcessStatus parseProcessStatus(String processStatus) {
        try {
            return ProcessStatus.valueOf(processStatus.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AccountingException(ContractErrorCode.INVALID_PROCESS_STATUS);
        }
    }

    private Method parseMethod(String method) {
        try {
            return Method.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AccountingException(ContractErrorCode.INVALID_METHOD);
        }
    }

    private ContractCategory parseContractCategory(String contractCategory) {
        try {
            return ContractCategory.valueOf(contractCategory.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AccountingException(ContractErrorCode.INVALID_CONTRACT_CATEGORY);
        }
    }



}
