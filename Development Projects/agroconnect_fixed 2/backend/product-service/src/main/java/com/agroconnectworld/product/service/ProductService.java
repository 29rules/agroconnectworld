package com.agroconnectworld.product.service;

import com.agroconnectworld.product.entity.Product;
import com.agroconnectworld.product.repository.ProductImageRepository;
import com.agroconnectworld.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductImageRepository imageRepository;

    public ProductService(ProductRepository repository, ProductImageRepository imageRepository) {
        this.repository = repository;
        this.imageRepository = imageRepository;
    }

    public List<Product> listAll() {
        return repository.findAll();
    }

    public Product findById(java.util.UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product save(Product product) {
        Product saved = repository.save(product);
        if (product.getProductImages() != null) {
            product.getProductImages().forEach(image -> {
                image.setProduct(saved);
                imageRepository.save(image);
            });
        }
        return saved;
    }

    public Product update(java.util.UUID id, Product product) {
        Product existing = findById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setCategory(product.getCategory());
        existing.setSku(product.getSku());
        existing.setPrice(product.getPrice());
        existing.setActive(product.isActive());
        return repository.save(existing);
    }

    public void delete(java.util.UUID id) {
        repository.deleteById(id);
    }
}

