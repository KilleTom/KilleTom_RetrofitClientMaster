package cn.ypz.com.retrofitclient.downLoad;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

public class FileBaseResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final FileDowanloadProgress progressListener;
    private BufferedSource bufferedSource;

    public FileBaseResponseBody(ResponseBody responseBody, FileDowanloadProgress progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(
                    new ForwardingSource(responseBody.source()) {
                        long totalBytesRead = 0L;
                        @Override
                        public long read(Buffer sink, long byteCount) throws IOException {
                            long bytesRead = super.read(sink, byteCount);
                            if (null != progressListener) {
                                if (bytesRead == -1) progressListener.dowanloadDone(true);
                                else {
                                    totalBytesRead += bytesRead;
                                    progressListener.dowanloadProgress(totalBytesRead, responseBody.contentLength());
                                }
                            }
                            return bytesRead;
                        }
                    });
        }
        return bufferedSource;
    }
}

