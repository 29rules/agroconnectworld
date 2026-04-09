package com.agroconnectworld.supplier.controller;

import com.agroconnectworld.supplier.entity.Supplier;
import com.agroconnectworld.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> listSuppliers() {
        return ResponseEntity.ok(supplierService.listAll());
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody @Valid Supplier supplier) {
        return ResponseEntity.ok(supplierService.create(supplier));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplier(@PathVariable UUID id) {
        return ResponseEntity.ok(supplierService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable UUID id, @RequestBody @Valid Supplier supplier) {
        return ResponseEntity.ok(supplierService.update(id, supplier));
    }
}




