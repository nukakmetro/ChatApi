package org.example.chatapi.exceptions;

public class UsernameIsExistException extends Exception {
    public UsernameIsExistException(String message) {
        super(message);
    }
}
