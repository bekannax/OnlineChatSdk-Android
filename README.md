# OnlineChatSdk-Android ![API](https://img.shields.io/badge/API-17%2B-brightgreen.svg?style=flat) [![Download](https://api.bintray.com/packages/sofit/onlinechatsdk/onlinechatsdk/images/download.svg) ](https://bintray.com/sofit/onlinechatsdk/onlinechatsdk/_latestVersion)

## Добавление в проект
```groovy
buildscript {
    repositories {
        maven {
            url 'https://dl.bintray.com/sofit/onlinechatsdk/'
        }
    }
    dependencies {
        implementation 'com.github.bekannax:onlinechatsdk:0.0.1'
    }
}
```
## Получение id
Перейдите в раздел «Настройки - Установка» и скопируйте значение переменной id.
![](https://github.com/bekannax/OnlineChatSdk-Android/blob/master/images/2019-03-21_16-53-28.png)

## Пример использования
```xml
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent">
    
    <com.sofit.onlinechatsdk.ChatView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/chatview"
        app:id="<Ваш id>"
        app:domain="<Домен вашего сайта>" />
</android.support.constraint.ConstraintLayout>
 ```
 
```java
public class MyActivity extends ChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        ChatView chatView = getChatView();
        if (chatView != null) {
            //
        }
    }
}
```

## События
 * **operatorSendMessage** - оператор отправил сообщение посетителю.
 * **clientSendMessage** - посетитель отправил сообщение оператору.
 * **clientMakeSubscribe** - посетитель заполнил форму.
 * **contactsUpdated** - посетитель обновил информацию о себе.
 * **sendRate** - посетитель отправил новый отзыв.
 
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
        }
    }
});
```

 ```java
chatView.setOperatorSendMessageListener(new ChatListener() {
    @Override
    public void onEvent(String name, String data) {

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

```java
chatView.callOpenTab(1);
```
