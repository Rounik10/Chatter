package com.example.chatter.viewmodel

import com.example.chatter.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FireStoreViewModel {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun addUser(user: UserModel?) {
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }
        }
    }
}