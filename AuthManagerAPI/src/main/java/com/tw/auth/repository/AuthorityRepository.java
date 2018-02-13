package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tw.auth.domain.authority.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
