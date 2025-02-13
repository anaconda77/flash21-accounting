package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.category.domain.APINumber;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.common.exception.errorcode.CorrespondentErrorCode;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.repository.ContractRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.Outsourcing;
import com.flash21.accounting.detailcontract.domain.entity.Payment;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.detailcontract.domain.repository.OutsourcingRepository;
import com.flash21.accounting.detailcontract.domain.repository.PaymentRepository;
import com.flash21.accounting.detailcontract.dto.request.CreateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.UpdateDetailContractRequest;
import com.flash21.accounting.detailcontract.dto.response.CreateDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.GetDetailContractResponse;
import com.flash21.accounting.detailcontract.dto.response.UpdateDetailContractResponse;
import com.flash21.accounting.file.domain.AttachmentFile;
import com.flash21.accounting.file.repository.AttachmentFileRepository;
import com.flash21.accounting.file.service.AttachmentFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailContractService {
    private final DetailContractRepository detailContractRepository;
    private final OutsourcingRepository outsourcingRepository;
    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final AttachmentFileRepository attachmentFileRepository;
    private final AttachmentFileService attachmentFileService;

    // 세부계약서(외주/지출) 생성
    @Transactional
    public CreateDetailContractResponse createDetailContract(CreateDetailContractRequest request) {
        // 계약서 조회
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AccountingException(ContractErrorCode.CONTRACT_NOT_FOUND));
        // 세부계약서 생성
        DetailContract detailContract = DetailContract.builder()
                .contract(contract)  // contract 엔티티 설정
                .contractType(request.getContractType())
                .contractStatus(request.getContractStatus())
                .largeCategory(request.getLargeCategory())
                .smallCategory(request.getSmallCategory())
                .content(request.getContent())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .supplyPrice(request.getSupplyPrice())
                .totalPrice(request.getTotalPrice())
                .mainContractContent(request.getMainContractContent())
                .outsourcingContent(request.getOutsourcingContent())
                .lastModifyUser(request.getLastModifyUser())
                .history(request.getHistory())
                .build();

        DetailContract savedDetailContract = detailContractRepository.save(detailContract);

        // 외주 정보 저장
        if (request.getOutsourcings() != null) {
            request.getOutsourcings().forEach(outsourcingRequest -> {
                Outsourcing outsourcing = Outsourcing.builder()
                        .detailContract(savedDetailContract)
                        .outsourcingName(outsourcingRequest.getOutsourcingName())
                        .content(outsourcingRequest.getContent())
                        .quantity(outsourcingRequest.getQuantity())
                        .unitPrice(outsourcingRequest.getUnitPrice())
                        .supplyPrice(outsourcingRequest.getSupplyPrice())
                        .totalAmount(outsourcingRequest.getTotalAmount())
                        .build();
                outsourcingRepository.save(outsourcing);
            });
        }

        // 지출 정보 저장
        if (request.getPayments() != null) {
            request.getPayments().forEach(paymentRequest -> {
                Payment payment = Payment.builder()
                        .detailContract(savedDetailContract)
                        .method(paymentRequest.getMethod())
                        .condition(paymentRequest.getCondition())
                        .build();
                paymentRepository.save(payment);
            });
        }

        // 첨부파일 처리
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (MultipartFile file : request.getFiles()) {
                try {
                    attachmentFileService.saveFile(
                            savedDetailContract.getDetailContractId(), // referenceId
                            null, // typeId
                            file,
                            APINumber.OUTSOURCING // apiNumber = 4
                    );
                } catch (IOException e) {
                    throw new AccountingException(FileErrorCode.FILE_PROCESSING_ERROR);
                }
            }
        }

        return CreateDetailContractResponse.of(savedDetailContract.getDetailContractId());
    }

    // 세부계약서 ID로 조회
    public GetDetailContractResponse getDetailContract(Long detailContractId) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        // referenceId로 첨부파일 조회
        List<AttachmentFile> attachmentFiles = attachmentFileRepository.findByReferenceId(detailContractId);

        return GetDetailContractResponse.of(detailContract, attachmentFiles);
    }

    // 기존 세부계약서 수정
    @Transactional
    public UpdateDetailContractResponse updateDetailContract(Long detailContractId, UpdateDetailContractRequest request) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        // 기본 정보 업데이트
        detailContract.update(
                request.getContractType(),
                request.getContractStatus(),
                request.getLargeCategory(),
                request.getSmallCategory(),
                request.getContent(),
                request.getQuantity(),
                request.getUnitPrice(),
                request.getSupplyPrice(),
                request.getTotalPrice(),
                request.getMainContractContent(),
                request.getOutsourcingContent(),
                request.getLastModifyUser(),
                request.getHistory()
        );

        // 기존 외주 정보 삭제 후 새로 저장
        outsourcingRepository.deleteAll(detailContract.getOutsourcings());
        if (request.getOutsourcings() != null) {
            request.getOutsourcings().forEach(outsourcingRequest -> {
                Outsourcing outsourcing = Outsourcing.builder()
                        .detailContract(detailContract)
                        .outsourcingName(outsourcingRequest.getOutsourcingName())
                        .content(outsourcingRequest.getContent())
                        .quantity(outsourcingRequest.getQuantity())
                        .unitPrice(outsourcingRequest.getUnitPrice())
                        .supplyPrice(outsourcingRequest.getSupplyPrice())
                        .totalAmount(outsourcingRequest.getTotalAmount())
                        .build();
                outsourcingRepository.save(outsourcing);
            });
        }

        // 기존 지출 정보 삭제 후 새로 저장
        paymentRepository.deleteAll(detailContract.getPayments());
        if (request.getPayments() != null) {
            request.getPayments().forEach(paymentRequest -> {
                Payment payment = Payment.builder()
                        .detailContract(detailContract)
                        .method(paymentRequest.getMethod())
                        .condition(paymentRequest.getCondition())
                        .build();
                paymentRepository.save(payment);
            });
        }

        // 새로운 파일 추가
        if (request.getNewFiles() != null && !request.getNewFiles().isEmpty()) {
            for (MultipartFile file : request.getNewFiles()) {
                try {
                    attachmentFileService.saveFile(
                            detailContractId,
                            null,
                            file,
                            APINumber.OUTSOURCING
                    );
                } catch (IOException e) {
                    throw new AccountingException(FileErrorCode.FILE_PROCESSING_ERROR);
                }
            }
        }

        return UpdateDetailContractResponse.success();
    }

    // 상위계약서 ID로 세부계약서 조회
    public List<GetDetailContractResponse> getDetailContractByContractId(Long contractId) {
        List<DetailContract> detailContracts = detailContractRepository.findByContract_ContractId(contractId);
        if (detailContracts.isEmpty()) {
            throw new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND);
        }
        return detailContracts.stream()
                .map(detailContract -> {
                    List<AttachmentFile> attachmentFiles = attachmentFileRepository.findByReferenceId(detailContract.getDetailContractId());
                    return GetDetailContractResponse.of(detailContract, attachmentFiles);
                })
                .collect(Collectors.toList());
    }
}
