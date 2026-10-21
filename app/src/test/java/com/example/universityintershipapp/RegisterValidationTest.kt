package com.example.universityintershipapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterValidationTest {

    // --- STUDENT REGISTRATION VALIDATION ---

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

    // --- EMPLOYER REGISTRATION VALIDATION ---

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
