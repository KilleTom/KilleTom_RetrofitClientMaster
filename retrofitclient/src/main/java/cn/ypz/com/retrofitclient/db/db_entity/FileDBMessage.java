package cn.ypz.com.retrofitclient.db.db_entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class FileDBMessage {

    @Unique
    private String uniqueKey;
    private String url;
    private String fileName;
    private String filePath;
    private int rangeSize;

    @Generated(hash = 1327843316)
    public FileDBMessage(String uniqueKey, String url, String fileName,
                         String filePath, int rangeSize) {
        this.uniqueKey = uniqueKey;
        this.url = url;
        this.fileName = fileName;
        this.filePath = filePath;
        this.rangeSize = rangeSize;
    }

    @Generated(hash = 1280850743)
    public FileDBMessage() {
    }

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getRangeSize() {
        return this.rangeSize;
    }

    public void setRangeSize(int rangeSize) {
        this.rangeSize = rangeSize;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            FileDBMessage fileDBMessage = (FileDBMessage) obj;
            return (
                    uniqueKey.equals(fileDBMessage.uniqueKey)
                            && rangeSize == fileDBMessage.rangeSize
                            && fileName.equals(fileDBMessage.fileName)
                            && filePath.equals(fileDBMessage.filePath)
                            && url.equals(fileDBMessage.url));
        } catch (Exception e) {
            return false;
        }
    }
}
