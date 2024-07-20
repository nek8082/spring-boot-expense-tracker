package com.nek.mysaasapp.repository;

import java.util.Optional;

import com.nek.mysaasapp.entities.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);
}
