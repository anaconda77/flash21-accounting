package com.flash21.accounting.sign.repository;

import com.flash21.accounting.sign.entity.Sign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignRepository extends JpaRepository<Sign, Integer> {
}
