package com.example.gccoffee.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void testInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("accc"));
    }

    @Test
    void testValidEmail() {
        Email email = new Email("aa@naver.com");
        assertEquals("aa@naver.com", email.getAddress());
    }

    @Test
    void testEqEmail() {
        Email email = new Email("aa@naver.com");
        Email email2 = new Email("aa@naver.com");
        System.out.println(email.hashCode());
        System.out.println(email2.hashCode());
        assertEquals(email, email2);
    }

}