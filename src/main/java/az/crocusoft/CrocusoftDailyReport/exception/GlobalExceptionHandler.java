package az.crocusoft.CrocusoftDailyReport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ExceptionResponse handleCartItemOwnership(ProjectNotFoundException exception) {
        return new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
    }
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ExceptionResponse handleCartItemOwnership(EmployeeNotFoundException exception) {
        return new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
    }
    @ExceptionHandler(DailyReportNotFoundException.class)
    public ExceptionResponse handleCartItemOwnership(DailyReportNotFoundException exception) {
        return new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
    }
    @ExceptionHandler(TeamNotFoundException.class)
    public ExceptionResponse handleCartItemOwnership(TeamNotFoundException exception) {
        return new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.NOT_FOUND.value()
                , HttpStatus.NOT_FOUND
                , exception.getMessage());
    }
    @ExceptionHandler(TeamHasAssociatedEmployeesException.class)
    public ExceptionResponse handleCartItemOwnership(TeamHasAssociatedEmployeesException exception) {
        return new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.BAD_REQUEST.value()
                , HttpStatus.BAD_REQUEST
                , exception.getMessage());
    }
    @ExceptionHandler(UpdateTimeException.class)
    public ExceptionResponse handleCartItemOwnership(UpdateTimeException exception) {
        return new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.REQUEST_TIMEOUT.value()
                , HttpStatus.REQUEST_TIMEOUT
                , exception.getMessage());
    }
    @ExceptionHandler(PasswordChangeIsFalse.class)
    public ExceptionResponse handleCartItemOwnership(PasswordChangeIsFalse exception) {
        return new ExceptionResponse(
                LocalDateTime.now()
                , HttpStatus.BAD_REQUEST.value()
                , HttpStatus.BAD_REQUEST
                , exception.getMessage());
    }
}
