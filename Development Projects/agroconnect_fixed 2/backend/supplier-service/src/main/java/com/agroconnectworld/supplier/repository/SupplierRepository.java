package com.agroconnectworld.supplier.repository;

import com.agroconnectworld.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
}




