package cn.ypz.com.retrofitclientmaster;

import android.support.annotation.FloatRange;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

public interface Test {

    @GET("index")
    Observable<NewsResult> getNews(@Query("type") String type, @Query("key") String key);

    @Streaming
    @GET("KilleTomRxMaterialDesignUtil/archive/master.zip")
    Observable<ResponseBody> getFile(@Header("Accept-Encoding")String header);

    @GET("KilleTomRxMaterialDesignUtil/archive/master.zip")
    Observable<ResponseBody> getFile(@Header("Accept-Encoding")String header, @FloatRange float f);
}
