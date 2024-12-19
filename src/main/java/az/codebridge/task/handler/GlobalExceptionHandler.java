package az.codebridge.task.handler;

import az.codebridge.task.exception.BalanceException;
import az.codebridge.task.exception.ProductNotFoundException;
import az.codebridge.task.exception.StockException;
import az.codebridge.task.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends DefaultErrorAttributes {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handle(UserNotFoundException ex,
                                                      WebRequest request) {
        log.trace("User not found exception occurred", ex);
        return ofType(request, HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handle(ProductNotFoundException ex,
                                                      WebRequest request) {
        log.trace("Product not found exception occurred", ex);
        return ofType(request, HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(StockException.class)
    public ResponseEntity<Map<String, Object>> handle(StockException ex,
                                                      WebRequest request) {
        log.trace("stock exception occurred", ex);
        return ofType(request, HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }


    @ExceptionHandler(BalanceException.class)
    public ResponseEntity<Map<String, Object>> handle(BalanceException ex,
                                                      WebRequest request) {
        log.trace("balance exception occurred", ex);
        return ofType(request, HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handle(MethodArgumentNotValidException ex,
                                                      WebRequest request) {
        log.trace("Application exception occurred", ex);
        final List<ConstraintsViolationError> list = ex.getFieldErrors()
                .stream()
                .map(fieldError -> new ConstraintsViolationError(fieldError.getField(), fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()))
                .toList();

        return ofType(request, HttpStatus.NOT_FOUND, "Validation failed for argument", list);
    }

    private ResponseEntity<Map<String, Object>> ofType(WebRequest request, HttpStatus status, String message, List<ConstraintsViolationError> list) {
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        attributes.put("status", status.value());
        attributes.put("error", message);
        attributes.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        attributes.put("fieldErrors", list);
        return new ResponseEntity<>(attributes, status);
    }

}
