package com.example.universityintershipapp

sealed class ValidationResult {
    data class Success(val email: String = "", val isEmployer: Boolean = false) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object ValidationUtils {
    fun validateLoginInput(emailInput: String, passwordInput: String): ValidationResult {
        val cleanEmail = emailInput.trim().lowercase()
        return when {
            cleanEmail.isBlank() -> ValidationResult.Error("Please enter your email address.")
            passwordInput.isBlank() -> ValidationResult.Error("Please enter your password.")
            !cleanEmail.contains("@") || !cleanEmail.contains(".") -> ValidationResult.Error("Please enter a valid email address.")
            cleanEmail.contains("admin") -> ValidationResult.Success(cleanEmail, isEmployer = false)
            cleanEmail.contains("employer") || cleanEmail.contains("company") -> ValidationResult.Success(cleanEmail, isEmployer = true)
            else -> ValidationResult.Success(cleanEmail, isEmployer = false)
        }
    }

    fun validateStudentRegistrationInput(
        fullName: String,
        studentId: String,
        email: String,
        phoneNumber: String,
        programme: String,
        yearOfStudy: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        return when {
            fullName.trim().isBlank() -> ValidationResult.Error("Please enter your full name.")
            studentId.trim().isBlank() -> ValidationResult.Error("Please enter your student ID.")
            email.trim().isBlank() || !email.contains("@") -> ValidationResult.Error("Please enter a valid email address.")
            phoneNumber.trim().isBlank() -> ValidationResult.Error("Please enter your phone number.")
            programme.trim().isBlank() -> ValidationResult.Error("Please select your programme of study.")
            yearOfStudy.trim().isBlank() -> ValidationResult.Error("Please select your year of study.")
            password.isBlank() || password.length < 6 -> ValidationResult.Error("Password must be at least 6 characters long.")
            confirmPassword != password -> ValidationResult.Error("Passwords do not match.")
            else -> ValidationResult.Success()
        }
    }

    fun validateEmployerRegistrationInput(
        contactName: String,
        companyName: String,
        email: String,
        phoneNumber: String,
        sector: String,
        location: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        return when {
            contactName.trim().isBlank() -> ValidationResult.Error("Please enter the contact person's full name.")
            companyName.trim().isBlank() -> ValidationResult.Error("Please enter your company or organization name.")
            email.trim().isBlank() || !email.contains("@") -> ValidationResult.Error("Please enter a valid organization email address.")
            phoneNumber.trim().isBlank() -> ValidationResult.Error("Please enter your phone number.")
            sector.trim().isBlank() -> ValidationResult.Error("Please select your industry or sector.")
            location.trim().isBlank() -> ValidationResult.Error("Please enter your organization's location.")
            password.isBlank() || password.length < 6 -> ValidationResult.Error("Password must be at least 6 characters long.")
            confirmPassword != password -> ValidationResult.Error("Passwords do not match.")
            else -> ValidationResult.Success()
        }
    }
}
