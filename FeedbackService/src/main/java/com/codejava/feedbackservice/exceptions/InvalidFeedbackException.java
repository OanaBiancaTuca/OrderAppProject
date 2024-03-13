package com.codejava.feedbackservice.exceptions;

public class InvalidFeedbackException extends RuntimeException{
    public InvalidFeedbackException() {
        super("Feedback format is invalid!");
    }
}
