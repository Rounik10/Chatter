package com.example.chatter.view.chat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chatter.R
import com.example.chatter.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.ui.avatar.AvatarView
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

class HomeFrag : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val args: HomeFragArgs by navArgs()

    private val client = ChatClient.instance()
    private val TAG = HomeFragArgs::class.java.simpleName

    @SuppressLint("WrongConstant")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        setUpUser()
        setUpDrawer()
        setUpChannels()

        binding.channelListHeaderView.setOnUserAvatarClickListener {
            binding.drawerLayout.openDrawer(Gravity.START)
        }

        binding.channelsView.setChannelDeleteClickListener {
            deleteChannel(it)
        }

        binding.channelListHeaderView.setOnActionButtonClickListener {
            findNavController().navigate(R.id.action_homeFrag_to_userListFrag)
        }

        binding.channelsView.setChannelItemClickListener {
            val action = HomeFragDirections.actionHomeFragToChatFragment(it.id)
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun setUpChannels() {
        val filters = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(client.getCurrentUser()!!.id))
        )
        val viewModelFactory = ChannelListViewModelFactory(
                filters,
                ChannelListViewModel.DEFAULT_SORT
        )
        val listViewModel: ChannelListViewModel by viewModels { viewModelFactory }
        val listHeaderViewModel: ChannelListHeaderViewModel by viewModels()

        listHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)
        listViewModel.bindView(binding.channelsView, viewLifecycleOwner)
    }


    private fun setUpDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.logout) {
                logout()
            }
            false
        }
        val currentUser = client.getCurrentUser()!!
        val headerView = binding.navigationView.getHeaderView(0)
        val avatar = headerView.findViewById<AvatarView>(R.id.avatar_view)
        avatar.setUserData(currentUser)

        val headerName = headerView.findViewById<TextView>(R.id.user_name)
        headerName.text = currentUser.name
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            client.disconnect()
            findNavController().navigate(R.id.action_homeFrag_to_logInFrag)
            Toast.makeText(context, "Successfully Logged Out", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
        }

        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Logout?")
        builder.setMessage("Are you sure you want to logout?")
        builder.create().show()
    }

    private fun setUpUser() {
        if (client.getCurrentUser() == null) {
            val user = User(
                    id = args.chatUser.uid,
                    extraData = mutableMapOf(
                            "name" to args.chatUser.name,
                            "email" to args.chatUser.email
                    )
            )
            val token = client.devToken(user.id)
            client.connectUser(user, token).enqueue { result ->
                if (result.isSuccess) {
                    Log.d(TAG, "User Connected Successfully")
                } else {
                    Log.d(TAG, result.error().message.toString())
                }
            }
        }
    }

    private fun deleteChannel(channel: Channel) {
        ChatDomain.instance().useCases.deleteChannel(channel.cid).enqueue { result ->
            if(result.isSuccess) {
                Toast.makeText(context, "${channel.name} removed!", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("HomeFrag", result.error().message.toString())
            }
        }
    }
}