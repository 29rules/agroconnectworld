package com.agroconnectworld.quote.repository;

import com.agroconnectworld.quote.entity.QuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, UUID> {
}




