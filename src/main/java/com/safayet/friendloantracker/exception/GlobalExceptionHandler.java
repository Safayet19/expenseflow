package com.safayet.friendloantracker.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        addError(model, "Record not found", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(InvalidOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidOperation(InvalidOperationException exception, Model model) {
        addError(model, "Invalid request", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseError(DataAccessException exception, Model model) {
        addError(model, "Database error", "The request could not be completed. Please try again.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpectedError(Exception exception, Model model) {
        addError(model, "Something went wrong", "The request could not be completed safely.");
        return "error";
    }

    private void addError(Model model, String title, String message) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
    }
}
