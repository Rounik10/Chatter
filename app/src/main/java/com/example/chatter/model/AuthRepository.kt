package com.example.chatter.model

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerWithEmailPass(email: String, password: String) =
        mAuth.createUserWithEmailAndPassword(email, password)

    fun loginWithEmailPassword(email: String, password: String) =
        mAuth.signInWithEmailAndPassword(email, password)

}