package cn.ypz.com.retrofitclientmaster

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by kingadmin on 2017/5/6.
 */

class NewsResult(@field:SerializedName("reason")
                 val reason: String, @field:SerializedName("result")
                 val result: Result, @field:SerializedName("error_code")
                 val errorCode: Int) {
    inner class Result(@field:SerializedName("stat")
                       val stat: Int, @field:SerializedName("data")
                       val dataItems: List<ResultDataItem>)

    inner class ResultDataItem(@field:SerializedName("uniquekey")
                               val key: String, @field:SerializedName("title")
                               val title: String, @field:SerializedName("date")
                               val date: String, @field:SerializedName("category")
                               val category: String, @field:SerializedName("author_name")
                               val authorName: String, @field:SerializedName("url")
                               val url: String, @field:SerializedName("thumbnail_pic_s")
                               val image0: String, @field:SerializedName("thumbnail_pic_s02")
                               val iamge1: String?, @field:SerializedName("thumbnail_pic_s03")
                               val image2: String?) {
        private var iamges: MutableList<String>? = null

        init {
            iamges = ArrayList()
            iamges!!.add(image0)
            if (iamge1 != null) iamges!!.add(iamge1)
            if (image2 != null) iamges!!.add(image2)
        }

        fun getIamges(): MutableList<String> {
            if (iamges == null) {
                iamges = ArrayList()
                iamges!!.add(image0)
                if (iamge1 != null) iamges!!.add(iamge1)
                if (image2 != null) iamges!!.add(image2)
            }
            return iamges!!
        }
    }
}
