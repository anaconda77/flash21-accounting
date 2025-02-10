package com.flash21.accounting.common.util;

import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.correspondent.model.Correspondent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityToDtoMapper {
    EntityToDtoMapper INSTANCE = Mappers.getMapper(EntityToDtoMapper.class);

    Correspondent dtoToCorrespondent(CorrespondentRequest correspondentRequest);

    @Mapping(target = "correspondentId", source="correspondent.id")
    CorrespondentResponse correspondentToDto(Correspondent correspondent);
}
