package com.flash21.accounting.correspondent.repository;

import com.flash21.accounting.correspondent.model.Correspondent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrespondentRepository extends JpaRepository<Correspondent, Long> {

}
