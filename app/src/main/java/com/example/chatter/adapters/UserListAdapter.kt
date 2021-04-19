package com.example.chatter.adapters

import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatter.databinding.UserListItemBinding
import com.example.chatter.view.chat.UserListFragDirections
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private val client = ChatClient.instance()
    private var userList = emptyList<User>()

    inner class  UserViewHolder(val binding: UserListItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            UserListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.binding.apply {
            avatarImageView.setUserData(currentUser)
            usernameTextView.text = currentUser.name
            holder.binding.lastActiveTextView.text = convertDate(currentUser.lastActive!!.time)
            root.setOnClickListener {
                createNewChannel(currentUser.id, holder)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setData(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }

    private fun convertDate(mileSec: Long): String =
            DateFormat.format("dd/MM/yyyy hh:mm a", mileSec).toString()


    private fun createNewChannel(id: String, holder: UserListAdapter.UserViewHolder) {
        client.createChannel(
            channelType =  "messaging",
            members = listOf(client.getCurrentUser()!!.id, id)
        ).enqueue { result->
            if(result.isSuccess) {
                navigateToChatFragment(holder, result.data().cid)
            } else {
                Log.e("UserListAdapter", result.error().message.toString())
            }
        }
    }

    private fun navigateToChatFragment(holder: UserListAdapter.UserViewHolder, cid: String) {
        val action = UserListFragDirections.actionUserListFragToChatFragment(cid)
        holder.itemView.findNavController().navigate(action)
    }

}