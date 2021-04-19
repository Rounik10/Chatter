package com.example.chatter.view.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.chatter.databinding.FragmentChatBinding
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory

class ChatFragment : Fragment() {

    private val args: ChatFragmentArgs by navArgs()
    private var _binging: FragmentChatBinding? = null
    private val binding get() = _binging!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binging = FragmentChatBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.messagesHeaderView.setBackButtonClickListener {
            requireActivity().onBackPressed()
        }

        setUpMessages()

        return binding.root
    }

    private fun setUpMessages() {
        val factory = MessageListViewModelFactory(args.channelId)

        val messageListHeaderViewBinding: MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels {factory}

        binding.apply {
            messageListHeaderViewBinding.bindView(messagesHeaderView, viewLifecycleOwner)
            messageListViewModel.bindView(messageList, viewLifecycleOwner)
            messageInputViewModel.bindView(messageInputView, viewLifecycleOwner)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binging = null
    }

}