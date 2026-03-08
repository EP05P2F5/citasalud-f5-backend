package com.feature5.pqrs.testJunit.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.feature5.pqrs.config.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {

    @Autowired
    JwtUtils jwtUtils;

    @Test
    void generateAndValidateToken() {
        String token = jwtUtils.generateToken("alice");
        assertNotNull(token);

        String username = jwtUtils.extractUsername(token);
        assertEquals("alice", username);

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void invalidTokenReturnsFalse() {
        // token random string should not validate
        assertFalse(jwtUtils.validateToken("not.a.real.token"));

        // malformed token should throw on extractUsername -> parser will throw; ensure it bubbles as runtime
        assertThrows(Exception.class, () -> jwtUtils.extractUsername("not.a.real.token"));
    }
}
