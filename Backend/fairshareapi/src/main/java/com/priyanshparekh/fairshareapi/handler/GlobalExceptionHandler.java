package com.priyanshparekh.fairshareapi.handler;

import com.priyanshparekh.fairshareapi.handler.exception.AccountCreatedButLoginFailedException;
import com.priyanshparekh.fairshareapi.handler.exception.AuthServiceException;
import com.priyanshparekh.fairshareapi.handler.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(exception = AuthServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthServiceException(AuthServiceException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(exception = AccountCreatedButLoginFailedException.class)
    public ResponseEntity<ErrorResponse> handleAccountCreatedButLoginFailedException(AccountCreatedButLoginFailedException exception) {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponse(exception.getMessage()));
    }

}
