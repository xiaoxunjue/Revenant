package com.example.revenant.revenant.App;

import android.app.Application;

import com.example.revenant.revenant.Utils.Constant;
import com.lzy.okgo.OkGo;
import com.umeng.commonsdk.BuildConfig;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import static com.example.revenant.revenant.Utils.Constant.QQAPP_ID;
import static com.example.revenant.revenant.Utils.Constant.QQSECRET;
import static com.example.revenant.revenant.Utils.Constant.WEIXIN_SECRET;
import static com.example.revenant.revenant.Utils.Constant.WEiXIN_APP_ID;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.getInstance().init(this);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
        }
        UMConfigure.init(this, "5bae130bb465f5d3ac000257", "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "");
        PlatformConfig.setWeixin(WEiXIN_APP_ID, WEIXIN_SECRET);
        PlatformConfig.setQQZone(QQAPP_ID,QQSECRET);
    }
}
