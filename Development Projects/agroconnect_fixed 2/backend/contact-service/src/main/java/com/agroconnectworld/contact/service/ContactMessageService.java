package com.agroconnectworld.contact.service;

import com.agroconnectworld.contact.dto.CreateContactMessageRequest;
import com.agroconnectworld.contact.entity.ContactMessage;
import com.agroconnectworld.contact.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository repository;

    public ContactMessageService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    public List<ContactMessage> listMessages() {
        return repository.findAll();
    }

    public ContactMessage createMessage(CreateContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setPhone(request.getPhone());
        message.setMessage(request.getMessage());
        message.setCreatedAt(Instant.now());
        return repository.save(message);
    }
}




