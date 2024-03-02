package az.crocusoft.CrocusoftDailyReport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCartItemOwnership(ProjectNotFoundException exception) {
        ExceptionResponse response =  new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCartItemOwnership(EmployeeNotFoundException exception) {
        ExceptionResponse response =  new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(DailyReportNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCartItemOwnership(DailyReportNotFoundException exception) {
        ExceptionResponse response =  new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCartItemOwnership(TeamNotFoundException exception) {
        ExceptionResponse response =  new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(TeamHasAssociatedEmployeesException.class)
    public ResponseEntity<ExceptionResponse> handleCartItemOwnership(TeamHasAssociatedEmployeesException exception) {
        ExceptionResponse response =  new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.BAD_REQUEST.value()
                , HttpStatus.BAD_REQUEST
                , exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(UpdateTimeException.class)
    public ResponseEntity<ExceptionResponse> handleCartItemOwnership(UpdateTimeException exception) {
        ExceptionResponse response =  new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.REQUEST_TIMEOUT.value()
                , HttpStatus.REQUEST_TIMEOUT
                , exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);

    }
    @ExceptionHandler(PasswordChangeIsFalse.class)
    public ResponseEntity<ExceptionResponse> handleCartItemOwnership(PasswordChangeIsFalse exception) {
        ExceptionResponse response =  new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.BAD_REQUEST.value()
                , HttpStatus.BAD_REQUEST
                , exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(TeamAlreadyExistException.class)
    public ResponseEntity<ExceptionResponse> handleTeamAlreadyExistException(TeamAlreadyExistException ex) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ProjectAlreadyExistException.class)
    public ResponseEntity<ExceptionResponse> handleProjectAlreadyExistException(ProjectAlreadyExistException ex) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleProjectAlreadyExistException(TokenNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ExceptionResponse> handleProjectAlreadyExistException(UnsupportedOperationException ex) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                HttpStatus.NOT_ACCEPTABLE,
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }
}
