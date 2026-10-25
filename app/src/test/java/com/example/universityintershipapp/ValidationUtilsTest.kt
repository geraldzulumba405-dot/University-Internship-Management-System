package com.example.universityintershipapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    // --- LOGIN VALIDATION TESTS ---

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
        val result = ValidationUtils.validateLoginInput("student@unima.ac.mw", "")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter your password.", (result as ValidationResult.Error).message)
    }

    @Test
    fun login_recognizedStudentEmail_returnsStudentSuccess() {
        val result = ValidationUtils.validateLoginInput("student@unima.ac.mw", "password123")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals("student@unima.ac.mw", success.email)
        assertEquals(false, success.isEmployer)
    }

    @Test
    fun login_recognizedEmployerEmail_returnsEmployerSuccess() {
        val result = ValidationUtils.validateLoginInput("employer@company.com", "password123")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals("employer@company.com", success.email)
        assertEquals(true, success.isEmployer)
    }

    @Test
    fun login_uppercaseRecognizedEmail_trimsAndSucceeds() {
        val result = ValidationUtils.validateLoginInput("  STUDENT@UNIMA.AC.MW  ", "password123")
        assertTrue(result is ValidationResult.Success)
        val success = result as ValidationResult.Success
        assertEquals("student@unima.ac.mw", success.email)
    }

    @Test
    fun login_invalidEmailFormat_returnsInvalidEmailError() {
        val result = ValidationUtils.validateLoginInput("unknown_domain_no_at", "password123")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter a valid email address.", (result as ValidationResult.Error).message)
    }

    // --- STUDENT REGISTRATION VALIDATION TESTS ---

    @Test
    fun studentRegister_emptyFullName_returnsError() {
        val result = ValidationUtils.validateStudentRegistrationInput(
            fullName = "   ",
            studentId = "U2021-0842",
            email = "student@unima.ac.mw",
            phoneNumber = "+265 999 123 456",
            programme = "B.Sc. Computer Science",
            yearOfStudy = "Year 3",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter your full name.", (result as ValidationResult.Error).message)
    }

    @Test
    fun studentRegister_emptyStudentId_returnsError() {
        val result = ValidationUtils.validateStudentRegistrationInput(
            fullName = "Gerald Zulumba",
            studentId = "",
            email = "student@unima.ac.mw",
            phoneNumber = "+265 999 123 456",
            programme = "B.Sc. Computer Science",
            yearOfStudy = "Year 3",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter your student ID.", (result as ValidationResult.Error).message)
    }

    @Test
    fun studentRegister_invalidEmailFormat_returnsError() {
        val result = ValidationUtils.validateStudentRegistrationInput(
            fullName = "Gerald Zulumba",
            studentId = "U2021-0842",
            email = "invalidemailformat",
            phoneNumber = "+265 999 123 456",
            programme = "B.Sc. Computer Science",
            yearOfStudy = "Year 3",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter a valid email address.", (result as ValidationResult.Error).message)
    }

    @Test
    fun studentRegister_shortPassword_returnsError() {
        val result = ValidationUtils.validateStudentRegistrationInput(
            fullName = "Gerald Zulumba",
            studentId = "U2021-0842",
            email = "student@unima.ac.mw",
            phoneNumber = "+265 999 123 456",
            programme = "B.Sc. Computer Science",
            yearOfStudy = "Year 3",
            password = "123",
            confirmPassword = "123"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals("Password must be at least 6 characters long.", (result as ValidationResult.Error).message)
    }

    @Test
    fun studentRegister_passwordMismatch_returnsError() {
        val result = ValidationUtils.validateStudentRegistrationInput(
            fullName = "Gerald Zulumba",
            studentId = "U2021-0842",
            email = "student@unima.ac.mw",
            phoneNumber = "+265 999 123 456",
            programme = "B.Sc. Computer Science",
            yearOfStudy = "Year 3",
            password = "password123",
            confirmPassword = "password456"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals("Passwords do not match.", (result as ValidationResult.Error).message)
    }

    @Test
    fun studentRegister_validData_returnsSuccess() {
        val result = ValidationUtils.validateStudentRegistrationInput(
            fullName = "Gerald Zulumba",
            studentId = "U2021-0842",
            email = "student@unima.ac.mw",
            phoneNumber = "+265 999 123 456",
            programme = "B.Sc. Computer Science",
            yearOfStudy = "Year 3",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result is ValidationResult.Success)
    }

    // --- EMPLOYER REGISTRATION VALIDATION TESTS ---

    @Test
    fun employerRegister_emptyContactName_returnsError() {
        val result = ValidationUtils.validateEmployerRegistrationInput(
            contactName = "",
            companyName = "Apex Tech",
            email = "careers@company.com",
            phoneNumber = "+265 888 123 456",
            sector = "Information Technology",
            location = "Blantyre",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter the contact person's full name.", (result as ValidationResult.Error).message)
    }

    @Test
    fun employerRegister_emptyCompanyName_returnsError() {
        val result = ValidationUtils.validateEmployerRegistrationInput(
            contactName = "Chikondi Mapira",
            companyName = "  ",
            email = "careers@company.com",
            phoneNumber = "+265 888 123 456",
            sector = "Information Technology",
            location = "Blantyre",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result is ValidationResult.Error)
        assertEquals("Please enter your company or organization name.", (result as ValidationResult.Error).message)
    }

    @Test
    fun employerRegister_validData_returnsSuccess() {
        val result = ValidationUtils.validateEmployerRegistrationInput(
            contactName = "Chikondi Mapira",
            companyName = "Apex Tech Solutions",
            email = "careers@company.com",
            phoneNumber = "+265 888 123 456",
            sector = "Information Technology",
            location = "Blantyre",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result is ValidationResult.Success)
    }
}
