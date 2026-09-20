package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;

import org.jetbrains.annotations.NotNull;

public class LiveSourceEditDialog extends BaseDialog {
    private EditText etName;
    private EditText etUrl;
    private TextView tvTitle;
    private OnEditListener listener;
    private String editName;
    private String editUrl;

    public LiveSourceEditDialog(@NonNull @NotNull Context context) {
        super(context, R.style.CustomDialogStyleDim);
        setContentView(R.layout.dialog_live_source_edit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etName);
        etUrl = findViewById(R.id.etUrl);

        if (editName != null) {
            tvTitle.setText("编辑直播源");
            etName.setText(editName);
            etUrl.setText(editUrl);
        } else {
            tvTitle.setText("新增直播源");
        }

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String url = etUrl.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "请输入名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(getContext(), "请输入网址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    Toast.makeText(getContext(), "网址需以http://或https://开头", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) {
                    listener.onConfirm(name, url);
                }
                dismiss();
            }
        });
    }

    public void setEditData(String name, String url) {
        this.editName = name;
        this.editUrl = url;
    }

    public void setOnEditListener(OnEditListener listener) {
        this.listener = listener;
    }

    public interface OnEditListener {
        void onConfirm(String name, String url);
    }
}