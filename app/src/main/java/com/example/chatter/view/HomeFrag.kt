package com.example.chatter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatter.R
import com.example.chatter.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFrag : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.logoutBtn.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_homeFrag_to_logInFrag)
        }

        return binding.root
    }

}