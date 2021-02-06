package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.exception.WalletErrorException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        ex.getAllErrors().forEach(error -> {
            response.getDetails().add(new ErrorResponseDetail("VAL-000", error.getDefaultMessage()));
        });

        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = WalletErrorException.class)
    public final ResponseEntity<ErrorResponse> handleWalletException(WalletErrorException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.getDetails().add(new ErrorResponseDetail(ex.getError().getCode(), ex.getError().getMessage()));
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private static class ErrorResponse {

        private List<ErrorResponseDetail> details = new ArrayList<>();

        public List<ErrorResponseDetail> getDetails() {
            return details;
        }

        public void setDetails(List<ErrorResponseDetail> details) {
            this.details = details;
        }
    }

    private static class ErrorResponseDetail {
        private String code;
        private String message;

        public ErrorResponseDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
