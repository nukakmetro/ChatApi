package org.example.chatapi.exceptions;

public class UserIsBlockedException extends Exception {
    public UserIsBlockedException(String message) {
        super(message);
    }
}
