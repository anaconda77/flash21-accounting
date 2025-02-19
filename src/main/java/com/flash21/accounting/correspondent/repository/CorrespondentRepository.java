package com.flash21.accounting.correspondent.repository;

import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrespondentRepository extends JpaRepository<Correspondent, Long> {

    List<Correspondent> findByCorrespondentNameStartsWith(String correspondentName);

    List<Correspondent> findByCorrespondentCategory(CorrespondentCategory correspondentCategory);

    List<Correspondent> findByOwnerNameStartsWith(String ownerName);
}
