package cn.ypz.com.retrofitclientmaster;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

public interface Test {

    Observable<NewsResult> getNews(@Query("key")String key);

    @GET("KilleTomRxMaterialDesignUtil/archive/master.zip")
    Observable<ResponseBody> getFile(@Header("Accept-Encoding")String header);
}
