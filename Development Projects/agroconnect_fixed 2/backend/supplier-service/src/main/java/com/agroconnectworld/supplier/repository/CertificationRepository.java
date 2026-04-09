package com.agroconnectworld.supplier.repository;

import com.agroconnectworld.supplier.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CertificationRepository extends JpaRepository<Certification, UUID> {
}




