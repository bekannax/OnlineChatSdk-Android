package com.sofit.onlinechatsdk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

public class LoadingDialog extends Dialog {

//    private String message;
    private OnCancelListener onCancelListener;
    private Button cancelButton;
//    private TextView messageTextView;

    public interface OnCancelListener {
        void onCancel();
    }

    public LoadingDialog(Context context) {
        super(context);
//        this.message = context.getString(R.string.dialog_loading_title) + "...";
    }

//    public LoadingDialog(Context context, String message) {
//        super(context);
//        this.message = message;
//    }

//    public LoadingDialog(Context context, int themeResId) {
//        super(context, themeResId);
////        this.message = "Загрузка...";
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);

        // Запрещаем закрытие диалога при клике вне его
        setCancelable(false);
        setCanceledOnTouchOutside(false);

//        messageTextView = findViewById(R.id.tv_message);
        cancelButton = findViewById(R.id.btn_cancel);

//        messageTextView.setText(message);

        cancelButton.setOnClickListener(v -> {
            if (onCancelListener != null) {
                onCancelListener.onCancel();
            }
            dismiss();
        });
    }

//    public void setMessage(String message) {
//        this.message = message;
//        if (messageTextView != null) {
//            messageTextView.setText(message);
//        }
//    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.onCancelListener = listener;
    }

//    public void showCancelButton(boolean show) {
//        if (cancelButton != null) {
//            cancelButton.setVisibility(show ? View.VISIBLE : View.GONE);
//        }
//    }
}