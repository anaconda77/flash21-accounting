package com.flash21.accounting.correspondent.service;

import com.flash21.accounting.common.util.EntityToDtoMapper;
import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.correspondent.model.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorrespondentService {

    private final CorrespondentRepository correspondentRepository;

    public CorrespondentResponse createCorrespondent(CorrespondentRequest correspondentRequest, MultipartFile businessRegNumberImage) {
        Correspondent correspondent = correspondentRepository.save(convertToEntity(correspondentRequest));
        return convertToDto(correspondent);
    }


    public Correspondent convertToEntity(CorrespondentRequest correspondentRequest) {
        return EntityToDtoMapper.INSTANCE.dtoToCorrespondent(correspondentRequest);
    }

    public CorrespondentResponse convertToDto(Correspondent correspondent) {
        return EntityToDtoMapper.INSTANCE.correspondentToDto(correspondent);
    }
}
