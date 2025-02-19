package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetailContractServiceImpl implements DetailContractService {
    private final ContractRepository contractRepository;
    private final DetailContractRepository detailContractRepository;

    @Override
    @Transactional
    public DetailContractResponse createDetailContract(DetailContractRequest request) {
        // 카테고리 유효성 검증
        if (request.getDetailContractCategory() == null) {
            throw new AccountingException(DetailContractErrorCode.INVALID_CATEGORY);
        }

        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.CONTRACT_NOT_FOUND));

        DetailContract detailContract = DetailContract.builder()
                .contract(contract)
                .detailContractCategory(request.getDetailContractCategory()) // 한글명 -> enum 변환
                .status(DetailContractStatus.TEMPORARY)  // 초기 상태는 TEMPORARY
                .content(request.getContent())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .supplyPrice(request.getSupplyPrice())
                .totalPrice(request.getTotalPrice())
                .build();

        Payment payment = Payment.builder()
                .detailContract(detailContract)
                .method(request.getPaymentMethod())
                .condition(request.getPaymentCondition())
                .build();

        detailContract.setPayment(payment);
        DetailContract savedDetailContract = detailContractRepository.save(detailContract);

        return DetailContractResponse.from(savedDetailContract);
    }

    // detailContractId로 조회(단건조회)
    @Override
    public DetailContractResponse getDetailContract(Long detailContractId) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        return DetailContractResponse.from(detailContract);
    }

    // 계약서 Id로 세부계약서 조회
    @Override
    public List<DetailContractResponse> getDetailContractsByContractId(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.CONTRACT_NOT_FOUND));

        List<DetailContract> detailContracts = detailContractRepository.findByContract(contract);

        return detailContracts.stream()
                .map(DetailContractResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DetailContractResponse updateDetailContract(Long detailContractId, DetailContractUpdateRequest request) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        detailContract.updateDetailContract(request);

        return DetailContractResponse.from(detailContract);
    }

    @Override
    @Transactional
    public void deleteDetailContract(Long detailContractId) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        detailContractRepository.delete(detailContract);
    }
}
