package com.academia.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET =
            "a-long-enough-secret-string-of-at-least-32-bytes-1234567890";

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Given the @Value-injected secret is null under @InjectMocks, set it manually
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
    }

    @Test
    void generateToken_returnsNonEmptyJwt() {
        // Given
        String run = "11111111-1";

        // When
        String token = jwtService.generateToken(run);

        // Then
        assertNotNull(token);
        assertFalse(token.isBlank());
        // A JWS has 3 dot-separated sections
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateToken_thenGetRunFromToken_roundTrips() {
        // Given
        String run = "22222222-2";

        // When
        String token = jwtService.generateToken(run);
        String extracted = jwtService.getRunFromToken(token);

        // Then
        assertEquals(run, extracted);
    }

    @Test
    void getRunFromToken_acceptsBearerPrefix() {
        // Given
        String run = "33333333-3";
        String token = jwtService.generateToken(run);

        // When
        String extracted = jwtService.getRunFromToken("Bearer " + token);

        // Then
        assertEquals(run, extracted);
    }

    @Test
    void getRunFromToken_nullToken_returnsNull() {
        // Given
        String token = null;

        // When
        String extracted = jwtService.getRunFromToken(token);

        // Then
        assertNull(extracted);
    }

    @Test
    void getRunFromToken_blankToken_returnsNull() {
        // Given
        String token = "   ";

        // When
        String extracted = jwtService.getRunFromToken(token);

        // Then
        assertNull(extracted);
    }

    @Test
    void getRunFromToken_garbageToken_returnsNull() {
        // Given
        String token = "this.is.not.a.valid.jwt";

        // When
        String extracted = jwtService.getRunFromToken(token);

        // Then
        assertNull(extracted);
    }

    @Test
    void getRunFromToken_tokenSignedWithDifferentSecret_returnsNull() {
        // Given a token issued by a service using a different secret
        JwtService other = new JwtService();
        ReflectionTestUtils.setField(other, "secret",
                "completely-different-secret-of-at-least-32-bytes-abcdefghij");
        String foreignToken = other.generateToken("44444444-4");

        // When
        String extracted = jwtService.getRunFromToken(foreignToken);

        // Then
        assertNull(extracted);
    }

    @Test
    void isValid_validToken_returnsTrue() {
        // Given
        String token = jwtService.generateToken("55555555-5");

        // When
        boolean result = jwtService.isValid(token);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_validTokenWithBearerPrefix_returnsTrue() {
        // Given
        String token = jwtService.generateToken("66666666-6");

        // When
        boolean result = jwtService.isValid("Bearer " + token);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_nullToken_returnsFalse() {
        // Given
        String token = null;

        // When
        boolean result = jwtService.isValid(token);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_blankToken_returnsFalse() {
        // Given
        String token = "";

        // When
        boolean result = jwtService.isValid(token);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_garbageToken_returnsFalse() {
        // Given
        String token = "abc.def.ghi";

        // When
        boolean result = jwtService.isValid(token);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_tokenSignedWithDifferentSecret_returnsFalse() {
        // Given a token signed with another secret
        JwtService other = new JwtService();
        ReflectionTestUtils.setField(other, "secret",
                "yet-another-distinct-secret-of-at-least-32-bytes-klmnopqrst");
        String foreignToken = other.generateToken("77777777-7");

        // When
        boolean result = jwtService.isValid(foreignToken);

        // Then
        assertFalse(result);
    }
}
