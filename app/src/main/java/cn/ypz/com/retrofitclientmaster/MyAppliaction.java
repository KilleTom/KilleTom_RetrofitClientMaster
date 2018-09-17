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
                initRetrofit(getApplicationContext());
        Test test = RetrofitClient.getmInstance().create(Test.class);
      /*  RetrofitClient.getmInstance().
                easliySubscription(test.getNews(""), new ObserverTransformEmitter<NewsResult,RetrofitClientBaeApiException>() {
                    @Override
                    public void call(NewsResult o) {

                    }

                    @Override
                    public void failed(int retCode, String msg) {

                    }
                });*/
    }
}
