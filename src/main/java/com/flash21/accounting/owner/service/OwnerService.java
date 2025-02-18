package com.flash21.accounting.owner.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.OwnerErrorCode;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.dto.request.OwnerRequest;
import com.flash21.accounting.owner.dto.response.OwnerResponse;
import com.flash21.accounting.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OwnerResponse create(OwnerRequest ownerRequest){
        Owner entity = ownerRepository.save(ownerRequest.toEntity());

        return OwnerResponse.fromEntity(entity);
    }

    public List<OwnerResponse> readAll(){
        List<Owner> owners = ownerRepository.findAll();

        // Owner -> OwenrResponse 변환
        return owners.stream()
                .map(OwnerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public OwnerResponse readById(Long id){
        Optional<Owner> owner = ownerRepository.findById(id);

        if(!owner.isPresent()){
            throw AccountingException.of(OwnerErrorCode.OWNER_NOT_FOUND);
        }

        return OwnerResponse.fromEntity(owner.get());
    }

    @Transactional
    public OwnerResponse update(Long id, OwnerRequest ownerRequest){
        Optional<Owner> owner = ownerRepository.findById(id);
        if(!owner.isPresent()){
            throw AccountingException.of(OwnerErrorCode.OWNER_NOT_FOUND);
        }

        owner.get().update(ownerRequest);

        return OwnerResponse.fromEntity(owner.get());
    }

    @Transactional
    public Long delete(Long id){
        Optional<Owner> owner = ownerRepository.findById(id);
        if(!owner.isPresent()){
            throw AccountingException.of(OwnerErrorCode.OWNER_NOT_FOUND);
        }

        ownerRepository.delete(owner.get());
        return id;
    }
}
