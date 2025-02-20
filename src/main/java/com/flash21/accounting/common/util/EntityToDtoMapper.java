package com.flash21.accounting.common.util;


import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.correspondent.domain.Correspondent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityToDtoMapper {
    EntityToDtoMapper INSTANCE = Mappers.getMapper(EntityToDtoMapper.class);

    @Mapping(target = "correspondentId", source="correspondent.id")
    @Mapping(target = "owner.ownerId", source="owner.ownerId")
    @Mapping(target = "owner.ownerName", source="owner.name")
    @Mapping(target = "categoryName", source="correspondentCategory.name")
    @Mapping(target = "type", source = "correspondentType.name")
    CorrespondentResponse correspondentToDto(Correspondent correspondent);

}
