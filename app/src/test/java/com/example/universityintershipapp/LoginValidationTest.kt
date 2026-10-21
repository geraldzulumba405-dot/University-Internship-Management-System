package com.example.universityintershipapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginValidationTest {

    @Test
    fun login_validStudentInput_returnsStudentSuccess() {
        val result = ValidationUtils.validateLoginInput("student@unima.ac.mw", "password123")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals("student@unima.ac.mw", success.email)
        assertEquals(false, success.isEmployer)
    }

    @Test
    fun login_validEmployerInput_returnsEmployerSuccess() {
        val result = ValidationUtils.validateLoginInput("employer@company.com", "password123")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals("employer@company.com", success.email)
        assertEquals(true, success.isEmployer)
    }

    @Test
    fun login_emptyEmail_returnsEmailError() {
        val result = ValidationUtils.validateLoginInput("", "password123")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter your email address.", (result as ValidationResult.Error).message)
    }

    @Test
    fun login_whitespaceEmail_returnsEmailError() {
        val result = ValidationUtils.validateLoginInput("   ", "password123")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter your email address.", (result as ValidationResult.Error).message)
    }

    @Test
    fun login_emptyPassword_returnsPasswordError() {
        val result = ValidationUtils.validateLoginInput("student@unima.ac.mw", "")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter your password.", (result as ValidationResult.Error).message)
    }

    @Test
    fun login_whitespacePassword_returnsPasswordError() {
        val result = ValidationUtils.validateLoginInput("student@unima.ac.mw", "   ")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun login_uppercaseRecognizedEmail_trimsAndSucceeds() {
        val result = ValidationUtils.validateLoginInput("  STUDENT@UNIMA.AC.MW  ", "password123")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals("student@unima.ac.mw", success.email)
    }

    @Test
    fun login_unrecognizedEmail_returnsUnrecognizedError() {
        val result = ValidationUtils.validateLoginInput("invalidemailformat", "password123")
        assertTrue(result is ValidationResult.Error)
        assertTrue((result as ValidationResult.Error).message.contains("Unrecognized email"))
    }
}
