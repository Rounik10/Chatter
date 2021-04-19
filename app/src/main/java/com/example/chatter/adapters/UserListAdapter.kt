package com.example.chatter.adapters

import android.graphics.Color
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
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
    private var isMultipleSelected = MutableLiveData(false)
    private var selectedList = arrayListOf(client.getCurrentUser()!!.id)

    inner class UserViewHolder(val binding: UserListItemBinding)
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

            root.setOnLongClickListener {
                isMultipleSelected.value = true
                selectedList.add(currentUser.id)
                it.setBackgroundColor(Color.rgb(187, 134, 252))
                true
            }

            root.setOnClickListener {
                if (!isMultipleSelected.value!!) {
                    createNewChannel(listOf(currentUser.id, client.getCurrentUser()!!.id), holder.itemView)
                } else {
                    if (selectedList.contains(currentUser.id)) {
                        selectedList.remove(currentUser.id)
                        it.setBackgroundColor(Color.rgb(255, 255, 255))
                    } else {
                        selectedList.add(currentUser.id)
                        it.setBackgroundColor(Color.rgb(187, 134, 252))
                    }
                }
                if (selectedList.isEmpty()) {
                    isMultipleSelected.value = false
                }
            }
        }
    }

    fun createGroup(itemView: View): Boolean {
        return if (selectedList.size < 2) false
        else {
            createNewChannel(selectedList, itemView)
            true
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

    private fun createNewChannel(list: List<String>, itemView: View) {
        client.createChannel(
                channelType = "messaging",
                members = list
        ).enqueue { result ->
            if (result.isSuccess) {
                navigateToChatFragment(itemView, result.data().cid)
            } else {
                Log.e("UserListAdapter", result.error().message.toString())
            }
        }
    }

    private fun navigateToChatFragment(itemView: View, cid: String) {
        val action = UserListFragDirections.actionUserListFragToChatFragment(cid)
        itemView.findNavController().navigate(action)
    }

}