package com.sofit.onlinechatsdk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

abstract class ChatActivity : Activity(), ChatViewHelper {

    private var chatView: ChatView? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                chatView?.let {
                    it.onReceiveValue(uri)
                    return
                }
            }
        }
        chatView?.onReceiveValue(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val keyboardHeight = imeInsets.bottom
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(  WindowInsetsCompat.Type.systemBars())
            val bottom = if (isKeyboardVisible) {
                keyboardHeight
            } else {
                systemBars.bottom
            }
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        onChatWasOpen()
    }

    override fun onStop() {
        super.onStop()
        onChatWasClosed()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyChatView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        chatView?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLinkPressed(link: String?) {
        chatView?.openLink(link)
    }

    override fun onCloseSupport() {
        finish()
    }

    override fun setChatView(chatView: ChatView?) {
        this.chatView = chatView
    }

    override fun getChatView(): ChatView? {
        return this.chatView
    }

    override fun setChatViewById(id: Int) {
        val findChatView = findViewById<ChatView>(id)
        findChatView?.let {
            setChatView(it)
        }
    }

    override fun onDestroyChatView() {
        chatView?.destroy()
        chatView = null
    }
}