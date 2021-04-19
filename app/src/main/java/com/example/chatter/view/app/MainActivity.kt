package com.example.chatter.view.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.chatter.R
import com.example.chatter.databinding.ActivityMainBinding
import com.example.chatter.model.UserModel
import com.example.chatter.view.auth.LogInFragDirections
import com.example.chatter.viewmodel.AuthViewModel
import com.example.chatter.viewmodel.FireStoreViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.name

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private val client = ChatClient.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment)

        if (navController.currentDestination?.id == R.id.logInFrag) {
            val authViewModel = AuthViewModel()
            val currentUser = client.getCurrentUser()

            if (currentUser != null) {
                val user = UserModel(currentUser.id, currentUser.name, currentUser.extraData["email"].toString())
                val action = LogInFragDirections.actionLogInFragToHomeFrag(user)
                navController.navigate(action)
            } else if (authViewModel.getUid().isNotBlank()) {
                FireStoreViewModel().getUser(authViewModel.getUid()).addOnSuccessListener {
                    val user = it.toObject(UserModel::class.java)!!
                    val action = LogInFragDirections.actionLogInFragToHomeFrag(user)
                    navController.navigate(action)
                }
            }
        }
    }
}