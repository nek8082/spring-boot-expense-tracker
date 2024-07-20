package com.nek.mysaasapp.repository;

import java.util.List;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.entities.TransactionRecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {

    List<TransactionRecord> findByUser(AppUser user);
}
