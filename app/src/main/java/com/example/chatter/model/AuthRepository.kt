package com.example.chatter.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential

class AuthRepository {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val userMutableLiveData = MutableLiveData<FirebaseUser>()

    fun registerWithEmailPass(email: String, password: String) : MutableLiveData<FirebaseUser> {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            userMutableLiveData.value = it.user
        }.addOnFailureListener {
            it.printStackTrace()
        }
        return userMutableLiveData
    }

    fun loginWithEmailPassword(email: String, password: String) : MutableLiveData<FirebaseUser> {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            userMutableLiveData.value = it.user
        }.addOnFailureListener {
            it.printStackTrace()
        }
        return userMutableLiveData
    }

    fun googleSignUp(credential: GoogleAuthCredential) {
        mAuth.signInWithCredential(credential).addOnSuccessListener {
            userMutableLiveData.value = it.user
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}