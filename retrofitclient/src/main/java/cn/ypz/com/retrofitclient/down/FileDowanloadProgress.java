package cn.ypz.com.retrofitclient.down;

public interface FileDowanloadProgress {

   void  dowanloadProgress(long readLength,long contentLength);

   void dowanloadDone(boolean done);
}
