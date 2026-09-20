package com.github.tvbox.osc.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.ui.fragment.ModelSettingFragment;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

public class SettingActivity extends BaseActivity {
    private Handler mHandler = new Handler();
    private String currentApi;
    private int homeRec;
    private String currentLiveApi;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_setting;
    }

    @Override
    protected boolean shouldRefreshAutoSize() {
        return true;
    }

    @Override
    protected void init() {
        currentApi = Hawk.get(HawkConfig.API_URL, "");
        homeRec = Hawk.get(HawkConfig.HOME_REC, HawkConfig.DEFAULT_HOME_REC);
        currentLiveApi = Hawk.get(HawkConfig.LIVE_API_URL, "");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mViewPager, ModelSettingFragment.newInstance())
                .commitAllowingStateLoss();
    }

    private Runnable mDevModeRun = new Runnable() {
        @Override
        public void run() {
            devMode = "";
        }
    };

    public interface DevModeCallback {
        void onChange();
    }

    public static DevModeCallback callback = null;

    String devMode = "";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                    mHandler.removeCallbacks(mDevModeRun);
                    devMode += "0";
                    mHandler.postDelayed(mDevModeRun, 200);
                    if (devMode.length() >= 4) {
                        if (callback != null) {
                            callback.onChange();
                        }
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (currentApi.equals(Hawk.get(HawkConfig.API_URL, ""))) {
            if (homeRec != Hawk.get(HawkConfig.HOME_REC, HawkConfig.DEFAULT_HOME_REC)) {
                jumpActivity(LivePlayActivity.class, createBundle());
            } else if (!currentLiveApi.equals(Hawk.get(HawkConfig.LIVE_API_URL, ""))) {
                jumpActivity(LivePlayActivity.class, createBundle());
            }
        } else {
            AppManager.getInstance().finishActivity(LivePlayActivity.class);
            jumpActivity(LivePlayActivity.class);
            finish();
            return;
        }
        super.onBackPressed();
    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("useCache", true);
        return bundle;
    }
}