package com.sofit.onlinechatsdk

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

public abstract class ChatAppCompatActivity : AppCompatActivity(), ChatViewHelper {

    private var chatView: ChatView? = null

    private val mGetContent: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        getChatView()?.onReceiveValue(uri)
        getChatView()?.onRequestPermissionsGranted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val keyboardHeight = imeInsets.bottom
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(  WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val bottom = if (isKeyboardVisible) {
                keyboardHeight
            } else {
                systemBars.bottom
            }
//            Log.d(ChatView.logTag, "--------------- onCreate ---------- setOnApplyWindowInsetsListener --- left=${systemBars.left} : right=${systemBars.right}")
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

    override fun onLinkPressed(link: String?) {
        chatView?.openLink(link)
    }

    override fun onCloseSupport() {
        Log.d(ChatView.logTag, "--------------- onCloseSupport ---------- finish")
        finish()
    }

    override fun onDestroyChatView() {
        chatView?.destroy()
        chatView = null
    }

    override fun setChatView(chatView: ChatView?) {
        Log.d(ChatView.logTag, "--------------- setChatView ---------- 1")
        this.chatView = chatView
        this.chatView?.setOnShowFileChooser { _, _, _ ->
            mGetContent.launch("*/*")
        }
    }

    override fun getChatView(): ChatView? {
        return chatView
    }

    override fun setChatViewById(id: Int) {
        Log.d(ChatView.logTag, "--------------- setChatViewById ---------- 1")
        findViewById<ChatView>(id)?.let {
            Log.d(ChatView.logTag, "--------------- setChatViewById ---------- 2")
            setChatView(it)
        }
    }
}