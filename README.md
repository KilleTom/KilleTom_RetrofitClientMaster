# KilleTom_RetrofitClientMaster
依赖方式：https://jitpack.io/#KilleTom/KilleTom_RetrofitClientMaster 该网站提供多种可选择版本进行依赖使用
```java kotlin
//初始化方式
        RetrofitClient.getmInstance().
                setBaseUrl("").
                setRetrofitClientLog(true, "").
                setOkHttpConnectTimeOut(10, TimeUnit.SECONDS).
                setOKHttpReadTimeOut(10, TimeUnit.SECONDS).
                setOkHttpWriteTimeOut(10, TimeUnit.SECONDS).
                setOpenDebugLogMessage(true).
                initRetrofit(getApplicationContext());  
                
```
```Kotlin
//普通请求
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
//取消请求
        RxApiSubscriptionManger.getRxApiSubscriptionManger().cancel(tag)
//文件下载
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
```
