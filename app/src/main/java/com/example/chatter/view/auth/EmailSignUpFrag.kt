package com.example.chatter.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatter.databinding.FragmentEmailSignUpBinding
import com.example.chatter.model.UserModel
import com.example.chatter.viewmodel.AuthViewModel
import com.example.chatter.viewmodel.FireStoreViewModel
import com.google.firebase.auth.FirebaseAuth

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
                if (usernameIpEdt.text.isNullOrBlank()) {
                    usernameIp.error = "Invalid Username"
                }
                authViewModel.registerWithEmailPass(
                        emailIp,
                        passwordIp
                )
                val mAuth = FirebaseAuth.getInstance()
                val currentUser = mAuth.currentUser
                if (currentUser == null) {
                    Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                } else {
                    val user = UserModel(
                            currentUser.uid,
                            binding.usernameIpEdt.text.toString(),
                            binding.emailIpEdt.text.toString()
                    )
                    FireStoreViewModel().addUser(user)
                    val action = EmailSignUpFragDirections.actionEmailSignUpFragToHomeFrag(user)
                    findNavController().navigate(action)
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