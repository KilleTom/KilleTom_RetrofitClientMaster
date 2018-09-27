package cn.ypz.com.retrofitclient.client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cn.ypz.com.retrofitclient.db.DBUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RetrofitClient {

    protected String baseUrl;
    protected Retrofit retrofit;
    protected Retrofit.Builder builder;
    private static RetrofitClient mInstance;
    protected OkHttpClient.Builder okHttpBuilder;
    protected long connectTime, readTime, writeTime;
    protected TimeUnit connectUnit, readUnit, writeUnit;
    protected boolean isRetrofitClientLog;
    private boolean isRetrofitClientDebugLog;
    protected String retrofitClientLogTag;
    protected Context context;


    public static RetrofitClient getmInstance() {
        if (mInstance == null)
            synchronized (RetrofitClient.class) {
                if (mInstance == null) mInstance = new RetrofitClient();
            }
        return mInstance;
    }

    protected RetrofitClient() {
        okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.retryOnConnectionFailure(true);
        builder = new Retrofit.Builder();
        baseUrl = "http:www.baidu.cpm";
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        builder = builder.
                addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                addConverterFactory(GsonConverterFactory.create(gson)).
                baseUrl(baseUrl);
        this.
                setOkHttpConnectTimeOut(30, TimeUnit.SECONDS).
                setOKHttpReadTimeOut(10, TimeUnit.MINUTES).
                setOkHttpWriteTimeOut(10, TimeUnit.MINUTES).
                setBaseUrl("http://www.baidu.com").
                setRetrofitClientLog(false, "KilleTom——易庞宙").
                initRetrofit(null);
    }

    /**
     * 初始化RetrofitClient的请求
     */
    public void initRetrofit(Context appApplicationContext) {
        builder.client(okHttpBuilder.build());
        retrofit = builder.build();
        if (context == null && appApplicationContext != null) {
            context = appApplicationContext;
            DBUtil.getDbUtil(context);
            DownLoadClient.getDownLoadClient().initApplicationContext(context);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public RetrofitClient setOpenDebugLogMessage(boolean isOpenDebugMessage ) {
        isRetrofitClientDebugLog = isOpenDebugMessage;
        return this;
    }

    protected void debugMessage(String message){
        if (isRetrofitClientDebugLog) Log.d(retrofitClientLogTag,message);
    }

    /*
     * -----------------------------------------------------------------------------
     * ----------------------------https相关证书配置----------------------------------
     * -----------------------------------------------------------------------------
     */

    public RetrofitClient addHttps(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) certificate.close();
                } catch (IOException e) {
                    debugMessage(e.getMessage());
                }
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            okHttpBuilder.sslSocketFactory(sslSocketFactory, trustManager);
        } catch (Exception e) {
            debugMessage(e.getMessage());
        }
        return this;
    }



    /*
     * -----------------------------------------------------------------------------
     * -----------------------------拦截器相关操作------------------------------------
     * -----------------------------------------------------------------------------
     */

    /**
     * @param interceptor 拦截器
     * @return RetrofitClient
     */
    public RetrofitClient addOkHttpInterceptor(Interceptor interceptor) {
        addInterceptor(interceptor);
        return this;
    }

    public RetrofitClient removeOkHttpInterceptor(Interceptor interceptor) {
        try {
            okHttpBuilder.interceptors().remove(interceptor);
        } catch (Exception e) {
            Log.i("RetrofitClient", "RetrofitClient removeOkHttpInterceptor Exception :" + e.getMessage());
        }
        initRetrofit(context);
        return this;
    }

    /**
     * 日志显示级 新建log拦截器
     */
    protected void setHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                Log.d(retrofitClientLogTag, "Retrofit====Message:" + message)).setLevel(HttpLoggingInterceptor.Level.HEADERS);
        addInterceptor(loggingInterceptor);
        okHttpBuilder.addNetworkInterceptor(loggingInterceptor);
    }

    /**
     * @param retrofitClientLog    是否打开Log日志
     * @param retrofitClientLogTag Log日志的Tag标签
     */
    public RetrofitClient setRetrofitClientLog(boolean retrofitClientLog, String
            retrofitClientLogTag) {
        isRetrofitClientLog = retrofitClientLog;
        if (!isRetrofitClientLog) return this;
        this.retrofitClientLogTag = retrofitClientLogTag;
        if (retrofitClientLogTag == null || TextUtils.isEmpty(retrofitClientLogTag))
            this.retrofitClientLogTag = "KilleTom———易庞宙";
        if (isRetrofitClientLog) setHttpLoggingInterceptor();
        return this;
    }

    /**
     * @param interceptor 拦截器
     */
    protected void addInterceptor(Interceptor interceptor) {
        if (interceptor == null) return;
        okHttpBuilder.addInterceptor(interceptor);
    }

    /**
     * 动态设置请求的BaseUrl
     */
    public RetrofitClient setBaseUrl(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) return this;
        this.baseUrl = baseUrl;
        builder.baseUrl(baseUrl);
        if (retrofit != null) retrofit = builder.build();

        return this;
    }

    /*
     * -----------------------------------------------------------------------------
     * -----------------------------时间设置相关操作----------------------------------
     * -----------------------------------------------------------------------------
     */


    /**
     * @param connectTime 连接时间
     * @param connectUnit 连接类型
     * @return RetrofitClient
     */
    public RetrofitClient setOkHttpConnectTimeOut(long connectTime, TimeUnit connectUnit) {
        setConnectTime(connectTime, connectUnit);
        return this;
    }

    /**
     * @param readTime 读取时间
     * @param readUnit 连接类型
     * @return RetrofitClient
     */
    public RetrofitClient setOKHttpReadTimeOut(long readTime, TimeUnit readUnit) {
        setReadTime(readTime, readUnit);
        return this;
    }

    public RetrofitClient setOkHttpWriteTimeOut(long writeTime, TimeUnit writeUnit) {
        setWriteTime(writeTime, writeUnit);
        return this;
    }

    /**
     * @param unit        设置的时间类型
     * @param defalutUnit 默认的时间类型
     */
    protected TimeUnit checkTimeUnit(TimeUnit unit, TimeUnit defalutUnit) {
        if (unit != null) return unit;
        else if (defalutUnit != null) return defalutUnit;
        else return TimeUnit.SECONDS;
    }

    /**
     * @param time 时间
     * @param unit 类型
     */
    protected void setConnectTime(long time, TimeUnit unit) {
        connectTime = time;
        connectUnit = checkTimeUnit(unit, TimeUnit.SECONDS);
        okHttpBuilder.connectTimeout(connectTime, connectUnit);
    }

    /**
     * @param time 时间
     * @param unit 类型
     */
    protected void setReadTime(long time, TimeUnit unit) {
        readTime = time;
        readUnit = checkTimeUnit(unit, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(readTime, unit);
    }

    /**
     * @param time 时间
     * @param unit 类型
     */
    protected void setWriteTime(long time, TimeUnit unit) {
        writeTime = time;
        writeUnit = checkTimeUnit(unit, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(writeTime, writeUnit);
    }


    /*
     * -----------------------------------------------------------------------------
     * -----------------------------—-请求相关操作------------------------------------
     * -----------------------------------------------------------------------------
     */

    /**
     * 创建请求对象
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    public Subscription baseSubscription(Observable observable, Scheduler
            subscribeOn, Scheduler unsubscribeOn, Scheduler observeOn, Subscriber subscriber) {
        return observable.subscribeOn(subscribeOn).subscribeOn(observeOn).unsubscribeOn(unsubscribeOn).subscribe(subscriber);
    }

    public Subscription easliySubscription(Observable observable, Subscriber subscriber) {
        return baseSubscription(observable, Schedulers.io(), Schedulers.io(), AndroidSchedulers.mainThread(), subscriber);
    }

    public Subscription easliyDownLoadSubscription(Observable observable, Subscriber subscriber) {
        return observable.subscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()).onBackpressureBuffer().unsubscribeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }


    public boolean isNetworkConnected() {
        Log.d("易庞宙", "isNetworkConnected: "+context);
        if (context == null) return false;
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        Log.d("易庞宙", String.valueOf(networkInfo!=null));
        Log.d("易庞宙", String.valueOf(null != networkInfo && networkInfo.isAvailable()));
        return null != networkInfo && networkInfo.isAvailable();
    }

}
