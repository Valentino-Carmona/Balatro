package com.balatro.modelo;

public class NoDiscardsLeftException extends RuntimeException {
  public NoDiscardsLeftException(String message) {
    super(message);
  }
}
