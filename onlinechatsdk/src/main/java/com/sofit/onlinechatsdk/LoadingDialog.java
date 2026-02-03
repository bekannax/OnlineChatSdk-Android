package com.sofit.onlinechatsdk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class LoadingDialog extends Dialog {

    private final boolean isShowCancelButton;
    private OnCancelListener onCancelListener;

    public interface OnCancelListener {
        void onCancel();
    }

    public LoadingDialog(Context context) {
        super(context);
        this.isShowCancelButton = true;
    }

    public LoadingDialog(Context context, boolean isShowCancelButton) {
        super(context);
        this.isShowCancelButton = isShowCancelButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);

        // Запрещаем закрытие диалога при клике вне его
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Button cancelButton = findViewById(R.id.btn_cancel);
        if (cancelButton != null) {
            if (isShowCancelButton) {
                cancelButton.setVisibility(View.VISIBLE);
            } else {
                cancelButton.setVisibility(View.GONE);
            }
        }
        cancelButton.setOnClickListener(v -> {
            if (onCancelListener != null) {
                onCancelListener.onCancel();
            }
            dismiss();
        });

    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.onCancelListener = listener;
    }

//    public void showCancelButton(boolean show) {
//        if (cancelButton != null) {
//            cancelButton.setVisibility(show ? View.VISIBLE : View.GONE);
//        }
//    }
}