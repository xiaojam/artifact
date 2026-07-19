package test.takehome.transinnovate.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XdockIntegrationExceptionTest {

    private XdockIntegrationException exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new XdockIntegrationException();
    }

    @Test
    void handleXdockErrors_ShouldReturnOriginalStatusAndBody() {
        String mockResponseBody = "{\"errors\":[\"quantity must be greater than 0\"]}";

        HttpClientErrorException xdockException = HttpClientErrorException.create(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            HttpHeaders.EMPTY,
            mockResponseBody.getBytes(StandardCharsets.UTF_8),
            StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = exceptionHandler.handleXdockErrors(xdockException);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mockResponseBody, response.getBody());
    }

    @Test
    void handleGeneralErrors_ShouldReturn500WithJsonFormat() {
        String errorMessage = "Database connection timeout";
        Exception generalException = new Exception(errorMessage);

        ResponseEntity<String> response = exceptionHandler.handleGeneralErrors(generalException);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        String expectedJsonBody = "{\"error\": \"Integration Error\", \"message\": \"Database connection timeout\"}";
        assertEquals(expectedJsonBody, response.getBody());
    }
}