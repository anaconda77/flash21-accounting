package com.flash21.accounting.outsourcing.service;

import com.flash21.accounting.outsourcing.dto.request.OutsourcingRequest;
import com.flash21.accounting.outsourcing.dto.request.OutsourcingUpdateRequest;
import com.flash21.accounting.outsourcing.dto.response.OutsourcingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface OutsourcingService
{
    @Transactional
    OutsourcingResponse createOutsourcing(Long detailContractId, OutsourcingRequest request);
    @Transactional(readOnly = true)
    OutsourcingResponse getOutsourcingByDetailContractId(Long detailContractId);
    @Transactional
    OutsourcingResponse updateOutsourcing(Long outsourcingId, OutsourcingUpdateRequest request);
    @Transactional
    void deleteOutsourcing(Long outsourcingId);
}
