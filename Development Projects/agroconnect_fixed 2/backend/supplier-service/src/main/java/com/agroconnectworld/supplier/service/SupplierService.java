package com.agroconnectworld.supplier.service;

import com.agroconnectworld.supplier.entity.Certification;
import com.agroconnectworld.supplier.entity.Supplier;
import com.agroconnectworld.supplier.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> listAll() {
        return supplierRepository.findAll();
    }

    @Transactional
    public Supplier create(Supplier supplier) {
        attachSupplierToCertifications(supplier);
        return supplierRepository.save(supplier);
    }

    public Supplier findById(UUID id) {
        return supplierRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
    }

    @Transactional
    public Supplier update(UUID id, Supplier updated) {
        Supplier existing = findById(id);
        existing.setName(updated.getName());
        existing.setCountry(updated.getCountry());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setSupplyCapacity(updated.getSupplyCapacity());
        existing.setNotes(updated.getNotes());
        existing.getCertifications().clear();

        if (updated.getCertifications() != null) {
            updated.getCertifications().forEach(cert -> {
                Certification certification = new Certification();
                certification.setTitle(cert.getTitle());
                certification.setAuthority(cert.getAuthority());
                certification.setIssuedOn(cert.getIssuedOn());
                certification.setExpiresOn(cert.getExpiresOn());
                certification.setSupplier(existing);
                existing.getCertifications().add(certification);
            });
        }
        return supplierRepository.save(existing);
    }

    private void attachSupplierToCertifications(Supplier supplier) {
        if (supplier.getCertifications() != null) {
            supplier.getCertifications().forEach(cert -> cert.setSupplier(supplier));
        }
    }
}




