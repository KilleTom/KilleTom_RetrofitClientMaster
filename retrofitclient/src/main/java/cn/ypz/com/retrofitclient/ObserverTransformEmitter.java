package cn.ypz.com.retrofitclient;

import android.util.Log;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cn.ypz.com.retrofitclient.client.RetrofitClient;
import cn.ypz.com.retrofitclient.retrofitClientException.RetrofitClientBaeApiException;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public abstract class ObserverTransformEmitter<T, e extends RetrofitClientBaeApiException> extends Subscriber<T> {
    @Override
    public void onNext(T value) {
        call(value);

    }

    @Override
    public void onError(Throwable error) {
        if (error instanceof RetrofitClientBaeApiException) {
            failed(((e) error).retCode, ((e) error).message);
        } else if (RetrofitClient.getmInstance().isNetworkConnected()) {
            failed(0, "网络未连接,请打开网络");
        } else if (error instanceof UnknownHostException) {
            failed(1, "您的网络不太给力，请稍后再试");
        } else if (error instanceof SocketTimeoutException) {
            failed(1, "您的网络不太给力，请稍后再试");
        } else if (error instanceof ConnectException) {
            failed(1, "您的网络不太给力，请稍后再试");
        } else if (error instanceof HttpException) {
            int code = ((HttpException) error).code();
            String msg;
            if (code >= 500 && code < 600) {
                msg = "服务器处理请求出错";
            } else if (code >= 400 && code < 500) {
                msg = "服务器无法处理请求";
            } else if (code >= 300 && code < 400) {
                msg = "请求被重定向到其他页面";
            } else {
                msg = ((HttpException) error).message();
            }
            failed(code, msg);
        } else {
            Log.e("ExceptionFail", error.getMessage());
            failed(2, error.getMessage());
        }
    }

    @Override
    public void onCompleted() {

    }

    public abstract void call(T t);

    public abstract void failed(int retCode, String msg);
}
