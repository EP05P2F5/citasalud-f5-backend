package com.feature5.pqrs.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestDTOTest {

    @Test
    void testConstructorAndSetters() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setNickname("usuario1");
        dto.setPassword("pass123");

        assertEquals("usuario1", dto.getNickname());
        assertEquals("pass123", dto.getPassword());

        LoginRequestDTO dto2 = new LoginRequestDTO("usuario2", "pass456");
        assertEquals("usuario2", dto2.getNickname());
        assertEquals("pass456", dto2.getPassword());
    }
}
