package cn.ypz.com.retrofitclient.db.db_entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class FileDB {

    private String url;

    private String type;

    private String fileName;

    private String filePath;
    @Unique
    private String rangeId;

    private String rangePath;

    private long rangLength;

    private long cacheLength;

    private long tagLength;

    @Override
    public boolean equals(Object obj) {
        try {
            FileDB fileDB = (FileDB) obj;
            return (
                    url.equals(fileDB.url)
                            && type.equals(fileDB.type)
                            && fileName.equals(fileDB.fileName)
                            && filePath.equals(fileDB.filePath)
                            && rangeId.equals(fileDB.rangeId)
                            && rangLength == fileDB.rangLength
                            && cacheLength == fileDB.cacheLength
                            && tagLength == fileDB.tagLength
                            && rangePath.equals(fileDB.rangePath)
            );
        } catch (Exception e) {
            return false;
        }
    }

    @Generated(hash = 2083563550)
    public FileDB(String url, String type, String fileName, String filePath, String rangeId,
                  String rangePath, long rangLength, long cacheLength, long tagLength) {
        this.url = url;
        this.type = type;
        this.fileName = fileName;
        this.filePath = filePath;
        this.rangeId = rangeId;
        this.rangePath = rangePath;
        this.rangLength = rangLength;
        this.cacheLength = cacheLength;
        this.tagLength = tagLength;
    }

    @Generated(hash = 1254237660)
    public FileDB() {
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getRangeId() {
        return this.rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getRangePath() {
        return this.rangePath;
    }

    public void setRangePath(String rangePath) {
        this.rangePath = rangePath;
    }

    public long getRangLength() {
        return this.rangLength;
    }

    public void setRangLength(long rangLength) {
        this.rangLength = rangLength;
    }

    public long getCacheLength() {
        return this.cacheLength;
    }

    public void setCacheLength(long cacheLength) {
        this.cacheLength = cacheLength;
    }

    public long getTagLength() {
        return this.tagLength;
    }

    public void setTagLength(long tagLength) {
        this.tagLength = tagLength;
    }

    public boolean isDone() {
        return getCacheLength() == getRangLength();
    }

    public boolean isSingleDone() {
        return (getCacheLength() == getRangLength()) && (getCacheLength() == getTagLength());
    }

}
