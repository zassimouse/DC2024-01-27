package by.bsuir.taskrest.exception;

import by.bsuir.taskrest.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CreateEntityException.class)
    public ResponseEntity<ErrorResponse> handleCreateEntityException(CreateEntityException e) {
        return buildDefaultResponse(e, UNPROCESSABLE_ENTITY, 1);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildDefaultResponse(e, NOT_FOUND, 1);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return buildDefaultResponse(e, BAD_REQUEST, 2);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return buildDefaultResponse(e, FORBIDDEN, 3);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .message(errors.toString()
                                .replace("{", "")
                                .replace("}", "")
                                .replace("=", ": "))
                        .code(BAD_REQUEST.value())
                        .subCode(1)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    private ResponseEntity<ErrorResponse> buildDefaultResponse(Exception e, HttpStatus httpStatus, Integer subCode) {
        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.builder()
                        .message(e.getMessage())
                        .code(httpStatus.value())
                        .subCode(subCode)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
