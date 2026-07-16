package com.example.demo.service;

public class PackingItemNotFoundException extends RuntimeException {

    public PackingItemNotFoundException(String message) {
        super(message);
    }
}
