package com.agroconnectworld.contact.controller;

import com.agroconnectworld.contact.dto.CreateContactMessageRequest;
import com.agroconnectworld.contact.entity.ContactMessage;
import com.agroconnectworld.contact.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    private final ContactMessageService service;

    public ContactMessageController(ContactMessageService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ContactMessage>> listMessages() {
        return ResponseEntity.ok(service.listMessages());
    }

    @PostMapping
    public ResponseEntity<ContactMessage> createMessage(@RequestBody @Valid CreateContactMessageRequest request) {
        return ResponseEntity.ok(service.createMessage(request));
    }
}




