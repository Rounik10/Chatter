package com.example.chatter.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.chatter.viewmodel.AuthViewModel
import com.example.chatter.databinding.FragmentEmailSignUpBinding

class EmailSignUpFrag : Fragment() {

    private var _binding: FragmentEmailSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailSignUpBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        authViewModel = AuthViewModel()

        binding.apply {
            registerBtn.setOnClickListener {
                if(usernameIpEdt.text.isNullOrBlank()) {
                    usernameIp.error = "Invalid Username"
                }
                authViewModel.registerWithEmailPass(
                    emailIp,
                    passwordIp
                )

                if (authViewModel.userMutableLiveData == null) {
                    Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}