package cn.ypz.com.retrofitclient.upoad;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class FileRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private final FileUploadProgress fileUploadProgress;
    private BufferedSink bufferSink;

    public FileRequestBody(RequestBody requestBody, FileUploadProgress fileUploadProgress) {
        this.requestBody = requestBody;
        this.fileUploadProgress = fileUploadProgress;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferSink == null) {
            bufferSink = Okio.buffer(new ForwardingSink(sink) {
                //当前写入字节数
                long writtenBytesCount = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long totalBytesCount = 0L;
                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    //增加当前写入的字节数
                    writtenBytesCount += byteCount;
                    //获得contentLength的值，后续不再调用
                    if (totalBytesCount == 0) {
                        totalBytesCount = contentLength();
                    }
                    Observable.just(writtenBytesCount).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> fileUploadProgress.uploadProgress(writtenBytesCount, totalBytesCount));

                }
            });
        }
        requestBody.writeTo(bufferSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferSink.flush();
    }
}
