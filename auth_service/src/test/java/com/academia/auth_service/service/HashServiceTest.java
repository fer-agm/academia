package com.academia.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @InjectMocks
    private HashService hashService;

    @BeforeEach
    void setUp() {
        // Given a fresh HashService instance (no dependencies)
        hashService = new HashService();
    }

    @Test
    void sha1_returnsNonNull40CharLowercaseHex() {
        // Given
        String input = "1234";

        // When
        String hash = hashService.sha1(input);

        // Then
        assertNotNull(hash);
        assertEquals(40, hash.length());
        assertTrue(hash.matches("[0-9a-f]{40}"), "Hash must be 40-char lowercase hex");
    }

    @Test
    void sha1_isDeterministic() {
        // Given
        String input = "secreta123";

        // When
        String first = hashService.sha1(input);
        String second = hashService.sha1(input);

        // Then
        assertEquals(first, second);
    }

    @Test
    void sha1_knownVector_matchesExpectedHash() {
        // Given the well-known SHA-1 of "1234"
        String input = "1234";
        String expected = "7110eda4d09e062aa5e4a390b0a572ac0d2c0220";

        // When
        String hash = hashService.sha1(input);

        // Then
        assertEquals(expected, hash);
    }

    @Test
    void sha1_emptyString_matchesKnownEmptyHash() {
        // Given the well-known SHA-1 of the empty string
        String input = "";
        String expected = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

        // When
        String hash = hashService.sha1(input);

        // Then
        assertNotNull(hash);
        assertEquals(40, hash.length());
        assertEquals(expected, hash);
    }

    @Test
    void sha1_differentInputs_produceDifferentHashes() {
        // Given
        String inputA = "password1";
        String inputB = "password2";

        // When
        String hashA = hashService.sha1(inputA);
        String hashB = hashService.sha1(inputB);

        // Then
        assertNotEquals(hashA, hashB);
    }

    @Test
    void sha1_nullInput_throwsRuntimeException() {
        // Given
        String input = null;

        // When / Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> hashService.sha1(input));
    }

    @Test
    void sha1_unicodeInput_isHandledAsUtf8AndDeterministic() {
        // Given a non-ASCII input (UTF-8 handling)
        String input = "clavEñÁ";

        // When
        String first = hashService.sha1(input);
        String second = hashService.sha1(input);

        // Then
        assertNotNull(first);
        assertEquals(40, first.length());
        assertTrue(first.matches("[0-9a-f]{40}"));
        assertFalse(first.isBlank());
        assertEquals(first, second);
    }
}
