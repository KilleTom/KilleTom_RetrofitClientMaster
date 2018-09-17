package cn.ypz.com.retrofitclient.upload;

public interface FileUploadProgress {

    void uploadProgress(long currentBytesCount, long totalBytesCount);


}
