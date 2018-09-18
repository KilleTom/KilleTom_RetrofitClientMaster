package cn.ypz.com.retrofitclient.upoad;

public interface FileUploadProgress {

    void uploadProgress(long currentBytesCount, long totalBytesCount);

}
