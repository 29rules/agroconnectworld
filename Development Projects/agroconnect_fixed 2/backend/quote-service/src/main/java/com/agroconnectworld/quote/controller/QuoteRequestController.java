package com.agroconnectworld.quote.controller;

import com.agroconnectworld.quote.dto.CreateQuoteRequest;
import com.agroconnectworld.quote.dto.UpdateQuoteStatusRequest;
import com.agroconnectworld.quote.entity.QuoteRequest;
import com.agroconnectworld.quote.service.QuoteRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")
public class QuoteRequestController {

    private final QuoteRequestService service;

    public QuoteRequestController(QuoteRequestService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<QuoteRequest>> listQuoteRequests() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping
    public ResponseEntity<QuoteRequest> createQuoteRequest(@RequestBody @Valid CreateQuoteRequest payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteRequest> getQuoteRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<QuoteRequest> updateStatus(@PathVariable UUID id,
                                                     @RequestBody @Valid UpdateQuoteStatusRequest payload) {
        return ResponseEntity.ok(service.updateStatus(id, payload));
    }
}




