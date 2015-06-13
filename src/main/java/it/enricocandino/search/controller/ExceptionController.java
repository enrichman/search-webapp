package it.enricocandino.search.controller;

import it.enricocandino.search.exception.UserNotFoundException;
import it.enricocandino.search.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
/**
 * @author Enrico Candino
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFound(UserNotFoundException ex) {

        ErrorResponse error = new ErrorResponse();
        error.setMessage("User not found");

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
