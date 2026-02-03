package com.sofit.onlinechatsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sofit.onlinechatsdk.chatapi.ChatApi;
import com.sofit.onlinechatsdk.chatapi.ChatApiOld;
import com.sofit.onlinechatsdk.chatapi.ChatApiOldMessagesWrapper;
import com.sofit.onlinechatsdk.chatapi.MyJsonArray;
import com.sofit.onlinechatsdk.chatapi.MyJsonObject;
import com.sofit.onlinechatsdk.chatapi.models.ChatApiGetClientUnreadMessageCountResponseModel;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Deprecated;

@SuppressLint("SetJavaScriptEnabled")
public class ChatView extends WebView implements ChatListener {

    public static final String event_operatorSendMessage = "operatorSendMessage";
    public static final String event_clientSendMessage = "clientSendMessage";
    public static final String event_clientMakeSubscribe = "clientMakeSubscribe";
    public static final String event_contactsUpdated = "contactsUpdated";
    public static final String event_sendRate = "sendRate";
    public static final String event_clientId = "clientId";
    public static final String event_linkPressed = "linkPressed";
    public static final String event_closeSupport = "closeSupport";

    public static final String method_setClientInfo = "setClientInfo";
    public static final String method_setTarget = "setTarget";
    public static final String method_openReviewsTab = "openReviewsTab";
    public static final String method_openTab = "openTab";
    public static final String method_sendMessage = "sendMessage";
    public static final String method_receiveMessage = "receiveMessage";
    public static final String method_setOperator = "setOperator";
    public static final String method_getContacts = "getContacts";
    private static final String method_destroy = "destroy";
    private static final String method_pageLoaded = "pageLoaded";

    public static final String logTag = "onlinechat.sdk";

//    final String loadUrl = "https://admin.verbox.ru/support/chat/%s/%s%s";

    private final String injectCssTemplate = "(function() {" +
        "var parent = document.getElementsByTagName('head').item(0);" +
        "var style = document.createElement('style');" +
        "style.type = 'text/css';" +
        "style.innerHTML = '%s';" +
        "parent.appendChild(style);" +
    "})()";
    private String id;
    private String domain;
    private String language;
    private String clientId;

    private String apiToken;
    private String css;
    private Boolean showCloseButton;

    private ChatListener listener;
    private ChatListener operatorSendMessageListener;
    private ChatListener clientSendMessageListener;
    private ChatListener clientMakeSubscribeListener;
    private ChatListener contactsUpdatedListener;
    private ChatListener sendRateListener;
    private ChatListener clientIdListener;
    private ChatListener linkPressedListener;
//    private ChatListener chatWasOpenListener;
//    private ChatListener chatWasClosedListener;
    private ChatListener getContactsCallback;

    private ChatFileChooser chatFileChooser;

    private Context context;
    private ChatChromeClient chatChromeClient;
    private List<String> callJs;
    private boolean isLoaderShowCancelButton = true;
    private boolean finished = false;
    private boolean destroyed = false;

//    private ProgressDialog progressDialog;

    private LoadingDialog loadingDialog;

    private final CheckConnection checkConnection = new CheckConnection();

    public ChatView(Context context) {
        this(context, null);
        init(context);
    }

    public ChatView(Context context, boolean isLoaderShowCancelButton) {
        this(context, null);
        setShowLoaderCancelButton(isLoaderShowCancelButton);
        init(context);
    }

    public static ChatApiGetClientUnreadMessageCountResponseModel getUnreadMessagesCount(Context context) {
        return (new ChatApi(context)).getClientUnreadMessageCount();
    }

    @Deprecated(message = "")
    private static JSONObject getUnreadedMessages(String startDate, String clientId, String token, Context context) {
        if (token.isEmpty()) {
            return MyJsonObject.create("{\"success\":false,\"error\":{\"code\":0,\"descr\":\"Не задан token\"}}");
        }
        if (clientId.isEmpty()) {
            return MyJsonObject.create("{\"success\":false,\"error\":{\"code\":0,\"descr\":\"Не задан clientId\"}}");
        }

        MyJsonObject dateRange = MyJsonObject.create();
        dateRange.Put("start", startDate);
        dateRange.Put("stop", (new ChatSimpleDateFormat()).getCurrent());

        MyJsonObject params = MyJsonObject.create();
        params.Put("client", MyJsonObject.create().Put("clientId", clientId));
        params.Put("sender", "operator");
        params.Put("status", "unreaded");
        params.Put("dateRange", dateRange);

        ChatApiOldMessagesWrapper resultWrapper = new ChatApiOldMessagesWrapper( (MyJsonObject) new ChatApiOld().message(token, params) );
        if (resultWrapper.getMessages().length() == 0) {
            return resultWrapper.getResult();
        }

        MyJsonArray unreadedMessages = MyJsonArray.create();
        for (int i = 0; i < resultWrapper.getMessages().length(); i++) {
            MyJsonObject message = resultWrapper.getMessages().GetJsonObject(i);
            if (!message.GetBoolean("isVisibleForClient", true)) {
                continue;
            }
            unreadedMessages.Put(message);
        }
        if (unreadedMessages.length() == 0) {
            return MyJsonObject.create("{\"success\":true,\"data\":[]}");
        }
        resultWrapper.setMessages(unreadedMessages);
        return resultWrapper.getResult();
    }

//    @Deprecated(message = "")
//    public static JSONObject getUnreadedMessages(Context context) {
//        return getUnreadedMessages( context, ChatConfig.getApiToken(context) );
//    }

    @Deprecated(message = "")
    public static JSONObject getUnreadedMessages(Context context, String token) {
        return getUnreadedMessages( ChatConfig.getClientId(context), token, context);
    }

    @Deprecated(message = "")
    public static JSONObject getUnreadedMessages(String clientId, String token, Context context) {
        return getUnreadedMessages( (new ChatSimpleDateFormat()).format(new Date(System.currentTimeMillis() - 86400000 * 14)), clientId, token, context);
    }

    @Deprecated(message = "")
    public static JSONObject getNewMessages(String clientId, String token, Context context) {
        String startDate = ChatConfig.getLastDateTimeNewMessage(context);
        ChatApiOldMessagesWrapper resultWrapper;
        if (startDate.isEmpty()) {
            resultWrapper = new ChatApiOldMessagesWrapper( (MyJsonObject) getUnreadedMessages(clientId, token, context) );
        } else {
            resultWrapper = new ChatApiOldMessagesWrapper( (MyJsonObject) getUnreadedMessages(startDate, clientId, token, context) );
        }
        if (resultWrapper.getMessages().length() == 0) {
            ChatConfig.setLastDateTimeNewMessage( (new ChatSimpleDateFormat()).getCurrent() , context);
            return resultWrapper.getResult();
        }
        MyJsonObject message = resultWrapper.getMessages().GetJsonObject( resultWrapper.getMessages().length() - 1 );
        DateFormat formatter = new ChatSimpleDateFormat();
        try {
            Date date = formatter.parse(message.GetString("dateTime"));
            Date newDate = new Date();
            newDate.setTime(date.getTime() + 1000);
            ChatConfig.setLastDateTimeNewMessage(formatter.format(newDate), context);
        } catch (Exception e) {/**/}
        return resultWrapper.getResult();
    }

//    @Deprecated(message = "")
//    public static JSONObject getNewMessages(Context context) {
//        return getNewMessages(context, ChatConfig.getApiToken(context));
//    }

    @Deprecated(message = "")
    public static JSONObject getNewMessages(Context context, String token) {
        return getNewMessages(ChatConfig.getClientId(context), token, context);
    }

    @Deprecated(message = "")
    public static JSONObject setInfoCustomDataValue(Context context, String token, String key, String value) {
        return ChatApiOld.setInfo(
            token,
            MyJsonObject.create().Put(
            "client", MyJsonObject.create()
                .Put("id", ChatConfig.getClientId(context))
                .Put("customData", MyJsonObject.create().Put(key, value))
            )
        );

    }

    public ChatView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
        init(context);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChatView);
        setShowLoaderCancelButton(a.getBoolean(R.styleable.ChatView_showLoaderCloseButton, true));
        init(context);
        setId(a.getString(R.styleable.ChatView_id));
        this.domain = a.getString(R.styleable.ChatView_domain);
        this.language = a.getString(R.styleable.ChatView_language);
        this.showCloseButton = a.getBoolean(R.styleable.ChatView_showCloseButton, true);
        if (a.getBoolean(R.styleable.ChatView_autoLoad, false)) {
            this.load();
        }
        a.recycle();
    }

    private void init(Context context) {
        this.context = context;
//        showLoading(context, isLoaderShowCancelButton);
        setChatView(context);
    }

    private void setChatView(Context context) {
        ChatAppCompatActivity activity = getChatActivityFromContext(context);
        if (activity != null) {
            activity.setChatView(this);
        } else {
            ChatActivity activityLegacy = getChatActivityLegacyFromContext(context);
            if (activityLegacy != null) {
                activityLegacy.setChatView(this);
            }
        }
    }

    private void linkPressed(String data) {
        ChatAppCompatActivity activity = getChatActivityFromContext(context);
        if (activity != null) {
            activity.onLinkPressed( MyJsonObject.create(data).GetString("link") );
        } else {
            ChatActivity activityLegacy = getChatActivityLegacyFromContext(context);
            if (activityLegacy != null) {
                activityLegacy.onLinkPressed( MyJsonObject.create(data).GetString("link") );
            } else {
                this.openLink( MyJsonObject.create(data).GetString("link") );
            }
        }
    }

    public void showLoading(Context context) {
        showLoading(context, true);
    }

    public void showLoading(Context context, boolean isShowCancelButton) {
        if (this.loadingDialog != null) {
            return;
        }
        loadingDialog = new LoadingDialog(context, isShowCancelButton);
        loadingDialog.setOnCancelListener(this::closeSupport);
        loadingDialog.show();
    }

    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public void closeSupport() {
        ChatAppCompatActivity activity = getChatActivityFromContext(context);
        if (activity != null) {
            activity.onCloseSupport();
        } else {
            ChatActivity activityLegacy = getChatActivityLegacyFromContext(context);
            if (activityLegacy != null) {
                activityLegacy.onCloseSupport();
            }
        }
//        checkConnection.close();
    }

    private String getCallJsMethod(String methodName, Object... params) {
        StringBuilder builder = new StringBuilder();
        builder.append("window.MeTalk('").append(methodName).append("'");
        if (params != null && params.length > 0) {
            for (Object p: params) {
                builder.append(",");
                if (p == null) {
                    builder.append("null");
                } else if (p instanceof Integer) {
                    builder.append(Integer.valueOf(p.toString()));
                } else if (p instanceof Long) {
                    builder.append(Long.valueOf(p.toString()));
                } else if (p instanceof Command) {
                    builder.append(((Command)p).command);
                } else {
                    builder.append("'").append(p).append("'");
                }
            }
        }
        builder.append(");");
        return builder.toString();
    }

    private String getSetup(String language, String clientId, Boolean showCloseButton) {
        StringBuilder setup = new StringBuilder();
        if (language != null && !language.isEmpty()) {
            setup.append("?setup={\"language\":\"").append(language).append("\"");
        }
        if (clientId != null && !clientId.isEmpty()) {
            if (setup.length() > 0) {
                setup.append(",");
            } else {
                setup.append("?setup={");
            }
            setup.append("\"clientId\"").append(":").append("\"").append(clientId).append("\"");
        }
        if (setup.length() > 0) {
            setup.append("}");
        }
        if (setup.length() == 0) {
            setup.append("?");
        } else {
            setup.append("&");
        }
        if (showCloseButton) {
            setup.append("sdk-show-close-button=1");
        }
        return setup.toString();
    }

    private void load(
        String id,
        String domain,
        String language,
        String clientId,
        Boolean showCloseButton,
        Boolean showLoaderCloseButton
    ) {
        if (id == null || domain == null || id.isEmpty() || domain.isEmpty()) {
            return;
        }
        showLoading(context, showLoaderCloseButton);
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        this.addJavascriptInterface(new ChatInterface(this), "ChatInterface");
        this.setWebViewClient(new ChatWebViewClient(this.context, this));
        Activity activity = getActivityFromContext(this.context);
        if (activity != null) {
            if (this.chatFileChooser == null) {
                this.chatChromeClient = new ChatChromeClient(activity);
            } else {
                this.chatChromeClient = new ChatChromeClient(activity, this.chatFileChooser);
            }
            this.setWebChromeClient(this.chatChromeClient);
        } else {
            this.chatChromeClient = null;
        }
//        this.loadUrl("file:///android_asset/chat.html");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {


            String sLoadUrl = String.format("https://%s/support/chat/%s/%s%s", checkConnection.getDomain(), id, domain, this.getSetup(language, clientId, showCloseButton));
            Log.d(ChatView.logTag, " loadUrl : " + sLoadUrl);

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                this.loadUrl( sLoadUrl );
            });
        });
    }

    private ChatAppCompatActivity getChatActivityFromContext(Context context) {
        try {
            while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            if (context instanceof ChatAppCompatActivity) {
                return (ChatAppCompatActivity) context;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private ChatActivity getChatActivityLegacyFromContext(Context context) {
        try {
            while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            if (context instanceof ChatActivity) {
                return (ChatActivity) context;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Activity getActivityFromContext(Context context) {
        try {
            while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            if (context instanceof Activity) {
                return (Activity) context;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void load() {
        this.load(this.id, this.domain, this.language, this.clientId, this.showCloseButton, this.isLoaderShowCancelButton);
    }

    public void load(String css) {
        this.setCss(css);
        this.load(this.id, this.domain, this.language, this.clientId, this.showCloseButton, this.isLoaderShowCancelButton);
    }

    public ChatView setId(String id) {
        this.id = id;
        ChatConfig.setOrgId(id, this.context);
        return this;
    }

    public ChatView setShowLoaderCancelButton(boolean value) {
        this.isLoaderShowCancelButton = value;
        return this;
    }

    public void onRequestPermissionsGranted() {
        chatChromeClient.onRequestPermissionsGranted();
    }

    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        chatChromeClient.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public ChatView setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public ChatView setLanguage(String language) {
        this.language = language;
        return this;
    }

    public ChatView setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ChatView setCss(String style) {
        this.css = style;
        return this;
    }

    public ChatView setShowCloseButton(boolean showCloseButton) {
        this.showCloseButton = showCloseButton;
        return this;
    }

    @Deprecated(message = "")
    public ChatView setApiToken(String apiToken) {
        this.apiToken = apiToken;
//        ChatConfig.setApiToken(this.apiToken, getContext());
        return this;
    }

    public String getID() {
        return this.id;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getClientId() {
        return this.clientId;
    }

    @Deprecated(message = "")
    public String getApiToken() {
//        if (this.apiToken == null || this.apiToken.isEmpty()) {
//            this.apiToken = ChatConfig.getApiToken(getContext());
//        }
        return this.apiToken;
    }

    public void callJs(final String script) {
        this.post(() -> loadUrl(String.format("javascript:%s", script)));
    }

    public void injectCss(final String style) {
        if (style.isEmpty()) {
            return;
        }
        String script = String.format(injectCssTemplate, style);
        if (isFinished()) {
            this.callJs( script );
        } else {
            if (this.callJs == null) {
                this.callJs = new ArrayList<>();
            }
            this.callJs.add( script );
        }
    }

    void injectCss() {
        if (css != null && !css.isEmpty()) {
            this.callJs( String.format(injectCssTemplate, css) );
        }
    }

    public void callJs() {
        if (this.callJs == null || this.callJs.isEmpty()) {
            return;
        }
        for (String script: this.callJs) {
            callJs(script);
        }
        this.callJs.clear();
        this.callJs = null;
    }

    public void callJsMethod(String methodName, Object... params) {
        if (isFinished()) {
            this.callJs(this.getCallJsMethod(methodName, params));
        } else {
            if (this.callJs == null) {
                this.callJs = new ArrayList<>();
            }
            this.callJs.add(this.getCallJsMethod(methodName, params));
        }
    }

    public void callJsSetClientInfo(String jsonInfo) {
        this.callJsMethod(method_setClientInfo, new Command(jsonInfo));
    }

    public void callJsSetTarget(String reason) {
        this.callJsMethod(method_setTarget, reason);
    }

    public void callJsOpenReviewsTab() {
        this.callJsMethod(method_openReviewsTab);
    }

    public void callJsOpenTab(int index) {
        this.callJsMethod(method_openTab, index);
    }

    public void callJsSendMessage(String text) {
        this.callJsMethod(method_sendMessage, text);
    }

    public void callJsReceiveMessage(String text, String operator, Integer simulateTyping) {
        this.callJsMethod(method_receiveMessage, text, operator, simulateTyping);
    }

    public void callJsSetOperator(String login) {
        this.callJsMethod(method_setOperator, login);
    }

    public void callJsGetContacts(ChatListener callback) {
        this.getContactsCallback = callback;
        this.callJsMethod(method_getContacts, new Command("window.getContactsCallback"));
    }

    public void callJsDestroy() {
        this.callJsMethod(method_destroy);
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setListener(ChatListener listener) {
        this.listener = listener;
    }

//    public void setOnChatWasOpenListener(ChatListener listener) {
//        this.chatWasOpenListener = listener;
//    }
//
//    public void setOnChatWasClosedListener(ChatListener listener) {
//        this.chatWasClosedListener = listener;
//    }

    public void setOnOperatorSendMessageListener(ChatListener listener) {
        this.operatorSendMessageListener = listener;
    }

    public void setOnClientSendMessageListener(ChatListener listener) {
        this.clientSendMessageListener = listener;
    }

    public void setOnClientMakeSubscribeListener(ChatListener listener) {
        this.clientMakeSubscribeListener = listener;
    }

    public void setOnContactsUpdatedListener(ChatListener listener) {
        this.contactsUpdatedListener = listener;
    }

    public void setOnSendRateListener(ChatListener listener) {
        this.sendRateListener = listener;
    }

    public void setOnClientIdListener(ChatListener listener) {
        this.clientIdListener = listener;
    }

    public void setOnLinkPressedListener(ChatListener listener) {
        this.linkPressedListener = listener;
    }

    public void onReceiveValue(Uri uri) {
        if (Build.VERSION.SDK_INT >= 26) {
            if (getWebChromeClient() != null) {
                if (getWebChromeClient() instanceof ChatChromeClient) {
                    ((ChatChromeClient) getWebChromeClient()).onReceiveValue(uri);
                }
            }
        } else {
            if (this.chatChromeClient != null) {
                this.chatChromeClient.onReceiveValue(uri);
            }
        }
    }

    public void onShowFileChooser() {
        if (Build.VERSION.SDK_INT >= 26) {
            if (getWebChromeClient() != null) {
                if (getWebChromeClient() instanceof ChatChromeClient) {
                    ((ChatChromeClient) getWebChromeClient()).onShowFileChooser();
                }
            }
        } else {
            if (this.chatChromeClient != null) {
                this.chatChromeClient.onShowFileChooser();
            }
        }
    }

    public void destroy() {
        super.destroy();
        stopLoading();
        callJsDestroy();
        destroyed = true;
//        onChatWasClosed();
    }

    public void openLink(String link) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivityFromContext(context).startActivity(browserIntent);
        } catch (Exception e) {
            Log.e(ChatView.logTag, e.toString());
        }
    }

    @Override
    public void onEvent(String name, String data) {
        if (destroyed) {
            return;
        }
        Log.d(ChatView.logTag, "onEvent :: name : " + name + " :: data : " + data);
        if (!isFinished()) {
            setFinished(true);
            callJs();
        }
        if (this.listener != null) {
            this.listener.onEvent(name, data);
        }
        switch (name) {
            case event_operatorSendMessage:
                if (this.operatorSendMessageListener != null) {
                    this.operatorSendMessageListener.onEvent(name, data);
                }
                break;
            case event_clientSendMessage:
                if (this.clientSendMessageListener != null) {
                    this.clientSendMessageListener.onEvent(name, data);
                }
                break;
            case event_clientMakeSubscribe:
                if (this.clientMakeSubscribeListener != null) {
                    this.clientMakeSubscribeListener.onEvent(name, data);
                }
                break;
            case event_contactsUpdated:
                if (this.contactsUpdatedListener != null) {
                    this.contactsUpdatedListener.onEvent(name, data);
                }
                break;
            case event_sendRate:
                if (this.sendRateListener != null) {
                    this.sendRateListener.onEvent(name, data);
                }
                break;
            case event_clientId:
                if (this.clientIdListener != null) {
                    this.clientIdListener.onEvent(name, data);
                }
                String clientId = MyJsonObject.create(data).GetString("clientId");
                if (!clientId.isEmpty()) {
                    ChatConfig.setClientId(clientId, getContext());
                }
                String siteId = MyJsonObject.create(data).GetString("siteId");
                if (!siteId.isEmpty()) {
                    ChatConfig.setSiteId(siteId, getContext());
                }
                break;
            case method_getContacts:
                if (this.getContactsCallback != null) {
                    this.getContactsCallback.onEvent(name, data);
                    this.getContactsCallback = null;
                }
                break;
            case event_linkPressed:
                linkPressed(data);
                break;
            case event_closeSupport:
                closeSupport();
                break;
        }
    }

    public void setOnShowFileChooser(ChatFileChooser chatFileChooser) {
        this.chatFileChooser = chatFileChooser;
        if (this.chatChromeClient != null) {
            this.chatChromeClient.setOnShowFileChooser(chatFileChooser);
        }
    }

//    public void onChatWasOpen() {
//        if (this.chatWasOpenListener != null) {
//            this.chatWasOpenListener.onEvent("", "");
//        }
//    }

//    public void onChatWasClosed() {
//        if (this.chatWasClosedListener != null) {
//            this.chatWasClosedListener.onEvent("", "");
//        }
//    }
}