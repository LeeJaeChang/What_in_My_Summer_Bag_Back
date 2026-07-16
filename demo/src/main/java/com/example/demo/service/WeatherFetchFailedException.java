package com.example.demo.service;

public class WeatherFetchFailedException extends RuntimeException {

    public WeatherFetchFailedException(String message) {
        super(message);
    }

    public WeatherFetchFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
