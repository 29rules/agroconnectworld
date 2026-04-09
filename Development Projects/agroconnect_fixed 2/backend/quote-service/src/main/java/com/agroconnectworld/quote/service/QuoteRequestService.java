package com.agroconnectworld.quote.service;

import com.agroconnectworld.quote.dto.CreateQuoteRequest;
import com.agroconnectworld.quote.dto.UpdateQuoteStatusRequest;
import com.agroconnectworld.quote.entity.QuoteRequest;
import com.agroconnectworld.quote.entity.QuoteStatus;
import com.agroconnectworld.quote.repository.QuoteRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class QuoteRequestService {

    private final QuoteRequestRepository repository;

    public QuoteRequestService(QuoteRequestRepository repository) {
        this.repository = repository;
    }

    public List<QuoteRequest> listAll() {
        return repository.findAll();
    }

    public QuoteRequest findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Quote request not found"));
    }

    @Transactional
    public QuoteRequest create(CreateQuoteRequest request) {
        QuoteRequest entity = new QuoteRequest();
        entity.setUserId(request.getUserId());
        entity.setProductId(request.getProductId());
        entity.setQuantity(request.getQuantity());
        entity.setPackagingSize(request.getPackagingSize());
        entity.setPort(request.getPort());
        entity.setNotes(request.getNotes());
        entity.setStatus(QuoteStatus.PENDING);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return repository.save(entity);
    }

    @Transactional
    public QuoteRequest updateStatus(UUID id, UpdateQuoteStatusRequest payload) {
        QuoteRequest existing = findById(id);
        existing.setStatus(payload.getStatus());
        existing.setUpdatedAt(Instant.now());
        return repository.save(existing);
    }
}




