package com.example.revenant.revenant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.apkfuns.logutils.LogUtils;
import com.example.revenant.revenant.Bean.Event;
import com.example.revenant.revenant.Bean.EventCode;
import com.example.revenant.revenant.Bean.WxPayParams;
import com.example.revenant.revenant.Event.JsApi;
import com.example.revenant.revenant.Utils.DemoIntentService;
import com.example.revenant.revenant.Utils.DemoPushService;
import com.example.revenant.revenant.Utils.GsonUtils;
import com.example.revenant.revenant.Utils.LoadDialog;
import com.example.revenant.revenant.Utils.PayResult;
import com.example.revenant.revenant.Utils.StatusBarUtil;
import com.example.revenant.revenant.Utils.Utils;
import com.example.revenant.revenant.view.RecorderButton;
import com.igexin.sdk.PushManager;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nanchen.compresshelper.CompressHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import wendu.dsbridge.DWebView;

import static com.example.revenant.revenant.Utils.Constant.BASE_URL;
import static com.example.revenant.revenant.Utils.Constant.pay;
import static com.example.revenant.revenant.Utils.Constant.uploadMany;
import static com.example.revenant.revenant.Utils.Constant.uploadONE;

public class MainActivity extends AppCompatActivity implements JsApi.JsCallback, UMShareListener, RecorderButton.OnFinishedRecordListener, RecorderButton.CheckRecordPermissionListener {

    private String clientId;
    private SHARE_MEDIA media;
    private List<LocalMedia> selectList = new ArrayList<>();
    private Context context;
    private Button button;
    private int REQUEST_CODE_SCAN = 111;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private String areainfo;
    private static final int REQUEST_PERMISSION = 0x007;
    private String openid;
    private String iconurl;
    private String nickname;
    private String weixinorqqtype;
    private ProgressDialog dialog;
    private String goodinfo;
    private AgentWeb mAgentWeb;
    private DWebView dWebView;
    private LinearLayout lyVoice;
    private RecorderButton recorderButton;
    private static final int SDK_PAY_FLAG = 1;
    private String url;

    private RelativeLayout loadView;
    private SmartRefreshLayout smartRefreshLayout;
    private boolean firstLoad = true;
    protected LoadDialog loadDialog;
    private ImageView ivClose;
    protected String finishAudioPath;
    protected String finishTimeLong;
    private String mVoiceLong;
    private IWXAPI api = WXAPIFactory.createWXAPI(this, com.example.revenant.revenant.Utils.Constant.WEiXIN_APP_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = BASE_URL;
        context = this;
        getGetTui();
//        initLocation();
// /*
// 初始化地图
// */
//        startLocation();
        /*
        标题栏
        * */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            StatusBarUtil.setStatusBarColor(MainActivity.this, R.color.white);
//        }
        Utils.setImageStatus(this);
        setStatusBg(R.color.colorPrimary);
        setContentView(R.layout.activity_main);
        initview();
    }

    private void setStatusBg(int resId) {
        ViewGroup contentView = findViewById(android.R.id.content);
        View statusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.getStatusBarHeight(this));
        statusBarView.setBackgroundResource(resId);
        contentView.addView(statusBarView, lp);
    }

    private void initLocation() {
        locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");

                    areainfo = location.getProvince() + location.getCity() + location.getDistrict();
                    LogUtils.d("数据是" + areainfo);
//                    LogUtils.d("数据是"+location.getAddress());
                    //定位完成的时间
                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
//                tvResult.setText(result);
                LogUtils.d("定位数据是" + result);
            } else {
                LogUtils.d("定位失败，loc is null");
            }
        }
    };

    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    private void startLocation() {
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    private void initview() {
        button = findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getPhone("18361332866");
//                addressbook("11111");
//                mapgetInfo("11111111111111");
//                thirdpartylogin("1");
//                takeShare("11111");
                recorderButton.post(new Runnable() {
                    @Override
                    public void run() {
                        lyVoice.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        dialog = new ProgressDialog(context);
        loadView = findViewById(R.id.loadView);
        smartRefreshLayout = findViewById(R.id.refreshlayout);
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        smartRefreshLayout.setEnableOverScrollDrag(false);
        smartRefreshLayout.setEnableRefresh(false);
        dWebView = new DWebView(this);
        dWebView.addJavascriptObject(new JsApi(this), null);
        //开启浏览器调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            dWebView.setWebContentsDebuggingEnabled(true);
        }

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mAgentWeb.getUrlLoader().reload();
            }
        });
        //下拉内容不偏移
        smartRefreshLayout.setEnableHeaderTranslationContent(false);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(smartRefreshLayout, new FrameLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setWebViewClient(webViewClient)
                .setWebView(dWebView)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//不跳转其他应用
                .createAgentWeb()
                .ready()
                .go(url);

        recorderButton = findViewById(R.id.recorderButton);
        lyVoice = findViewById(R.id.ly_voice);
        ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyVoice.setVisibility(View.GONE);
            }
        });
        recorderButton.setOnFinishedRecordListener(this);
        recorderButton.setCheckRecordPermissionListener(this);
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (firstLoad) {
                loadView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);


            /*
             * 前端视频自动播放
             * */
//            view.loadUrl("javascript:(function() { " +
//                    "var videos = document.getElementsByTagName('video');" +
//                    " for(var i=0;i<videos.length;i++){videos[i].play();}})()");
            /*
             * 前端音频自动播放
             * */
//            view.loadUrl("javascript:(function() { " +
//                    "var videos = document.getElementsByTagName('audio');" +
//                    " for(var i=0;i<videos.length;i++){videos[i].play();}})()");
            smartRefreshLayout.finishRefresh();
            if (firstLoad) {
                firstLoad = false;
                goneAnim(loadView);
            }
        }
    };

    private void goneAnim(final View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(700);

        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                /*
                 * 返回上一页
                 * */
//                if (dWebView != null && dWebView.canGoBack()) {
//                    dWebView.goBack();
//                }
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getGetTui() {
        EventBus.getDefault().register(this);
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event testEvent) {
        if (testEvent.getCode() == EventCode.EventCodeNum.A) {
            clientId = testEvent.getData().toString();
        } else if (testEvent.getCode() == EventCode.EventCodeNum.B) {
            dWebView.callHandler("weChatPayCallBack", new Object[]{testEvent.getData().toString()});
        }
    }


    private void initPictandVideo() {
        PictureSelector.create(MainActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .imageSpanCount(3)
                .maxSelectNum(1)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .previewVideo(false)
                .enableCrop(false)
                .freeStyleCropEnabled(false)
                .circleDimmedLayer(false)
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .isCamera(true)
                .isZoomAnim(true)
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)
                .withAspectRatio(1, 1)
                .selectionMedia(selectList)
                .minimumCompressSize(100)
                .isDragFrame(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void initPictandVideoMutil() {
        PictureSelector.create(MainActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .imageSpanCount(3)
                .maxSelectNum(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(false)
                .enableCrop(false)
                .freeStyleCropEnabled(false)
                .circleDimmedLayer(false)
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .isCamera(true)
                .isZoomAnim(true)
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)
                .withAspectRatio(1, 1)
                .selectionMedia(selectList)
                .minimumCompressSize(100)
                .isDragFrame(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectList = PictureSelector.obtainMultipleResult(data);

                    if (selectList.size() > 0 && selectList.size() == 1) {
                        for (LocalMedia media : selectList) {
                            Log.i("图片-----》", media.getPath());
                            LogUtils.d("裁剪的数据是" + media.getCutPath());
                            LogUtils.d("未裁剪的数据是" + media.getPath());
                            uploadAvatar(media.getPath());
                            if (media.isCut()) {
                                LogUtils.d("数据是" + media.getCutPath());
                            }
                        }
                    } else if (selectList.size() > 1) {
                        List<File> map = new ArrayList<>();
                        for (LocalMedia media : selectList) {
                            Log.i("图片-----》", media.getPath());
                            LogUtils.d("裁剪的数据是" + media.getCutPath());
                            LogUtils.d("未裁剪的数据是" + media.getPath());
                            String path = media.getPath();
                            File file = new File(path);
                            File newfile = CompressHelper.getDefault(context).compressToFile(file);
                            map.add(newfile);
                        }
                        LogUtils.d("数据是" + map.size());
                        uploadImages(map);
                    }
                    break;
            }
        } else if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                LogUtils.d("扫一扫：" + content);
                if (!content.isEmpty()) {
                }
//
            }
        }
    }

    private void uploadImages(List<File> map) {
        OkGo.<String>post(uploadMany)
                .tag(this)
                .params("token", "1111")
                .addFileParams("file", map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.d("数据是" + response.body());
                        if (response.code() == 200) {
                            selectList = new ArrayList<>();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtils.d("失败是" + response.message());
                    }
                });
    }

    private void uploadAvatar(final String path) {
        File file = new File(path);
        File newfile = CompressHelper.getDefault(context).compressToFile(file);

        OkGo.<String>post(uploadONE)
                .tag(this)
                .isMultipart(true)
                .params("file", newfile)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.code() == 200) {
                            LogUtils.d("返回的数据是" + response.body());
                            selectList = new ArrayList<>();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtils.d("失败是" + response.message());
                    }
                });
    }

    /*
     * 图片压缩
     * */
    private void compress(List<String> paths) {
        showDig("压缩中...", false);
        final int size = paths.size();
//        final Map<String, File> map = new HashMap<>();
        final List<File> map = new ArrayList<>();

        Luban.with(context).load(paths).ignoreBy(100)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        LogUtils.d("数据是:" + file.getPath() + "---------------->>>>>>");
//                        map.put(file.getName(), file);
                        map.add(file);
                        if (map.size() == size) {
                            //压缩完毕,上传图片
                            showDig("上传中...", false);
                            uploadImages(map);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissDig();
                        toast("图片压缩失败");

                    }
                }).launch();
    }

    protected void showDig(String msg, boolean canCancel) {
        if (loadDialog == null) {
            loadDialog = new LoadDialog.Builder(this).loadText(msg).canCancel(canCancel).build();
        } else {
            loadDialog.setText(msg);
        }
        if (!loadDialog.isShowing())
            loadDialog.show();
    }


    protected void dismissDig() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
        destroyLocation();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        toast("分享成功");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        toast("分享失败");
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        toast("分享取消");
    }


    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void addHeadImg(Object params) {

        initPictandVideo();

    }

    @Override
    public void addManyImg(Object params) {
        initPictandVideoMutil();
    }

    @Override
    public void takeCopy(Object params) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("tahome text copy", params.toString());
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);

        toast("已复制内容到剪切板");
    }

    @Override
    public void takeShare(Object params) {
        new ShareAction(MainActivity.this).withText("你好吗")
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        LogUtils.d("分享的是:" + share_media.toString());
                    }
                }).share();
    }

    @Override
    public void thirdpartylogin(Object params) {
        weixinorqqtype = params.toString();
        if (weixinorqqtype.equals("1")) {
            media = SHARE_MEDIA.WEIXIN;
        } else if ("2".equals(weixinorqqtype)) {
            media = SHARE_MEDIA.QQ;
        }
        UMShareAPI.get(context).getPlatformInfo(this,
//                SHARE_MEDIA.WEIXIN,
//                    SHARE_MEDIA.QQ,
                media,
                new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        SocializeUtils.safeShowDialog(dialog);
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        SocializeUtils.safeCloseDialog(dialog);
                        Toast.makeText(context, "授权成功", Toast.LENGTH_LONG).show();
                        openid = map.get("openid");
                        nickname = map.get("name");
                        iconurl = map.get("iconurl");
                        LogUtils.d("图片是" + iconurl);

                        StringBuilder sb = new StringBuilder();
                        for (String key : map.keySet()) {
                            sb.append(key).append(" : ").append(map.get(key)).append("\n");
                        }
                        LogUtils.d("微信信息是:" + sb.toString());

                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        SocializeUtils.safeCloseDialog(dialog);
                        Toast.makeText(context, "失败：" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        SocializeUtils.safeCloseDialog(dialog);
                        Toast.makeText(context, "取消了", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void getPhone(Object params) {
        /*
         * 直接拨打电话
         * */
        CallPhonedirect(params.toString());

        /*
         * 拨打电话界面
         * */
//        CallPhoneNOdirect(params.toString());
    }

    public void CallPhoneNOdirect(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    public void CallPhonedirect(String phoneNum) {
        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED
                ) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }


    @Override
    public void addressbook(Object params) {
        String[] list = getContacts();
        for (int i = 0; i < list.length; i++) {
            LogUtils.d("数据是" + list[i]);
        }
    }

    @Override
    public void mapgetInfo(Object params) {
        startLocation();
        LogUtils.d("地图是" + areainfo);
    }

    @Override
    public void qrcode(Object params) {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void getPayInfo(Object params) {

        /*
         * 支付宝的信息
         * */
        OkGo.<String>post(pay)
                .tag(this)
                .params("type", "AAAAa")

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtils.d("数据是" + response.body());
                        if (response.code() == 200) {
                            goodinfo = response.body();
                            start();
                        }
//

                    }
                });

        /*
         * 微信支付信息
         * */


        WxPayParams wxPayParams = GsonUtils.parseJsonWithGson(params.toString(), WxPayParams.class);
        wxPay(wxPayParams);
    }

    @Override
    public void getClientId(Object params) {
        if (!clientId.isEmpty()) {

        }
    }

    private String[] getContacts() {
        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        String[] arr = new String[cursor.getCount()];
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(0);
                //获取姓名
                String name = cursor.getString(1);
                //指定获取NUMBER这一列数据
                String[] phoneProjection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                arr[i] = id + " , 姓名：" + name;

                //根据联系人的ID获取此人的电话号码
                Cursor phonesCusor = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null,
                        null);

                //因为每个联系人可能有多个电话号码，所以需要遍历
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    do {
                        String num = phonesCusor.getString(0);
                        arr[i] += " , 电话号码：" + num;
                    } while (phonesCusor.moveToNext());
                }
                i++;
            } while (cursor.moveToNext());
        }
        return arr;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        LogUtils.d("支付成功");
//                        finish();
//                        Intent jump_gouwuche = new Intent(GoToBuyActivity.this, MainActivity.class);
////                finish();
//                        jump_gouwuche.putExtra("jump_gouwuche", 0);
//                        startActivity(jump_gouwuche);
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        Toast.makeText(GoToBuyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        Toast.makeText(GoToBuyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        LogUtils.d("支付失败");
                    }
                    break;
                }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
//                    } else {
//                        // 其他状态值则为授权失败
//                        Toast.makeText(PayDemoActivity.this,
//                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
//
//                    }
//                    break;
//                }
                default:
                    break;
            }
        }

        ;
    };

    private void start() {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                Log.d("AAAAAAAAAAAA", goodinfo);
                PayTask alipay = new PayTask(MainActivity.this);
                Map<String, String> result = alipay.payV2(goodinfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public void checkRecordPermission() {
        checkPermission();
    }

    public void checkPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    }


    @Override
    public void onFinishedRecord(String audioPath, String voiceLong) {
        finishAudioPath = audioPath;
        Log.d("TT", finishAudioPath);
        LogUtils.d("录音数据是" + finishAudioPath);
        finishTimeLong = voiceLong;
        lyVoice.setVisibility(View.GONE);
        mVoiceLong = voiceLong;


    }

    /**
     * 微信支付
     *
     * @param params
     */
    private void wxPay(WxPayParams params) {
        PayReq req = new PayReq();
        req.appId = params.getAppId();
        req.partnerId = params.getPartnerId();
        req.prepayId = params.getPrepayId();
        req.packageValue = params.getPackages();
        req.nonceStr = params.getNonceStr();
        req.timeStamp = params.getTimeStamp();
        req.sign = params.getSign();
        api.sendReq(req);
    }
}
