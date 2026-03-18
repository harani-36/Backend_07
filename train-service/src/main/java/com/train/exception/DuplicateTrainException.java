package com.train.exception;

public class DuplicateTrainException extends RuntimeException {
    public DuplicateTrainException(String message) {
        super(message);
    }
}