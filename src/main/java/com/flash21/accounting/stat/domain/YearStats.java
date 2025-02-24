package com.flash21.accounting.stat.domain;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.stat.util.StatsConverter;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class YearStats {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer yearNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrespondentCategory category;
    @Column(nullable = false)
    private Long userId;
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @Convert(converter = StatsConverter.class)
    private List<YearStatsContent> content;


    public void updateContent(List<YearStatsContent> content) {
        this.content = content;
    }
}


