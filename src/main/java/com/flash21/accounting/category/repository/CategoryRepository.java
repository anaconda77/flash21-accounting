package com.flash21.accounting.category.repository;

import com.flash21.accounting.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
