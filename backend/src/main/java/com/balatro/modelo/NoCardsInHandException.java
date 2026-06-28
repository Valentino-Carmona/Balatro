package com.balatro.modelo;

public class NoCardsInHandException extends RuntimeException {
    public NoCardsInHandException(String message) {
        super(message);
    }
}
