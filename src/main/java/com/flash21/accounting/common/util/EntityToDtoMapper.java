package com.flash21.accounting.common.util;

import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.category.dto.request.CategoryRequest;
import com.flash21.accounting.category.dto.response.CategoryResponse;
import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.correspondent.domain.Correspondent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityToDtoMapper {
    EntityToDtoMapper INSTANCE = Mappers.getMapper(EntityToDtoMapper.class);

    Correspondent dtoToCorrespondent(CorrespondentRequest correspondentRequest);
    Category dtoToCategory(CategoryRequest categoryRequest);

    @Mapping(target = "correspondentId", source="correspondent.id")
    CorrespondentResponse correspondentToDto(Correspondent correspondent);

    CategoryResponse categoryToDto(Category category);
}
