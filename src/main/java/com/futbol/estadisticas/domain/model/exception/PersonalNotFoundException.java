package com.futbol.estadisticas.domain.model.exception;

public class PersonalNotFoundException extends RuntimeException {

    public PersonalNotFoundException(String message) {
        super(message);
    }

    public PersonalNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
