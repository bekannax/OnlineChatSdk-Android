# OnlineChatSdk-Android ![API](https://img.shields.io/badge/API-17%2B-brightgreen.svg?style=flat)
![](https://github.com/bekannax/OnlineChatSdk-Android/blob/master/images/2020-08-14%2017.52.26.jpg?raw=true)
## Добавление в проект
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        implementation 'com.github.bekannax:OnlineChatSdk:0.2.8'
    }
}
```
## Получение id
Перейдите в раздел «Online чат - Ваш сайт - Настройки - Установка» и скопируйте значение переменной id.
![](https://github.com/bekannax/OnlineChatSdk-Android/blob/master/images/2019-03-21_16-53-28.png?raw=true)

## Пример использования
Создайте `layout` с нашим `ChatView`. Создайте `Activity`, которая наследует `ChatActivity`.
```xml
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">

    <com.sofit.onlinechatsdk.ChatView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/chatview"
        app:id="<Ваш id>"
        app:domain="<Домен вашего сайта>"
        app:showCloseButton="true"
        app:autoLoad="true" />
</android.support.constraint.ConstraintLayout>
 ```
Если `autoLoad = true`, то при запуске `Activity` виджет автоматически загрузится, если `autoLoad = false`, то нужно использовать функцию **load()**.
```java
public class MyActivity extends ChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        ChatView chatView = getChatView();
        if (chatView != null) {
            chatView.load();
        }
    }
}
```
Так же перед загрузкой можно указать **language** и **clientId**.
```java
chatView.setLanguage("en").setClientId("newId").load();
```

## События
 * **operatorSendMessage** - оператор отправил сообщение посетителю.
 * **clientSendMessage** - посетитель отправил сообщение оператору.
 * **clientMakeSubscribe** - посетитель заполнил форму.
 * **contactsUpdated** - посетитель обновил информацию о себе.
 * **sendRate** - посетитель отправил новый отзыв.
 * **clientId** - уникальный идентификатор посетителя.

Для каждого события есть персональный `Listener`.

```java
chatView.setOnOperatorSendMessageListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

    }
});

chatView.setOnClientSendMessageListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

    }
});

chatView.setOnClientMakeSubscribeListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

    }
});

chatView.setOnContactsUpdatedListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

    }
});

chatView.setOnSendRateListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

    }
});

chatView.setOnClientIdListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

    }
});
```

Или можно задать один `Listener` на все события.

```java
chatView.setListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {
        switch (name) {
            case ChatView.event_operatorSendMessage:
                break;
            case ChatView.event_clientSendMessage:
                break;
            case ChatView.event_clientMakeSubscribe:
                break;
            case ChatView.event_contactsUpdated:
                break;
            case ChatView.event_sendRate:
                break;
            case ChatView.event_clientId:
                break;
        }
    }
});
```

## Методы
 * **setClientInfo** - изменение информации о посетителе.
 * **setTarget** - пометить посетителя целевым.
 * **openReviewsTab** - отобразить форму для отзыва.
 * **openTab** - отобразить необходимую вкладку.
 * **sendMessage** - отправка сообщения от имени клиента.
 * **receiveMessage** - отправка сообщения от имени оператора.
 * **setOperator** - выбор любого оператора.
 * **getContacts** - получение контактных данных.

```java
chatView.callJsSetClientInfo("{name: \"Имя\", email: \"test@mail.ru\"}");

chatView.callJsSetTarget("reason");

chatView.callJsOpenReviewsTab();

chatView.callJsOpenTab(1);

chatView.callJsSendMessage("Здравствуйте! У меня серьёзная проблема!");

chatView.callJsReceiveMessage("Мы уже спешим на помощь ;)", null, 2000);

chatView.callJsSetOperator("Логин оператора");

chatView.callJsGetContacts(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

    }
});
```
Подробное описание методов можно прочесть в разделе «Интеграция и API - Javascript API».

## Получение token
Перейдите в раздел «Интеграция и API - REST API», скопируйте существующий token или добавьте новый.
![](https://github.com/bekannax/OnlineChatSdk-Android/blob/master/images/2019-04-01_18-32-22.png?raw=true)

## Получение новых сообщений от оператора
Для получения новых сообщений, в `ChatView` есть два статичных метода **getUnreadedMessages** и **getNewMessages**.

**getUnreadedMessages** - возвращает все непрочитанные сообщения.

**getNewMessages** работает аналогичным образом, но при повторном запросе предыдущие сообщения уже не возвращаются.
Перед использование методов, нужно указать `apiToken`.


Это можно сделать в коде
```java
chatView.setApiToken("<apiToken>");
```
или в `layout`

![](https://github.com/bekannax/OnlineChatSdk-Android/blob/master/images/2020-08-14_23-52-04.png?raw=true)

```java
JSONObject data = ChatView.getUnreadedMessages(Context context);
JSONObject data = ChatView.getNewMessages(Context context);
```
Формат `data` аналогичен ответу метода /chat/message/getList в Rest Api.

Подробное описание можно прочесть в разделе «Интеграция и API - REST API - Инструкции по подключению».

![](https://github.com/bekannax/OnlineChatSdk-Android/blob/master/images/2020-08-14_19-05-48.png?raw=true)