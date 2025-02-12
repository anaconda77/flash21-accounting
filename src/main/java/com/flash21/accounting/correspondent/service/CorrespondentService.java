package com.flash21.accounting.correspondent.service;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.aop.ReflectionOperation;
import com.flash21.accounting.common.exception.errorcode.CorrespondentErrorCode;
import com.flash21.accounting.common.util.EntityToDtoMapper;
import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import java.lang.reflect.Method;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorrespondentService {

    private final CorrespondentRepository correspondentRepository;

    @Transactional
    public CorrespondentResponse createCorrespondent(CorrespondentRequest correspondentRequest,
        MultipartFile businessRegNumberImage) {
        Correspondent correspondent = correspondentRepository.save(
            convertToEntity(correspondentRequest));
        return convertToDto(correspondent);
    }

    @ReflectionOperation
    public List<CorrespondentResponse> getCorrespondents(String searchCondition,
        String searchValue) {
        // 검색 조건이 없는 경우 전체 거래처 데이터 리턴
        if (searchCondition == null || searchCondition.isEmpty()) {
            return correspondentRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
        }

        // 검색 조건이 있는데 빈 값을 보내면 안 됨
        if (searchValue == null || searchValue.isEmpty()) {
            throw AccountingException.of(CorrespondentErrorCode.NOT_ALLOWING_EMPTY_SEARCH_VALUES);
        }

        String capitalizedCondition = searchCondition.substring(0, 1).toUpperCase()
            + searchCondition.substring(1);

        Method method = ReflectionUtils.findMethod(CorrespondentRepository.class,
            "findBy" + capitalizedCondition + "StartsWith", String.class);

        Object result = ReflectionUtils.invokeMethod(method, correspondentRepository, searchValue);
        List<Correspondent> correspondents = (List<Correspondent>) result;

        return correspondents.stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional
    public void updateCorrespondent(Long correspondentId, CorrespondentRequest correspondentRequest,
        MultipartFile businessRegNumberImage) {
        Correspondent correspondent = correspondentRepository.findById(correspondentId)
            .orElseThrow(
                () -> AccountingException.of(CorrespondentErrorCode.NOT_FOUND_CORRESPONDENT));

        correspondent.updateCorrespondent(correspondentRequest);
    }

    @Transactional
    public void deleteCorrespondent(Long correspondentId) {
        Correspondent correspondent = correspondentRepository.findById(correspondentId)
            .orElseThrow(
                () -> AccountingException.of(CorrespondentErrorCode.NOT_FOUND_CORRESPONDENT));
        correspondentRepository.delete(correspondent);
    }

    public Correspondent convertToEntity(CorrespondentRequest correspondentRequest) {
        return EntityToDtoMapper.INSTANCE.dtoToCorrespondent(correspondentRequest);
    }

    public CorrespondentResponse convertToDto(Correspondent correspondent) {
        return EntityToDtoMapper.INSTANCE.correspondentToDto(correspondent);
    }
}
