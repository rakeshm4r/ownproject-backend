package com.own.project.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserException(UserException ex) {
      log.info("In GlobalExceptionHandler handleUserException()");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DynamicHeadingTagsException.class)
    public ResponseEntity<String> handleDynamicHeadingTagsException(DynamicHeadingTagsException ex) {
      log.info("In GlobalExceptionHandler handleDynamicHeadingTagsException()");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<String> handleCartException(CartException ex) {
      log.info("In GlobalExceptionHandler handleCartException()");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }


    // Handling general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
      log.info("In GlobalExceptionHandler handleGeneralException()");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }

    
}
