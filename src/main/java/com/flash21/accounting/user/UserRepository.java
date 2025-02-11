package com.flash21.accounting.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    User findByUsername(String username);

    List<User> findByName(String name);

    List<User> findByNameAndEmail(String name, String email);
}
