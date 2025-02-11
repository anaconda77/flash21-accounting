package com.flash21.accounting.correspondent.repository;

import com.flash21.accounting.correspondent.model.Correspondent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrespondentRepository extends JpaRepository<Correspondent, Long> {

    List<Correspondent> findByCorrespondentNameStartsWith(String correspondentName);
    List<Correspondent> findByBusinessRegNumberStartsWith(String businessRegNumber);
    List<Correspondent> findByOwnerNameStartsWith(String ownerName);
}
