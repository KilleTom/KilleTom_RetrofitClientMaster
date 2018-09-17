package cn.ypz.com.retrofitclientmaster

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.ypz.com.retrofitclient.ObserverTransformEmitter
import cn.ypz.com.retrofitclient.client.RetrofitClient
import cn.ypz.com.retrofitclient.down.DownLoadManager
import cn.ypz.com.retrofitclient.down.FileBaseResponseBody
import cn.ypz.com.retrofitclient.down.FileDowanloadProgress
import cn.ypz.com.retrofitclient.retrofitClientException.RetrofitClientBaeApiException
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        click.setOnClickListener {
            val any = try {
                RetrofitClient.getmInstance().setBaseUrl("http://github.com//KilleTom/").initRetrofit(application)
                val test = RetrofitClient.getmInstance().create(Test::class.java)
                RetrofitClient.getmInstance().easliySubscription(test.getFile("identity"), object : ObserverTransformEmitter<ResponseBody, RetrofitClientBaeApiException>() {
                    override fun call(t: ResponseBody?) {
                        val fileBaseResponseBody = object : FileDowanloadProgress {
                            override fun dowanloadProgress(readLength: Long, contentLength: Long) {
                                Log.i("易庞宙—killeTom", "readLength:" + readLength)
                                Log.i("易庞宙—killeTom", "contentLength:" + contentLength)
                                Log.i("易庞宙—killeTom", " Percentage:" + (readLength.toDouble() / contentLength * 100).toString() + "%")
                            }
                            override fun dowanloadDone(done: Boolean) {

                            }
                        }
                        DownLoadManager("myGitHub.zip", this@MainActivity.filesDir.path + "/dowland",
                                this@MainActivity, true,
                                "易庞宙—killeTom").isFinshWrittingtoDisk(FileBaseResponseBody(t, fileBaseResponseBody))
                    }

                    override fun failed(retCode: Int, msg: String?) {

                    }
                }
                )
            } catch (e: Exception) {
                Log.i("易庞宙—killeTom:", e.message)
            }
        }
    }
}
