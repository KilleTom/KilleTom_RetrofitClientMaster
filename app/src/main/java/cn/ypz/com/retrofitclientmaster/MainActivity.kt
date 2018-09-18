package cn.ypz.com.retrofitclientmaster

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.ypz.com.retrofitclient.client.RetrofitClient
import cn.ypz.com.retrofitclient.client.RxApiSubscriptionManger
import cn.ypz.com.retrofitclient.downLoad.DownLoadManager
import cn.ypz.com.retrofitclient.downLoad.FileBaseResponseBody
import cn.ypz.com.retrofitclient.downLoad.FileDowanloadProgress
import cn.ypz.com.retrofitclient.retrofitClientException.RetrofitClientBaeApiException
import cn.ypz.com.retrofitclient.transformEmitter.ObserverTransformEmitter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * 单线程下载
         * */
        click.setOnClickListener {
            RetrofitClient.getmInstance().setBaseUrl("http://github.com//KilleTom/").initRetrofit(application)
            val test = RetrofitClient.getmInstance().create(Test::class.java)
            RxApiSubscriptionManger.getRxApiSubscriptionManger().add("downLoadDemo",
                    RetrofitClient.getmInstance().easliyDownLoadSubscription(test.getFile("identity"),
                            object : ObserverTransformEmitter<ResponseBody, RetrofitClientBaeApiException>() {
                                override fun call(t: ResponseBody?) {
                                    val fileBaseResponseBody = object : FileDowanloadProgress {
                                        override fun dowanloadProgress(readLength: Long, contentLength: Long) {
                                            logI("已下载长度:$readLength")
                                            logI("请求下载文件长度:$contentLength")
                                            logI("Percentage(百分比):" + (readLength.toDouble() / contentLength * 100).toString() + "%")
                                            setMessage("已下载长度:$readLength\n请求下载文件长度:$contentLength\n" + "Percentage(百分比):" + (readLength.toDouble() / contentLength * 100).toString() + "%")
                                        }

                                        override fun dowanloadDone(done: Boolean) {
                                            logI("文件下载完成")
                                        }
                                    }
                                    DownLoadManager("myGitHub.zip", this@MainActivity.filesDir.path + "/dowland",
                                            this@MainActivity, true,
                                            "易庞宙—killeTom").isFinshWrittingtoDisk(FileBaseResponseBody(t, fileBaseResponseBody))
                                }

                                override fun failed(retCode: Int, msg: String?) {

                                }
                            }
                    ))
        }
        /**
         * 普通网络请求
         * */
        click.setOnClickListener {
            RetrofitClient.getmInstance().setBaseUrl("http://v.juhe.cn/toutiao/").initRetrofit(this)
            RxApiSubscriptionManger.getRxApiSubscriptionManger().add(
                    "NewsResult", RetrofitClient.getmInstance().easliySubscription(
                    RetrofitClient.getmInstance().create(Test::class.java).getNews("shehui", "13728f03ef29af183184d6d30dc6ae43"),
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
