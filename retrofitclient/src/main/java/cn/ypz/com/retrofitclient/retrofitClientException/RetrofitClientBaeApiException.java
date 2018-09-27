package cn.ypz.com.retrofitclient.retrofitClientException;

public abstract class RetrofitClientBaeApiException extends Exception{

    public int retCode;
    public String message;

    RetrofitClientBaeApiException(int retCode, String message) {
        this.retCode = retCode;
        this.message = message;
    }
}
