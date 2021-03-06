package cn.ypz.com.retrofitclient.downLoad;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class DownLoadManager {

    public static final String APK = "application/vnd.android.package-archive";
    public static final String PDF = "application/pdf";
    public static final String ZIP_0 = "application/zip";
    public static final String ZIP_1 = "application/x-compressed-zip";
    public static final String PNG = "image/png";
    public static final String JPEG = "image/jpeg";
    public static final String JPG = "image/jpg";
    public static final String GIF = "image/gif";
    public static final String HTML = "text/html";
    private static final String JS = "application/x-javascript";
    private String contentTYpe;
    protected FileBaseResponseBody fileBaseResponseBody;
    protected String fileName;
    protected String downFilePath;
    protected Context context;
    protected boolean isDebugLog;
    protected boolean isRange;
    protected String logTag;

    public DownLoadManager(String fileName, String downFilePath, Context context, boolean isDebugLog, String logTag, boolean isRange) {
        this(fileName, downFilePath, context, isDebugLog, logTag);
        this.isRange = isRange;
    }

    public DownLoadManager(String fileName, String downFilePath, Context context, boolean isDebugLog, String logTag) {
        this.fileName = fileName;
        this.downFilePath = downFilePath;
        this.context = context;
        this.isDebugLog = isDebugLog;
        this.logTag = (!TextUtils.isEmpty(logTag)) ? logTag : "ypz_killeTom";
    }

    protected boolean typeMatchers(String type) {
        boolean isEmpaty = TextUtils.isEmpty(contentTYpe);
        if (!isEmpaty) return type.equals(contentTYpe);
        if (fileBaseResponseBody == null && contentTYpe == null) return false;
        else {
            assert fileBaseResponseBody != null;
            contentTYpe = Objects.requireNonNull(fileBaseResponseBody.contentType()).toString();
        }
        return contentTYpe != null && type.equals(contentTYpe);
    }

    public boolean isFinshWittingDisk(FileBaseResponseBody fileBaseResponseBody) {
        contentTYpe = fileBaseResponseBody.contentType().toString();
        this.fileBaseResponseBody = fileBaseResponseBody;
        if (TextUtils.isEmpty(contentTYpe)) {
            notEmptyContentType(fileBaseResponseBody);
        } else {
            if (isRange) rangeDownLoadManager();
            else if (typeMatchers(APK)) apkDownLoadManager();
            else if (typeMatchers(PDF)) pdfDownLoadManager();
            else if (typeMatchers(ZIP_0) || typeMatchers(ZIP_1)) zipDownLoadManager();
            else if (typeMatchers(PNG)) pngDownLoadManager();
            else if (typeMatchers(JPEG) || typeMatchers(JPG)) jpegDownLoadManager();
            else if (typeMatchers(GIF)) gifDownLoadManager();
            else if (typeMatchers(HTML)) htmlDownLoadManger();
            else if (typeMatchers(JS)) jsDownLoadManger();
            else steamDownloadType();
        }
        File file = new File(downFilePath);
        if (!file.exists()) file.mkdir();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logD("创建文件异常");
            }
        }
        File downlandFile = new File(downFilePath + "/" + fileName);
        Log.i("ypz", downlandFile.getAbsolutePath());
        Sink fileSink = null;
        BufferedSink fileBufferedSink = null;
        try {
            if (isRange && downlandFile.length() > 0) fileSink = Okio.appendingSink(downlandFile);
            else fileSink = Okio.sink(downlandFile);
            fileBufferedSink = Okio.buffer(fileSink);
            fileBufferedSink.writeAll(fileBaseResponseBody.source());
        } catch (FileNotFoundException e) {
            logD("文件没有找到异常", e);
            return false;
        } catch (IOException e) {
            logD("文件写入异常", e);
            return false;
        } finally {
            if (fileBufferedSink != null) {
                try {
                    fileBufferedSink.close();
                } catch (IOException e) {
                    logD("BufferedSink关闭异常", e);
                }
            }
            if (fileSink != null) {
                try {
                    fileSink.close();
                } catch (IOException e) {
                    logD("Sink关闭异常", e);
                }
            }
            System.gc();
        }
        return true;
    }


    protected void rangeDownLoadManager() {

    }

    private void logD(String message, Exception e) {
        if (isDebugLog) {
            logD(message);
            logD("异常信息\n" + e.getMessage());
        }
    }

    protected void logD(String message) {
        Log.d(logTag, message);
    }

    protected void jsDownLoadManger() {

    }

    protected void htmlDownLoadManger() {
    }


    protected void gifDownLoadManager() {
        checkSaveFileType(".gif");
    }

    protected void steamDownloadType() {

    }

    protected void apkDownLoadManager() {
        checkSaveFileType(".apk");
    }

    protected void pdfDownLoadManager() {
        checkSaveFileType(".pdf");
    }

    protected void zipDownLoadManager() {
        checkSaveFileType(".zip");
    }

    protected void notEmptyContentType(FileBaseResponseBody fileBaseResponseBody) {

    }

    protected void pngDownLoadManager() {
        checkSaveFileType(".png");
    }

    protected void jpegDownLoadManager() {
        checkSaveFileType("jpeg");
    }

    protected void checkSaveFileType(String saveType) {
        if (!(saveType.indexOf(".") == 0)) saveType = "." + saveType;
        if (!fileName.endsWith(saveType)) {
            int lastIndex = fileName.lastIndexOf(".");
            fileName = fileName.substring(0, lastIndex) + saveType;
        }
    }
}
