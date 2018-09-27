package cn.ypz.com.retrofitclientmaster

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.ypz.com.retrofitclient.client.DownLoadClient
import cn.ypz.com.retrofitclient.client.RetrofitClient
import cn.ypz.com.retrofitclient.client.RxApiSubscriptionManger
import cn.ypz.com.retrofitclient.retrofitClientException.RetrofitClientBaeApiException
import cn.ypz.com.retrofitclient.transformEmitter.ObserverTransformEmitter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**
     * 因为涉及到了断点下载以及普通请求
     * 上传代码没法演示在商业使用服务器不能暴露请求接口
     * 本代码皆由本人易庞宙
     * */
    private lateinit var longlist: LongArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * 线程下载示例
         * */
        click.setOnClickListener {
            downloadFile()
        }
        /**
         * 普通网络请求
         * */
        news.setOnClickListener { getNews() }


    }

    fun downloadFile() {
        lateinit var isDownload: BooleanArray
        lateinit var longlist: LongArray
        DownLoadClient.getDownLoadClient().initApplicationContext(this).downLoadClientConfig.setChangeBaseUrl("http://github.com//KilleTom/").setFileUrl("KilleTomRxMaterialDesignUtil/archive/master.zip").setDebug(true).setLogTag("易庞宙—killeTom").setDownLoadThreadsSize(1).setFileName("myGitHub.zip").setFileSavePath(this@MainActivity.filesDir.path + "/dowland").downStart(
                DownLoadClient.RequestWay.REQUEST_POST,
                { code, callMessage -> logI("code:$code\nMessage:$callMessage") },
                object : DownLoadClient.DownloadListener {
                    //文件下载百分比比回调
                    override fun filePercent(contentData: Long, downloadData: Long, Percent: Double) {

                    }

                    //文件下载完成回调
                    override fun fileFinshDone() {
                        logI("done")
                    }

                    //每一个线程下载的数据回调
                    override fun callData(index: Int, indexChangeData: Long, contentData: Long, rangeSize: Int) {
                        longlist[index] = indexChangeData
                        logI("index:$index\nindexChangeData:$indexChangeData\ncontentData:$contentData\nrangeSize:$rangeSize")
                    }

                    //下载的前的回调
                    override fun rangeListInit(rangeSize: Int) {
                        longlist = LongArray(rangeSize, init = { 0 })
                        isDownload = BooleanArray(rangeSize, init = { false })
                    }

                    //每个线程下载完成会的回调。多线程有多次回调、单线程只回调一次
                    override fun done(index: Int, isDone: Boolean, rangeSize: Int) {
                        isDownload[index] = isDone
                        var isAllDone = true
                        isDownload.forEach { b: Boolean -> if (!b) isAllDone = b }
                        if (isAllDone) {
                            logI("done")
                        }
                    }
                })
    }

    fun getNews() {
        RetrofitClient.getmInstance().setBaseUrl("http://v.juhe.cn/toutiao/").initRetrofit(this)
        RxApiSubscriptionManger.getRxApiSubscriptionManger().add(
                "NewsResult",
                RetrofitClient.getmInstance().easliySubscription(
                        RetrofitClient.getmInstance().create(Test::class.java).getNews(
                                "shehui", "13728f03ef29af183184d6d30dc6ae43"),
                        object : ObserverTransformEmitter<NewsResult, RetrofitClientBaeApiException>() {
                            override fun call(t: NewsResult?) {
                                setMessage(Gson().toJson(t))
                            }

                            override fun failed(retCode: Int, msg: String?) {
                                logI("failedCode:$retCode\nMessage:$msg")
                                setMessage("failedCode:$retCode\nMessage:$msg")
                            }

                        }))
    }

    override fun onDestroy() {
        super.onDestroy()
        RxApiSubscriptionManger.getRxApiSubscriptionManger().cancel("NewsResult")
    }

    fun logI(message: String) {
        Log.i("易庞宙—killeTom", message)
    }

    fun setMessage(message: String) {
        runOnUiThread({ this@MainActivity.message.text = message })
    }
}
