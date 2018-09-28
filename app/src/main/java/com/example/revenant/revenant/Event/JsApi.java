package com.example.revenant.revenant.Event;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsApi {
    private JsCallback jsCallback;

    public JsApi(JsCallback jsCallback) {
        this.jsCallback = jsCallback;
    }


    @JavascriptInterface
    public void addHeadImg(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.addHeadImg(params);
    }

    @JavascriptInterface
    public void addManyImg(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.addManyImg(params);
    }


    @JavascriptInterface
    public void takeCopy(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.takeCopy(params);
    }

    @JavascriptInterface
    public void takeShare(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.takeShare(params);
    }

    @JavascriptInterface
    public void thirdpartylogin(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.thirdpartylogin(params);
    }

    @JavascriptInterface
    public void mapgetInfo(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.mapgetInfo(params);
    }


    @JavascriptInterface
    public void qrcode(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.qrcode(params);
    }


    @JavascriptInterface
    public void getClientId(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.getClientId(params);
    }

    @JavascriptInterface
    public void getPayInfo(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.getPayInfo(params);
    }

    @JavascriptInterface
    public void getPhone(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.getPhone(params);
    }


    @JavascriptInterface
    public void addressbook(Object params) {
        Log.d("---->>>>", params.toString());
        jsCallback.addressbook(params);
    }


    public interface JsCallback {


        /**
         * 单张图片上传
         **/
        void addHeadImg(Object params);

        /**
         * 多张图片上传
         **/
        void addManyImg(Object params);


        /**
         * 拷贝到剪切板
         **/
        void takeCopy(Object params);

        /**
         * 分享
         **/
        void takeShare(Object params);

        /**
         * 第三方登录
         **/
        void thirdpartylogin(Object params);

        /**
         * 获取高德地图信息
         **/
        void mapgetInfo(Object params);

        /**
         * 开启二维码
         **/
        void qrcode(Object params);

        /**
         * 支付信息
         **/
        void getPayInfo(Object params);

        /**
         * 个推信息获取
         **/
        void getClientId(Object params);

        /**
         * 电话
         **/
        void getPhone(Object params);


        /**
         * 获取通讯录
         **/
        void addressbook(Object params);
    }
}
