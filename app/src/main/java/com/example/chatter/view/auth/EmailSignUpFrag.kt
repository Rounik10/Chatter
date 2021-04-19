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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
                registerProgress.visibility = View.VISIBLE
                registerBtn.isEnabled = false

                if (usernameIpEdt.text.isNullOrBlank()) {
                    usernameIp.error = "Invalid Username"
                    registerProgress.visibility = View.GONE
                    registerBtn.isEnabled = true
                    Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                GlobalScope.launch(Dispatchers.IO) {
                    val task = authViewModel.registerWithEmailPass(emailIp, passwordIp)
                    if (task == null) {
                        handleFail()
                        return@launch
                    }
                    val result = task.await()
                    if (result.user == null) {
                        handleFail()
                    } else {
                        val user = UserModel(
                                result.user!!.uid,
                                binding.usernameIpEdt.text.toString(),
                                binding.emailIpEdt.text.toString()
                        )
                        FireStoreViewModel().addUser(user).await()
                        withContext(Main) {
                            navigateToHome(user)
                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        return binding.root
    }

    private suspend fun handleFail() {
        withContext(Main) {
            binding.registerProgress.visibility = View.GONE
            binding.registerBtn.isEnabled = true
            Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHome(user: UserModel) {
        val action = EmailSignUpFragDirections.actionEmailSignUpFragToHomeFrag(user)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}