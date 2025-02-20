package com.flash21.accounting.outsourcing.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.CorrespondentErrorCode;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.common.exception.errorcode.OutsourcingErrorCode;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.repository.DetailContractRepository;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import com.flash21.accounting.outsourcing.domain.entity.OutsourcingStatus;
import com.flash21.accounting.outsourcing.domain.repository.OutsourcingRepository;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingRequest;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingUpdateRequest;
import com.flash21.accounting.outsourcing.dto.response.OutsourcingResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

@Setter
@RequiredArgsConstructor
public class OutsourcingServiceImpl implements OutsourcingService {
    private final DetailContractRepository detailContractRepository;
    private final OutsourcingRepository outsourcingRepository;
    private final CorrespondentRepository correspondentRepository;

    @Override
    public OutsourcingResponse createOutsourcing(Long detailContractId, OutsourcingRequest request) {
        DetailContract detailContract = detailContractRepository.findById(detailContractId)
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.DETAIL_CONTRACT_NOT_FOUND));

        if(detailContract.isHasOutsourcing()){
            throw new AccountingException(OutsourcingErrorCode.OUTSOURCING_ALREADY_EXISTS);
        }

        Correspondent correspondent = correspondentRepository.findById(request.getCorrespondentId())
                .orElseThrow(() -> new AccountingException(CorrespondentErrorCode.NOT_FOUND_CORRESPONDENT));

        Outsourcing outsourcing = Outsourcing.builder()
                .detailContract(detailContract)
                .correspondent(correspondent)
                .status(OutsourcingStatus.fromDisplayName(request.getStatus()))
                .content(request.getContent())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .supplyPrice(request.getSupplyPrice())
                .totalPrice(request.getTotalPrice())
                .build();

        detailContract.setHasOutsourcing(true);
        return OutsourcingResponse.from(outsourcingRepository.save(outsourcing));
    }

    @Override
    @Transactional(readOnly = true)
    public OutsourcingResponse getOutsourcingByDetailContractId(Long detailContractId) {
        Outsourcing outsourcing = outsourcingRepository.findByDetailContractDetailContractId(detailContractId)
                .orElseThrow(() -> new AccountingException(OutsourcingErrorCode.OUTSOURCING_NOT_FOUND));
        return OutsourcingResponse.from(outsourcing);
    }

    @Override
    @Transactional
    public OutsourcingResponse updateOutsourcing(Long outsourcingId, OutsourcingUpdateRequest request) {
        Outsourcing outsourcing = outsourcingRepository.findById(outsourcingId)
                .orElseThrow(() -> new AccountingException(OutsourcingErrorCode.OUTSOURCING_NOT_FOUND));

        outsourcing.updateOutsourcing(request);

        return OutsourcingResponse.from(outsourcing);
    }

    @Override
    @Transactional
    public void deleteOutsourcing(Long outsourcingtId) {
        Outsourcing outsourcing = outsourcingRepository.findById(outsourcingtId)
                .orElseThrow(() -> new AccountingException(OutsourcingErrorCode.OUTSOURCING_NOT_FOUND));

        DetailContract detailContract = outsourcing.getDetailContract();
        detailContract.setHasOutsourcing(false);

        outsourcingRepository.delete(outsourcing);
    }
}
