package com.example.demo.service;

public class ChecklistNotFoundException extends RuntimeException {

    public ChecklistNotFoundException(String message) {
        super(message);
    }
}
