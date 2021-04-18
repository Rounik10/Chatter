package com.example.chatter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatter.R
import com.example.chatter.databinding.FragmentLogInBinding
import com.example.chatter.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class LogInFrag : Fragment() {
    private var _binging: FragmentLogInBinding? = null
    private val binding get() = _binging!!
    private val authViewModel = AuthViewModel()
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binging = FragmentLogInBinding.inflate(inflater, container, false)
        if (mAuth.currentUser != null) {
            findNavController().navigate(R.id.action_logInFrag_to_homeFrag)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.apply {
            loginBtn.setOnClickListener { login() }
            registerText.setOnClickListener {
                findNavController().navigate(R.id.action_logInFrag_to_emailSignUpFrag)
            }
        }

        return binding.root
    }

    private fun login() {
        binding.apply {
            authViewModel.loginWithEmailPass(binding.emailIp, binding.passwordIp)
            if (authViewModel.userMutableLiveData == null) {
                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_logInFrag_to_homeFrag)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binging = null
    }
}