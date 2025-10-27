package pl.kul.taskmanager.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void should_handleTaskNotFoundException() {
        // given
        TaskNotFoundException exception = new TaskNotFoundException(1L);

        // when
        ResponseEntity<String> response = globalExceptionHandler.handleTaskNotFoundException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found with ID: 1", response.getBody());
    }

    @Test
    void should_handleProjectNotFoundException() {
        // given
        ProjectNotFoundException exception = new ProjectNotFoundException(1L);

        // when
        ResponseEntity<String> response = globalExceptionHandler.handleProjectNotFoundException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Project not found with ID: 1", response.getBody());
    }

    @Test
    void should_handleGenericException() {
        // given
        Exception exception = new Exception("Unexpected error");

        // when
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: Unexpected error", response.getBody());
    }

    @Test
    void should_handleValidationException() {
        // given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("objectName", "field1", "must not be null");
        FieldError fieldError2 = new FieldError("objectName", "field2", "must be greater than 0");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // when
        ResponseEntity<String> response = globalExceptionHandler.handleValidationException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("field1: must not be null, field2: must be greater than 0", response.getBody());
    }

    @Test
    void should_handleIllegalArgumentException() {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // when
        ResponseEntity<String> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }
}
