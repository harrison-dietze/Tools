package tools.exception;

import tools.exception.MappedExceptions;
import tools.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MappedExceptions.PagamentoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(MappedExceptions.PagamentoNaoEncontradoException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        System.out.println(ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                status.value(),
                "Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MappedExceptions.PagamentoJaCanceladoException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(MappedExceptions.PagamentoJaCanceladoException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                "Bad Request",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

}