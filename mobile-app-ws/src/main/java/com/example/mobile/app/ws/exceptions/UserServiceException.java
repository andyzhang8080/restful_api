package com.example.mobile.app.ws.exceptions;

public class UserServiceException extends RuntimeException {
    private  static final long serialVersionUID = 5313493413859894403L;

    public UserServiceException(String message) {
        super(message);
    }

}
