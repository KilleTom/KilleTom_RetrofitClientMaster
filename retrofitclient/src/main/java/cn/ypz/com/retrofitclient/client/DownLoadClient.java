package cn.ypz.com.retrofitclient.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.ypz.com.retrofitclient.db.DBUtil;
import cn.ypz.com.retrofitclient.db.db_entity.FileDB;
import cn.ypz.com.retrofitclient.db.db_entity.FileDBMessage;
import cn.ypz.com.retrofitclient.downLoad.DownLoadManager;
import cn.ypz.com.retrofitclient.downLoad.FileBaseResponseBody;
import cn.ypz.com.retrofitclient.downLoad.FileDowanloadProgress;
import cn.ypz.com.retrofitclient.retrofitClientException.RetrofitClientBaeApiException;
import cn.ypz.com.retrofitclient.transformEmitter.ObserverTransformEmitter;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;
import rx.Observable;
import rx.Subscription;

/**
 * The type Down load client.
 *
 * @param <RClient> the type parameter
 * @param <RE>      the type parameter
 */
public class DownLoadClient<RClient extends RetrofitClient, RE extends RetrofitClientBaeApiException> {
    /**
     * @vlaue downLoadClient 获取这个类的单例
     */
    @SuppressLint("StaticFieldLeak")
    private static DownLoadClient downLoadClient;

    private ArrayMap<String, String[]> fileDownloadTag;
    private Context context;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * Gets down load client.
     *
     * @return the down load client
     */
    public static DownLoadClient getDownLoadClient() {
        if (downLoadClient == null) {
            synchronized (DownLoadClient.class) {
                downLoadClient = new DownLoadClient();
            }
        }
        return downLoadClient;
    }

    private DownLoadClient() {
        fileDownloadTag = new ArrayMap<>();
    }

    /**
     * Init application context down load client.
     *
     * @param context the context
     * @return the down load client
     */
    public DownLoadClient initApplicationContext(Context context) {
        if (context != null && this.context == null)
            this.context = context;
        return this;
    }

    /**
     * Gets down load client config.
     *
     * @return the down load client config
     */
    public DownLoadClientConfig getDownLoadClientConfig() {
        return new DownLoadClientConfig();
    }

    /**
     * Cancle down load.
     *
     * @param fileName the file name
     */
    public void cancleDownLoad(String fileName) {
        if (fileDownloadTag.size() <= 0) return;
        if (fileDownloadTag.get(fileName) == null) return;
        for (String tag : fileDownloadTag.get(fileName))
            RxApiSubscriptionManger.getRxApiSubscriptionManger().cancel(tag);
    }

    /**
     * The type Down load client config.
     */
    public class DownLoadClientConfig {

        /**
         * The Base url.
         *
         * @vlaue baseUrl 完整url的前缀
         */
        protected String baseUrl;
        /**
         * The File url.
         *
         * @value fileUrl 完整url的后缀
         */
        protected String fileUrl;
        /**
         * The File name.
         */
        protected String fileName;
        /**
         * The File save path.
         *
         * @value fileSavePath 文件下载保留路径
         */
        protected String fileSavePath;
        /**
         * The Tag.
         *
         * @vlaue tag 网络请求的Tag标记
         */
        protected String tag;
        /**
         * The Log tag.
         *
         * @value logTag 打印日志的Tag标记
         */
        protected String logTag;
        /**
         * The Type.
         */
        protected String type;
        /**
         * The Is debug.
         */
        protected boolean isDebug;
        /**
         * The Is already completed.
         */
        protected boolean isAlreadyCompleted;
        /**
         * The Is range.
         */
        protected boolean isRange;
        /**
         * The Is ranges download done.
         */
        protected boolean[] isRangesDownloadDone;
        /**
         * The Is had file.
         */
        protected boolean isHadFile;
        /**
         * The Header response.
         */
        protected Response headerResponse;
        /**
         * The Content tag length.
         */
        protected long contentTagLength;
        /**
         * The Range size.
         */
        protected long[] saveChacheSize;
        protected long[] cacheSize;
        /**
         * The Down load threads size.
         *
         * @value downLoadThreadsSize 下载线程池数量
         */
        protected int downLoadThreadsSize;
        /**
         * The Error net call back.
         */
        protected ErrorNetCallBack errorNetCallBack;
        /**
         * The Downloadlistener.
         */
        protected DownloadListener downloadlistener;
        /**
         * The Request way.
         */
        protected RequestWay requestWay;
        /**
         * The File db messages.
         */
        protected List<FileDBMessage> fileDBMessages;
        /**
         * The Range file dbs.
         */
        protected List<FileDB> rangeFileDBS;
        /**
         * The Single file db.
         *
         * @vlaue singleFileDb 单线程下载的数据文件对象
         */
        protected FileDB singleFileDb;


        /**
         * The Header interceptor.
         */
        protected Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                headerResponse = chain.proceed(chain.request());
                String ranges = headerResponse.header("Accept-Ranges");
                isRange = !TextUtils.isEmpty(ranges) && !ranges.equals("none");
                ResponseBody body = headerResponse.body();
                if (body != null) {
                    contentTagLength = body.contentLength();
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) type = mediaType.toString();
                }
                if (contentTagLength == -1) isRange = false;
                return headerResponse;
            }
        };

        /**
         * Sets down load threads size.
         *
         * @param downLoadThreadsSize the down load threads size
         * @return the down load threads size
         */
        public DownLoadClientConfig setDownLoadThreadsSize(int downLoadThreadsSize) {
            if (downLoadThreadsSize > CPU_COUNT) this.downLoadThreadsSize = CPU_COUNT;
            else this.downLoadThreadsSize = downLoadThreadsSize;
            return this;
        }

        /**
         * Sets file url.
         *
         * @param fileUrl the file url
         * @return the file url
         */
        public DownLoadClientConfig setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        /**
         * Sets file name.
         *
         * @param fileName the file name
         * @return the file name
         */
        public DownLoadClientConfig setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * Sets file save path.
         *
         * @param fileSavePath the file save path
         * @return the file save path
         */
        public DownLoadClientConfig setFileSavePath(String fileSavePath) {
            this.fileSavePath = fileSavePath;
            return this;
        }

        /**
         * Sets change base url.
         *
         * @param baseUrl the base url
         * @return the change base url
         */
        public DownLoadClientConfig setChangeBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets debug.
         *
         * @param debug the debug
         * @return the debug
         */
        public DownLoadClientConfig setDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        /**
         * Sets log tag.
         *
         * @param logTag the log tag
         * @return the log tag
         */
        public DownLoadClientConfig setLogTag(String logTag) {
            this.logTag = logTag;
            return this;
        }

        /**
         * Log d.
         *
         * @param message the message
         */
        protected void logD(String message) {
            if (isDebug)
                Log.d(logTag, message);
        }

        /**
         * Down start.
         *
         * @param requestWay            the request way
         * @param errorNetCallBack      the error net call back
         * @param rangeDownloadlistener the range downloadlistener
         */
        public void downStart(RequestWay requestWay, ErrorNetCallBack errorNetCallBack, DownloadListener rangeDownloadlistener) {
            this.errorNetCallBack = errorNetCallBack;
            downloadlistener = rangeDownloadlistener;
            this.requestWay = requestWay;
            if (TextUtils.isEmpty(baseUrl))
                if (TextUtils.isEmpty(RClient.getmInstance().getBaseUrl())) {
                    baseUrl = "";
                } else baseUrl = RClient.getmInstance().getBaseUrl();
            else if (!RClient.getmInstance().getBaseUrl().equals(baseUrl)) {
                RClient.getmInstance().setBaseUrl(baseUrl).initRetrofit(context);
            }
            if (downLoadThreadsSize <= 0) downLoadThreadsSize = 1;
            tag = baseUrl + this.fileUrl;
            setLogTag(logTag);
            logD("url" + fileUrl);
            headerCallBack();
        }

        /**
         * 先请求一遍
         */
        protected void headerCallBack() {
            ObserverTransformEmitter observerTransformEmitter = new ObserverTransformEmitter<ResponseBody, RE>() {
                @Override
                public void call(ResponseBody body) {
                    headerCallSuccessfully(body);
                }

                @Override
                public void failed(int retCode, String msg) {
                    headerFailed(retCode, msg);
                }

                @Override
                public void onCompleted() {
                    headerCompleted();
                }
            };
            rxApiMangerAdd(tag, RClient.getmInstance().addOkHttpInterceptor(headerInterceptor).easliyDownLoadSubscription(getRequestMethodObservable("bytes=0-"), observerTransformEmitter)
            );
        }

        /**
         * Rx api manger add.
         *
         * @param tag          the tag
         * @param subscription the subscription
         */
        protected void rxApiMangerAdd(String tag, Subscription subscription) {
            RxApiSubscriptionManger.getRxApiSubscriptionManger().add(tag, subscription);
        }

        /**
         * Rx api manger cancle.
         *
         * @param tag the tag
         */
        protected void rxApiMangerCancle(String tag) {
            RxApiSubscriptionManger.getRxApiSubscriptionManger().cancel(tag);
        }

        /**
         * 第一次请求成功
         *
         * @param responseBody 请求成功的请求体
         */
        protected void headerCallSuccessfully(ResponseBody responseBody) {
            contentTagLength = responseBody.contentLength();
            MediaType mediaType = responseBody.contentType();
            if (mediaType != null)
                type = mediaType.toString();
            logD("headerCallSuccessfully");
            FileDBMessage fileDBMessage = new FileDBMessage();
            fileDBMessage.setFileName(fileName);
            fileDBMessage.setFilePath(fileSavePath);
            fileDBMessage.setRangeSize(1);
            fileDBMessage.setUniqueKey(tag + "@" + fileName);
            fileDBMessage.setUrl(tag);
            if (!isRange) {
                singleFileDb = new FileDB(tag, type, fileName, fileSavePath, fileName + "-0", fileSavePath, contentTagLength, 0, contentTagLength);
                fileDownloadTag.put(fileName, new String[]{tag});
                fileDBMessages = DBUtil.getDbUtil(context).queryFileDBMessages(tag + "@" + fileName);
                isHadFile = fileDBMessages.size() > 0;
                if (!isHadFile) {
                    DBUtil.getDbUtil(context).insertFileDBMessage(fileDBMessage);
                    logD("新建单线程下载");
                    createSingleDownload(responseBody, fileDBMessage);
                } else {
                    logD("重建单线程下载");
                    reSetSingleDownload(responseBody, fileDBMessage);
                }
            } else {
                if (!isHadFile) createRangeDownload(fileDBMessage);
                else {
                    continueRangeDownload();
                }
            }
        }

        /**
         * 第一次请求失败
         *
         * @param reCode 错误码
         * @param msg    错误信息
         */
        protected void headerFailed(int reCode, String msg) {
            Log.i(logTag, msg);
            if (errorNetCallBack != null) {
                if (!(isRange || isAlreadyCompleted)) {
                    errorNetCallBack.errorNetCallckBack(reCode, msg);
                }
            }

            if (singleFileDb != null)
                DBUtil.getDbUtil(context).updateFileDB(singleFileDb);
        }

        /**
         * 第一次请求订阅完成事件
         */
        protected void headerCompleted() {
            Log.i("ypz", "...");
            if (singleFileDb != null) DBUtil.getDbUtil(context).updateFileDB(singleFileDb);
        }

        /**
         * 利用第一次模拟请求创建单线程下载
         *
         * @param responseBody  第一次请求回调的请求体
         * @param fileDBMessage 需要更新处理的DB数据类
         */
        protected void createSingleDownload(ResponseBody responseBody, FileDBMessage fileDBMessage) {
            Log.i(logTag, "single");
            DBUtil.getDbUtil(context).insertFileDBMessage(fileDBMessage);
            if (singleFileDb != null) {
                DBUtil.getDbUtil(context).insertFileDB(singleFileDb);
            }
            saveChacheSize = new long[1];
            saveChacheSize[0] = 0;
            cacheSize = new long[1];
            if (downloadlistener != null)
                downloadlistener.rangeListInit(cacheSize.length);
            fileDownloadTag.put(fileName, new String[]{tag});
            if (isFinshWittingDisk(responseBody, fileName, fileSavePath, 0, cacheSize.length, singleFileDb, tag)) {
                singleFileDb.setRangLength(singleFileDb.getCacheLength());
                singleFileDb.setTagLength(singleFileDb.getCacheLength());
                DBUtil.getDbUtil(context).updateFileDB(singleFileDb);
                RxApiSubscriptionManger.getRxApiSubscriptionManger().cancel(tag);
                if (downloadlistener != null)
                    downloadlistener.done(0, true, 1);
            }
        }

        /**
         * 处理不支持断点时的重新下载的处理及时删除相应的数据
         *
         * @param responseBody  第一次模拟请求获取到的数据流
         * @param fileDBMessage 相应的数据库存储的数据类信息
         */
        protected void reSetSingleDownload(ResponseBody responseBody, FileDBMessage fileDBMessage) {
            List<FileDB> fileDBS = DBUtil.getDbUtil(context).querySingleFileDB(singleFileDb.getRangeId());
            logD("fileDBS.size():" + fileDBS.size());
            logD("fileDBS.get(0).getType()" + fileDBS.get(0).getType());
            logD("type:" + type);
            if (fileDBS.size() == 1 && fileDBS.get(0).getType().equals(type)) {
                boolean isfinshDone = false;
                if (contentTagLength == -1 || fileDBS.get(0).getTagLength() == contentTagLength) {
                    isfinshDone = fileDBS.get(0).isSingleDone();
                }
                if (isfinshDone) {
                    if (downloadlistener != null) {
                        downloadlistener.fileFinshDone();
                    }
                    RxApiSubscriptionManger.getRxApiSubscriptionManger().cancel(tag);
                    return;
                }
            }
            DBUtil.getDbUtil(context).delecFileDB(fileName, fileSavePath, fileUrl);
            createSingleDownload(responseBody, fileDBMessage);
        }

        /**
         * 新建多线程下载
         *
         * @param fileDBMessage 相应的数据库存储的数据类信息
         */
        protected void createRangeDownload(FileDBMessage fileDBMessage) {
            logD("新建多线程下载");
            if (downLoadThreadsSize == 0) downLoadThreadsSize = 3;
            double rangeLength = contentTagLength / downLoadThreadsSize;
            fileDBMessage.setRangeSize(downLoadThreadsSize);
            DBUtil.getDbUtil(context).insertFileDBMessage(fileDBMessage);
            String[] tags = new String[downLoadThreadsSize];
            rangeFileDBS = new ArrayList<>();
            isRangesDownloadDone = new boolean[downLoadThreadsSize];
            for (int i = 0; i < downLoadThreadsSize; i++) {
                isRangesDownloadDone[i] = false;
                FileDB fileDB = new FileDB(tag, type, fileName, fileSavePath, fileName + "-" + i, fileSavePath, contentTagLength, 0, contentTagLength);
                rangeFileDBS.add(fileDB);
                tags[i] = tag + fileDB.getRangeId();
                DBUtil.getDbUtil(context).insertFileDB(fileDB);
            }
            String fistBytes, endBytes;
            fileDownloadTag.put(fileName, tags);
            cacheSize = new long[downLoadThreadsSize];
            if (downloadlistener != null)
                downloadlistener.rangeListInit(cacheSize.length);
            for (int i = 0; i < tags.length; i++) {
                fistBytes = String.valueOf((long) (rangeLength * i));
                if (i == tags.length - 1) endBytes = "";
                else endBytes = String.valueOf((long) (rangeLength * (i + 1)));
                createRangeDownload(tags[i], ("bytes=" + fistBytes + "-" + endBytes).trim(), i, rangeFileDBS.get(i));
            }

        }

        /**
         * Continue range download.
         * 继续多线程下载
         */
        protected void continueRangeDownload() {
            logD("继续多线程下载");
            rangeFileDBS = DBUtil.getDbUtil(context).queryFileDBs(fileName, fileSavePath, tag);
            if (rangeFileDBS.size() == 0) {
                //不存在文件数据则重新下载
                createRangeDownload(fileDBMessages.get(0));
            } else {
                boolean isWrong = false;
                List<String> downloadTag = new ArrayList<>();
                List<String> downloadBytes = new ArrayList<>();
                List<FileDB> fileDBS = new ArrayList<>();
                List<Integer> index = new ArrayList<>();
                String[] tags;
                FileDB fileDB = rangeFileDBS.get(0);
                boolean isChange = fileDB.getTagLength() != contentTagLength || !fileDB.getType().equals(type);
                //判断服务器文件是否已经发生改变
                if (rangeFileDBS.get(0).getType().equals(type) || isChange) {
                    isWrong = true;
                } else {
                    //保证多线程数量和已经存在的相关数据量一致并初始化与线程数量一致的相关参数
                    downLoadThreadsSize = rangeFileDBS.size();
                    saveChacheSize = new long[downLoadThreadsSize];
                    cacheSize = new long[downLoadThreadsSize];
                    tags = new String[downLoadThreadsSize];
                    for (int i = 0; i < downLoadThreadsSize; i++) {
                        FileDB db = rangeFileDBS.get(i);
                        tags[i] = rangeFileDBS.get(i).getRangeId();
                        long cacheLength = db.getCacheLength();
                        final long rangLength = db.getRangLength();
                        //如果多线程下载缓存文件不存在缓存数据长度则设置为0
                        if (!new File(db.getRangePath() + "/" + db.getRangeId()).exists()) {
                            cacheLength = 0;
                        }
                        saveChacheSize[i] = cacheLength;
                        cacheSize[i] = cacheLength;
                        //判断是否文件已经下载好的情况没有下载好则添加list中去然后遍历集合创建多线程下载任务
                        //二次判断是否缓存长度大于区间长度大于表示出错重新删除相关数据重新创建多线程任务下载
                        if (cacheLength != rangLength && cacheLength < rangLength) {
                            downloadTag.add(tags[i]);
                            index.add(i);
                            downloadBytes.add(("bytes=" + (rangLength * i + cacheLength) + "-" + rangLength * (i + 1)).trim());
                            fileDBS.add(db);
                        } else if (cacheLength > rangLength) isWrong = true;
                        fileDownloadTag.put(fileName, tags);
                    }
                }
                if (isWrong) {
                    DBUtil.getDbUtil(context).delecFileDB(fileName, fileSavePath, tag);
                    createRangeDownload(fileDBMessages.get(0));
                } else {
                    for (int i = 0; i < fileDBS.size(); i++) {
                        createRangeDownload(downloadTag.get(i), downloadBytes.get(i), index.get(i), fileDBS.get(i));
                    }
                }
            }
        }

        /**
         * Write range file boolean.
         * 判断所有多线程下载好的任务是否已经能够全部合并写入到一个文件中去
         *
         * @return the boolean
         */
        protected boolean writeRangeFile() {
            File finalFile = new File(fileSavePath + "/" + fileName);
            File pathFile = new File(fileSavePath);
            Sink fileSink = null;
            BufferedSink fileBufferedSink = null;
            Source source = null;
            if (!finalFile.exists()) {
                if (pathFile.mkdirs()) {
                    logD("文件目录创建成功");
                } else {
                    logD("文件目录创建失败");
                }
            } else logD("文件目录已经存在");

            try {
                fileSink = Okio.sink(finalFile);
                fileBufferedSink = Okio.buffer(fileSink);
                for (FileDB db : rangeFileDBS) {
                    source = Okio.source(new File(db.getRangeId()));
                    fileBufferedSink.writeAll(source);
                }
            } catch (IOException e) {
                logD(e.getMessage());
                return false;
            } finally {
                if (source != null) {
                    try {
                        source.close();
                    } catch (IOException e) {
                        logD(e.getMessage());
                    }
                }
                if (fileBufferedSink != null) {
                    try {
                        fileBufferedSink.close();
                    } catch (IOException e) {
                        logD(e.getMessage());
                    }
                }
                if (fileSink != null) {
                    try {
                        fileSink.close();
                    } catch (IOException e) {
                        logD(e.getMessage());
                    }
                }
                System.gc();
            }
            return true;
        }


        /**
         * Judge range download done.
         * 判断所有多线程任务是否已经完成了
         */
        protected void judgeRangeDownloadDone() {
            boolean isDone = true;
            for (boolean rangeDone : isRangesDownloadDone) {
                if (!rangeDone) {
                    isDone = false;
                    break;
                }
            }
            if (isDone) {
                if (writeRangeFile()) {
                    if (downloadlistener != null) downloadlistener.fileFinshDone();
                }
            }
        }

        /**
         * 创建多线程片段下载
         *
         * @param tag    下载管理的tag标记
         * @param bytes  下载的片段区间
         * @param i      多线程下载的当前角标位置
         * @param fileDB 多线程下载的当前角标对应文件对象类
         */
        protected void createRangeDownload(String tag, String bytes, int i, FileDB fileDB) {
            rxApiMangerAdd(tag, RClient.getmInstance().easliyDownLoadSubscription(
                    getRequestMethodObservable(bytes), new ObserverTransformEmitter<ResponseBody, RetrofitClientBaeApiException>() {
                        @Override
                        public void failed(int retCode, String msg) {
                            if (errorNetCallBack != null)
                                errorNetCallBack.errorNetCallckBack(retCode, msg);
                        }

                        @Override
                        public void call(ResponseBody responseBody) {
                            if (isFinshWittingDisk(responseBody, fileDB.getRangeId(), fileDB.getRangePath(), i, downLoadThreadsSize, fileDB, tag)) {
                                if (downloadlistener != null)
                                    downloadlistener.done(i, true, downLoadThreadsSize);
                                isRangesDownloadDone[i] = true;
                                judgeRangeDownloadDone();
                            }
                        }
                    }));
        }

        /**
         * Gets request method observable.
         *
         * @param bytes 请求的bytes数据范围
         * @return Observable 线程下载的Observable
         */
        protected Observable getRequestMethodObservable(String bytes) {
            DownLoadApi loadApi = RClient.getmInstance().create(DownLoadApi.class);
            String header = "identity";
            if (requestWay == RequestWay.REQUEST_POST)
                return loadApi.postFile(header, fileUrl, bytes);
            else return loadApi.getFile(header, fileUrl, bytes);
        }

        /**
         * 判断请求下载好的数据流是否都写入到本地文件中去
         *
         * @param responseBody 数据流
         * @param fileName     文件名
         * @param fileSavePath 文件保存路径
         * @param index        线程下载的当前角标位置
         * @param rangeSize    线程下载数量
         * @param fileDB       对应的数据类
         * @param cancleTag    取消下载的tag标记
         * @return boolean true 文件写入完成 false 文件写入失败
         */
        protected boolean isFinshWittingDisk(ResponseBody responseBody, String fileName, String fileSavePath, int index, int rangeSize, FileDB fileDB, String cancleTag) {
            return new DownLoadManager(fileName, fileSavePath, context, isDebug, logTag, isRange).isFinshWittingDisk(
                    new FileBaseResponseBody(responseBody, new FileDowanloadProgress() {
                        @Override
                        public void dowanloadProgress(long readLength, long contentLength) {
                            long cacheLength ;
                            if (!isRange) {
                                cacheLength = readLength;
                                if (downloadlistener != null) {
                                    downloadlistener.callData(index, readLength, contentLength, rangeSize);
                                    downloadlistener.filePercent(contentLength, readLength, readLength / contentLength);
                                }
                            } else {
                                cacheSize[index] = saveChacheSize[index] + readLength;
                                long saveData = 0;
                                for (long cache : cacheSize) {
                                    saveData += cache;
                                }
                                cacheLength = saveData;
                                if (downloadlistener != null)
                                    downloadlistener.filePercent(contentTagLength, saveData, saveData / contentTagLength);
                            }
                            if (fileDB != null) {
                                fileDB.setCacheLength(cacheLength);
                                logD("update\nkey:" + fileDB.getRangeId());
                                DBUtil.getDbUtil(context).updateFileDB(fileDB);
                            }
                        }

                        @Override
                        public void dowanloadDone(boolean done) {
                            if (done) rxApiMangerCancle(cancleTag);
                         /*   if (done) {
                                if (fileDB != null) {
                                    fileDB.setRangLength(fileDB.getCacheLength());
                                    fileDB.setTagLength(fileDB.getCacheLength());
                                    DBUtil.getDbUtil(context).updateFileDB(fileDB);
                                }
                                RxApiSubscriptionManger.getRxApiSubscriptionManger().cancel(cancleTag);
                            }*/
                        }
                    }));
        }

    }


    /**
     * 调用下载过程中出现了错误的回调
     */
    public interface ErrorNetCallBack {
        /**
         * Error net callck back.
         *
         * @param code          错误回调码
         * @param callckMessage 错误回调信息
         */
        void errorNetCallckBack(int code, String callckMessage);
    }

    /**
     * downLoadClient 监听下载回调接口
     */
    public interface DownloadListener {
        /**
         * Range list init.
         *
         * @param rangeSize 多少个线程下载如果不支持range则默认是单线程下载
         */
        void rangeListInit(int rangeSize);

        /**
         * Call data.
         *
         * @param index           当前下载线程的角标位，如果不支持range下载则默认是0
         * @param indexChangeData 当前下载线程的网络缓存的数据长度
         * @param contentData     the content data
         * @param rangeSize       多少个线程下载如果不支持range则默认是单线程下载
         */
        void callData(int index, long indexChangeData, long contentData, int rangeSize);

        /**
         * Done.
         *
         * @param index     当前下载线程的角标位，如果不支持range下载则默认是0
         * @param isDone    当前下载线程的网络缓存的数据长度
         * @param rangeSize 多少个线程下载如果不支持range则默认是单线程下载
         */
        void done(int index, boolean isDone, int rangeSize);

        /**
         * file下载完成
         */
        void fileFinshDone();

        /**
         * File percent.
         *
         * @param contentData  the content data
         * @param downloadData the download data
         * @param Percent      the percent
         */
        void filePercent(long contentData, long downloadData, double Percent);
    }

    /**
     * The enum Request way.
     */
    public enum RequestWay {
        /**
         * Request get request way.
         */
        REQUEST_GET,
        /**
         * Request post request way.
         */
        REQUEST_POST
    }
}
