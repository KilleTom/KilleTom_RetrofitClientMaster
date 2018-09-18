package cn.ypz.com.retrofitclient.downLoad;

public interface FileDowanloadProgress {

   void  dowanloadProgress(long readLength,long contentLength);

   void dowanloadDone(boolean done);
}
