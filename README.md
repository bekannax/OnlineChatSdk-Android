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
    }
}
```

## События
 * **operatorSendMessage** - оператор отправил сообщение посетителю
 * **clientSendMessage**
 * **clientMakeSubscribe**
 * **contactsUpdated**
 * **sendRate**

## Методы
 * **setClientInfo**
 * **setTarget**
 * **openSupport**
 * **openReviewsTab**
 * **openTab**
 * **sendMessage**
 * **receiveMessage**
 * **setOperator**
