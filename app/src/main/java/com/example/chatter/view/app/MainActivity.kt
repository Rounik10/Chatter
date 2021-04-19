package com.example.chatter.view.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.chatter.R
import com.example.chatter.databinding.ActivityMainBinding
import com.example.chatter.model.UserModel
import com.example.chatter.view.auth.LogInFragDirections
import io.getstream.chat.android.client.ChatClient

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
            val currentUser = getSharedPreferences("ChatUser", Context.MODE_PRIVATE)

            val id = currentUser.getString("id", "")!!
            val name = currentUser.getString("name", "")!!
            val email = currentUser.getString("email", "")!!

            if (id != "") {
                val user = UserModel(id, name, email)
                val action = LogInFragDirections.actionLogInFragToHomeFrag(user)
                navController.navigate(action)
            }
        }
    }
}