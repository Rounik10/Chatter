package com.example.chatter.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatter.R
import com.example.chatter.databinding.FragmentLogInBinding
import com.example.chatter.model.UserModel
import com.example.chatter.viewmodel.AuthViewModel
import com.example.chatter.viewmodel.FireStoreViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LogInFrag : Fragment() {
    private var _binging: FragmentLogInBinding? = null
    private val binding get() = _binging!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binging = FragmentLogInBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.apply {
            loginBtn.setOnClickListener {
                loginBtn.isEnabled = false
                binding.loginProgress.visibility = View.VISIBLE
                login()
            }
            registerText.setOnClickListener {
                findNavController().navigate(R.id.action_logInFrag_to_emailSignUpFrag)
            }
        }

        return binding.root
    }

    private fun login() {
        binding.apply {
            loginProgress.visibility = View.VISIBLE
            val authViewModel = AuthViewModel()
            val loginTask = authViewModel.loginWithEmailPass(binding.emailIp, binding.passwordIp)
            if (loginTask == null) {
                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                loginBtn.isEnabled = true
                loginProgress.isVisible = false
                return
            }
            GlobalScope.launch(Dispatchers.IO) {
                loginTask.addOnFailureListener {
                    when (it) {
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(context, "Invalid User", Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    loginBtn.isEnabled = true
                    loginProgress.isVisible = false
                }.addOnSuccessListener {
                    val user = it.user
                    if (user != null) {
                        FireStoreViewModel().getUser(user.uid).addOnSuccessListener { userSnap ->
                            val chatUser = userSnap.toObject(UserModel::class.java)
                            if (chatUser == null) {
                                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val action = LogInFragDirections.actionLogInFragToHomeFrag(chatUser)
                                findNavController().navigate(action)
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    loginBtn.isEnabled = true
                    loginProgress.isVisible = false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binging = null
    }
}