package com.blossy.flowerstore.utils
import android.widget.EditText


fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isStrongPassword(): Boolean {
    val hasUpperCase = contains(Regex("[A-Z]"))
    val hasLowerCase = contains(Regex("[a-z]"))
    val hasDigit = contains(Regex("[0-9]"))
    val hasSpecialChar = contains(Regex("[^A-Za-z0-9]"))
    return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
}

fun String.hasNoWhiteSpaceEdge(): Boolean {
    return this == this.trim()
}

fun EditText.isNotEmptyOrShowError(errorMsg: String): Boolean {
    return if (this.text.toString().trim().isEmpty()) {
        this.error = errorMsg
        false
    } else {
        this.error = null
        true
    }
}

fun EditText.hasMinLength(min: Int, errorMsg: String): Boolean {
    return if (this.text.toString().trim().length < min) {
        this.error = errorMsg
        false
    } else {
        this.error = null
        true
    }
}

fun EditText.hasStrongPassword(errorMsg: String): Boolean {
    return if (!this.text.toString().isStrongPassword()) {
        this.error = errorMsg
        false
    } else {
        this.error = null
        true
    }
}

fun EditText.hasNoWhiteSpaceEdge(errorMsg: String): Boolean {
    return if (!this.text.toString().hasNoWhiteSpaceEdge()) {
        this.error = errorMsg
        false
    } else {
        this.error = null
        true
    }
}

fun EditText.matches(other: EditText, errorMsg: String): Boolean {
    return if (this.text.toString().trim() != other.text.toString().trim()) {
        this.error = errorMsg
        false
    } else {
        this.error = null
        true
    }
}

fun EditText.clearError() {
    this.error = null
}

fun validateChangePasswordInputs(
    currentInput: EditText,
    newInput: EditText,
    reNewInput: EditText
): Boolean {
    currentInput.clearError()
    newInput.clearError()
    reNewInput.clearError()

    val currentPassword = currentInput.text.toString().trim()
    val newPassword = newInput.text.toString().trim()

    var isValid = true

    if (!currentInput.isNotEmptyOrShowError("Please enter your current password")) {
        isValid = false
    }

    if (!newInput.isNotEmptyOrShowError("Please enter a new password")) {
        isValid = false
    } else {
        if (!newInput.hasMinLength(6, "New password must be at least 6 characters")) {
            isValid = false
        }
        if (!newInput.hasStrongPassword("Password must contain upper, lower, number and special character")) {
            isValid = false
        }
        if (!newInput.hasNoWhiteSpaceEdge("Password should not start or end with whitespace")) {
            isValid = false
        }
    }

    if (!reNewInput.isNotEmptyOrShowError("Please confirm your new password")) {
        isValid = false
    } else if (!reNewInput.matches(newInput, "Passwords do not match")) {
        isValid = false
    }

    if (currentPassword == newPassword && currentPassword.isNotEmpty()) {
        newInput.error = "New password must be different from the current password"
        isValid = false
    }

    return isValid
}


//View Extensions