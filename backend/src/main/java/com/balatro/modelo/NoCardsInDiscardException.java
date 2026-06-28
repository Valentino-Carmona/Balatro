package com.balatro.modelo;

public class NoCardsInDiscardException extends RuntimeException {
    public NoCardsInDiscardException(String message) {
        super(message);
    }
}
