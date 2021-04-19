package com.example.chatter.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = ""
): Parcelable