package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modal.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    // JPA provides all necessary methods for saving, finding, and deleting entities
}