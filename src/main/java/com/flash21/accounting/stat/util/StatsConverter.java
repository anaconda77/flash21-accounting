package com.flash21.accounting.stat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.StatsErrorCode;
import com.flash21.accounting.stat.domain.YearStatsContent;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Converter(autoApply = true)
@RequiredArgsConstructor
public class StatsConverter implements AttributeConverter<List<YearStatsContent>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<YearStatsContent> statistics) {
        try {
            return objectMapper.writeValueAsString(statistics);
        } catch (JsonProcessingException e) {
            throw AccountingException.of(StatsErrorCode.FAILED_CONVERTING_TO_JSON, e);
        }
    }

    @Override
    public List<YearStatsContent> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) {
                return null;
            }
            return objectMapper.readValue(dbData, new TypeReference<List<YearStatsContent>>() {});
        } catch (JsonProcessingException e) {
            throw AccountingException.of(StatsErrorCode.FAILED_CONVERTING_TO_JSON, e);
        }
    }

}
