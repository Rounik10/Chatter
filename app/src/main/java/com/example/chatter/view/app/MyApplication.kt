package com.example.chatter.view.app

import android.app.Application
import com.example.chatter.R
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.livedata.ChatDomain

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val client =
            ChatClient.Builder(getString(R.string.api_key), this).logLevel(ChatLogLevel.ALL).build()
        ChatDomain.Builder(client, this).build()
    }

}