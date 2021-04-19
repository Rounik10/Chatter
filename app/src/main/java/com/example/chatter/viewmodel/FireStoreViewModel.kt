package com.example.chatter.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.chatter.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class FireStoreViewModel {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    val userModel = MutableLiveData<UserModel>()

    fun addUser(user: UserModel?) {
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }
        }
    }

    fun getUser(uid: String) : Task<DocumentSnapshot> = userCollection.document(uid).get()
}