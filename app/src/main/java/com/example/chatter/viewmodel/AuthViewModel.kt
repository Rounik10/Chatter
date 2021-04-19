package com.example.chatter.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chatter.model.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    suspend fun registerWithEmailPass(email: TextInputLayout, password: TextInputLayout): Task<AuthResult>? {
        val validInputs = withContext(Main) {
            validEmailPass(email, password)
        }
        return if (validInputs) {
            authRepository.registerWithEmailPass(
                    email.editText?.text.toString(),
                    password.editText?.text.toString()
            )
        } else null
    }

    fun loginWithEmailPass(email: TextInputLayout, password: TextInputLayout): Task<AuthResult>? {
        return if (validEmailPass(email, password)) {
            val emailText = email.editText?.text.toString()
            val passwordText = password.editText?.text.toString()
            authRepository.loginWithEmailPassword(email = emailText, password = passwordText)
        } else null
    }

    private fun validEmailPass(
            emailInput: TextInputLayout,
            passwordInput: TextInputLayout
    ): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"

        val email = emailInput.editText?.text.toString()
        var isEmailValid = true
        if (!email.matches(emailPattern.toRegex())) {
            emailInput.error = "Invalid Email"
            isEmailValid = false
        }

        val password = passwordInput.editText?.text.toString()
        var isPasswordValid = true
        if (!password.matches(passwordPattern.toRegex())) {
            passwordInput.error =
                    "Password must contain at least one Upper Case, one Lower Case, Digit, and a Special Character"
            isPasswordValid = false
        }

        return isEmailValid and isPasswordValid
    }

}