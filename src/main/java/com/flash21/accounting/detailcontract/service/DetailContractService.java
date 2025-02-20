package com.flash21.accounting.detailcontract.service;

import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.dto.request.DetailContractRequest;
import com.flash21.accounting.detailcontract.dto.request.DetailContractUpdateRequest;
import com.flash21.accounting.detailcontract.dto.response.DetailContractResponse;
import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DetailContractService {
    @Transactional
    DetailContractResponse createDetailContract(DetailContractRequest request);
    @Transactional(readOnly = true)
    DetailContractResponse getDetailContract(Long detailContractId);
    @Transactional(readOnly = true)
    List<DetailContractResponse> getDetailContractsByContractId(Long contractId);
    @Transactional
    DetailContractResponse updateDetailContract(Long detailContractId, DetailContractUpdateRequest request);
    @Transactional
    void deleteDetailContract(Long detailContractId);
}
