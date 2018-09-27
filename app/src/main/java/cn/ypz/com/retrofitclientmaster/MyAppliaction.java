package cn.ypz.com.retrofitclientmaster;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.TimeUnit;

import cn.ypz.com.retrofitclient.client.RetrofitClient;

public class MyAppliaction extends Application {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        RetrofitClient.getmInstance().
                setBaseUrl("").
                setRetrofitClientLog(true, "").
                setOkHttpConnectTimeOut(10, TimeUnit.SECONDS).
                setOKHttpReadTimeOut(10, TimeUnit.SECONDS).
                setOkHttpWriteTimeOut(10, TimeUnit.SECONDS).
                setOpenDebugLogMessage(true).
                initRetrofit(getApplicationContext());




        /* Test test = RetrofitClient.getmInstance().create(Test.class);*/
    }
}
