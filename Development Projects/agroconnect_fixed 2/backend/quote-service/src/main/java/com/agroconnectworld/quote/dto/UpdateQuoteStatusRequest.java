package com.agroconnectworld.quote.dto;

import com.agroconnectworld.quote.entity.QuoteStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateQuoteStatusRequest {

    @NotNull
    private QuoteStatus status;

    public QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }
}




