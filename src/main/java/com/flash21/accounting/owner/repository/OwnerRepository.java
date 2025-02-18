package com.flash21.accounting.owner.repository;

import com.flash21.accounting.owner.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

}

