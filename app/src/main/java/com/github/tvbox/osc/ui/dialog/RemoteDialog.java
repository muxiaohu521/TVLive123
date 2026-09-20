package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.tv.QRCodeGen;

import org.jetbrains.annotations.NotNull;

public class RemoteDialog extends BaseDialog {
    private ImageView ivQRCode;
    private TextView tvAddress;
    private TextView tvCloseRemote;

    public RemoteDialog(@NonNull @NotNull Context context) {
        super(context, R.style.CustomDialogStyleDim);
        setContentView(R.layout.dialog_remote);
        setCanceledOnTouchOutside(false);
        ivQRCode = findViewById(R.id.ivQRCode);
        tvAddress = findViewById(R.id.tvAddress);
        tvCloseRemote = findViewById(R.id.tvCloseRemote);
        tvCloseRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        refreshQRCode();
    }

    @Override
    public void dismiss() {
        ControlManager.get().stopServer();
        super.dismiss();
    }

    private void refreshQRCode() {
        String address = ControlManager.get().getAddress(false);
        tvAddress.setText(String.format("手机/电脑扫描上方二维码或者直接浏览器访问地址\n%s", address));
        int size = 240;
        ivQRCode.setImageBitmap(QRCodeGen.generateBitmap(address, size, size));
    }
}