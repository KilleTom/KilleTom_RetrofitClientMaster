package cn.ypz.com.retrofitclient.client;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface DownLoadApi {

    @Streaming
    @POST
    Observable<ResponseBody> postFile(@Header("Accept-Encoding")String header, @Url String fileUrl);

    @Streaming
    @GET
    Observable<ResponseBody> getFile(@Header("Accept-Encoding")String header, @Url String fileUrl);

    @Streaming
    @POST
    Observable<ResponseBody> postFile(@Header("Accept-Encoding")String header, @Url String fileUrl, @Header("Accept-Ranges")String range);


    @Streaming
    @GET
    Observable<ResponseBody> getFile(@Header("Accept-Encoding")String header, @Url String fileUrl, @Header("Accept-Ranges")String range);

}
