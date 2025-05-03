package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modal.Contact;
import com.example.demo.service.ContactService;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;


    @PostMapping("/api/submit-contact")
    public ResponseEntity<?> submitContact(@RequestBody Contact contact) {
        // Validate if required fields are missing
        if (contact.getName() == null || contact.getEmail() == null || contact.getContent() == null) {
            return ResponseEntity.badRequest().body("{\"message\": \"All fields are required.\"}");
        }

        // Save the contact
        contactService.saveContact(contact);

        // Return a JSON response
        return ResponseEntity.ok().body("{\"message\": \"Cảm ơn bạn ! Chúng tôi sẽ phản hồi cho bạn trong 24h\"}");
    }
}
