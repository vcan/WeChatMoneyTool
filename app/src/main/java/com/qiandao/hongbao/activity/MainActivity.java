package com.qiandao.hongbao.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.qiandao.hongbao.R;
import com.qiandao.hongbao.StatusValue;
import com.qiandao.hongbao.activity.WebViewActivity;
import com.qiandao.hongbao.util.ConnectivityUtil;
import com.qiandao.hongbao.util.Helper;
import com.qiandao.hongbao.util.UpdateTask;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import cn.bmob.v3.Bmob;

public class MainActivity extends BaseActivity implements AccessibilityManager.AccessibilityStateChangeListener {

    private static String TAG = "HongbaoMainActivity";
    private TextView switchTextview;
    private ImageView switchImageview;
    private AccessibilityManager accessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Helper.handleMaterialStatusBar(this);
        CrashReport.initCrashReport(getApplicationContext(), "900019366", false);
        Bmob.initialize(this, "0c5edbc3c54a95e5b899ceba77679594");
        accessibilityManager =
                (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        switchTextview = (TextView) findViewById(R.id.tv_accessible);
        switchImageview =(ImageView)findViewById(R.id.im_accessible);
        updateServiceStatus();
    }

    private void initPreferenceValue() {
        String excludeWordses = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_watch_exclude_words", "");
        StatusValue.getInstance().setExculdeWords(excludeWordses);

        boolean issupportBlackSceen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_watch_black_screen_notification", true);
        StatusValue.getInstance().setIsSupportBlackSreen(issupportBlackSceen);

        boolean isSupportAutoRob = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_click_open_hongbao", true);
        StatusValue.getInstance().setIsSupportAutoRob(isSupportAutoRob);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        updateServiceStatus();
        initPreferenceValue();
        // Check for update when WIFI is connected or on first time.
        if (ConnectivityUtil.isWifi(this) || UpdateTask.count == 0)
            new UpdateTask(this, false).update();
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void updateServiceStatus() {
        boolean serviceEnabled = false;
        if (accessibilityManager == null) return;
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info != null && info.getId() != null && info.getId().equals(getPackageName() + "/.HongbaoService")) {
                serviceEnabled = true;
            }
        }

        if (serviceEnabled) {
            switchTextview.setText("关闭插件");
            switchImageview.setImageResource(R.drawable.logo_stop);
            // Prevent screen from dimming
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            switchTextview.setText("开启插件");
            switchImageview.setImageResource(R.drawable.logo_start);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
//        Toast.makeText(this, "版本号：" + getVersionName(), Toast.LENGTH_SHORT).show();
    }

    private String getVersionName() {
        String version = "";
        int versioncode = 0;
        try {
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
            versioncode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version + "." + versioncode;
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.ly_accessible:
                try {
                    Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(mAccessibleIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "遇到一些问题,请手动打开系统“设置”->找到“无障碍”或者“辅助服务”->“签到钱就到”", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.ly_setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }


    public void openGithub(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/geeeeeeeeek/WeChatLuckyMoney"));
        startActivity(browserIntent);
    }

    public void openUber(View view) {
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra("title", "Uber (优惠码x7kcrsc9ub)");
        webViewIntent.putExtra("url", "https://get.uber.com.cn/invite/x7kcrsc9ub");
        startActivity(webViewIntent);
    }

    public void openWeChat(View view) {

    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }
}