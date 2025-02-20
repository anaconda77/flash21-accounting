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
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import com.flash21.accounting.outsourcing.domain.repository.OutsourcingRepository;
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
    private final OutsourcingRepository outsourcingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public DetailContractResponse createDetailContract(DetailContractRequest request) {
        // 카테고리 유효성 검증
        DetailContractCategory category = DetailContractCategory.fromDisplayName(request.getDetailContractCategory());
        DetailContractStatus status = DetailContractStatus.fromDisplayName(request.getStatus());

        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.CONTRACT_NOT_FOUND));

        DetailContract detailContract = DetailContract.builder()
                .contract(contract)
                .detailContractCategory(category)
                .status(status)
                .content(request.getContent())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .supplyPrice(request.getSupplyPrice())
                .totalPrice(request.getTotalPrice())
                .build();

        DetailContract savedDetailContract = detailContractRepository.save(detailContract);

        Payment payment = Payment.builder()
                .detailContract(detailContract)
                .method(request.getPaymentMethod())
                .condition(request.getPaymentCondition())
                .build();

        paymentRepository.save(payment);

        return createDetailContractResponse(savedDetailContract);
    }

    private DetailContractResponse createDetailContractResponse(DetailContract detailContract) {
        Payment payment = paymentRepository.findByDetailContractId(detailContract.getDetailContractId())
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.PAYMENT_NOT_FOUND));

        Outsourcing outsourcing = null;
        if (detailContract.isHasOutsourcing()) {
            outsourcing = outsourcingRepository.findByDetailContractDetailContractId(
                    detailContract.getDetailContractId()).orElse(null);
        }

        return DetailContractResponse.from(detailContract, payment, outsourcing);
    }

    // detailContractId로 조회(단건조회)
    @Override
    public DetailContractResponse getDetailContract(Long detailContractId) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        Payment payment = paymentRepository.findByDetailContractId(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.PAYMENT_NOT_FOUND));

        Outsourcing outsourcing = null;
        if (detailContract.isHasOutsourcing()) {
            outsourcing = outsourcingRepository.findByDetailContractDetailContractId(detailContractId)
                    .orElse(null);
        }

        return DetailContractResponse.from(detailContract, payment, outsourcing);
    }

    // 계약서 Id로 세부계약서 조회
    @Override
    public List<DetailContractResponse> getDetailContractsByContractId(Long contractId) {
        List<DetailContract> detailContracts = detailContractRepository.findByContractContractId(contractId);

        return detailContracts.stream()
                .map(detailContract -> {
                    Payment payment = paymentRepository.findByDetailContractId(
                                    detailContract.getDetailContractId())
                            .orElseThrow(() -> new AccountingException(DetailContractErrorCode.PAYMENT_NOT_FOUND));

                    Outsourcing outsourcing = null;
                    if (detailContract.isHasOutsourcing()) {
                        outsourcing = outsourcingRepository.findByDetailContractDetailContractId(
                                detailContract.getDetailContractId()).orElse(null);
                    }

                    return DetailContractResponse.from(detailContract, payment, outsourcing);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DetailContractResponse updateDetailContract(Long detailContractId, DetailContractUpdateRequest request) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        detailContract.updateDetailContract(request);

        // Payment 업데이트
        if(request.getPaymentMethod() != null || request.getPaymentCondition() != null) {
            Payment payment = paymentRepository.findByDetailContractId(detailContractId)
                    .orElseThrow(() -> new AccountingException(DetailContractErrorCode.PAYMENT_NOT_FOUND));
            payment.update(request.getPaymentMethod(), request.getPaymentCondition());
        }

        Payment payment = paymentRepository.findByDetailContractId(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.PAYMENT_NOT_FOUND));

        // 수정 시에는 현재 연결된 outsourcing 정보 조회하여 전달
        Outsourcing outsourcing = null;
        if (detailContract.isHasOutsourcing()) {
            outsourcing = outsourcingRepository.findByDetailContractDetailContractId(detailContractId)
                    .orElse(null);
        }

        return DetailContractResponse.from(detailContract,payment, outsourcing);
    }

    @Override
    @Transactional
    public void deleteDetailContract(Long detailContractId) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        detailContractRepository.delete(detailContract);
    }
}
