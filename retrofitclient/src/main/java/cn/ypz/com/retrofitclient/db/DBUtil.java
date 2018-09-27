package cn.ypz.com.retrofitclient.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.ypz.com.retrofitclient.db.db_entity.FileDB;
import cn.ypz.com.retrofitclient.db.db_entity.FileDBMessage;


public class DBUtil {
    private static DBUtil dbUtil;
    private Context context;
    private DaoMaster daoMaster;
    private SQLiteDatabase database;
    private DaoSession daoSession;


    public static DBUtil getDbUtil(Context context) {
        if (dbUtil == null) {
            synchronized (DBUtil.class) {
                dbUtil = new DBUtil(context);
            }
        }
        return dbUtil;
    }

    private DBUtil(Context context) {
        this.context = context;
        final String dbDir = context.getFilesDir().getAbsolutePath()+"/appDB";
        ContextWrapper wrapper = new ContextWrapper(context) {
            @Override
            public File getDatabasePath(String name) {
                    // 获取sd卡路径
                    // 数据库所在目录
                    String dbPath = dbDir + "/" + name;
                    // 数据库路径
                    // 判断目录是否存在，不存在则创建该目录
                    File dirFile = new File(dbDir);
                    if (!dirFile.exists())
                        dirFile.mkdirs();
                    // 数据库文件是否创建成功
                    boolean isFileCreateSuccess = false;
                    // 判断文件是否存在，不存在则创建该文件
                    File dbFile = new File(dbPath);
                    if (!dbFile.exists()) {
                        try {
                            isFileCreateSuccess = dbFile.createNewFile();// 创建文件
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else
                        isFileCreateSuccess = true;
                    // 返回数据库文件对象
                    Log.i("ypz",dbFile.getAbsolutePath());
                    if (isFileCreateSuccess)
                        return dbFile;
                    else
                        return super.getDatabasePath(name);
            }

            /*** Android 4.0会调用此方法获取数据库 */
            @Override
            public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
                return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            }
        };
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(wrapper, "downloadFIle.db", null);
        database = devOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();

    }

    public void insertFileDBMessage(FileDBMessage message) {
        daoSession.insertOrReplace(message);
    }

    public void insertFileDB(FileDB fileDB) {
        daoSession.insertOrReplace(fileDB);
    }

    public void updateFileDBMessage(FileDBMessage fileDBMessage) {
        daoSession.update(fileDBMessage);
    }

    public void updateFileDB(FileDB fileDB) {
        daoSession.insertOrReplace(fileDB);
    }

    public void delecFileDB(String fileName, String filePath, String fileUrl) {
        for (FileDB db : queryFileDBs(fileName, filePath, fileUrl)) {
            daoSession.delete(db);
        }
    }

    public List<FileDBMessage> queryFileDBMessages(String key) {
        return daoSession.queryBuilder(FileDBMessage.class).
                where(FileDBMessageDao.Properties.UniqueKey.eq(key)).
                orderDesc(FileDBMessageDao.Properties.RangeSize).list();
    }

    public List<FileDB> queryFileDBs(String fileName, String filePath, String fileUrl) {
        return daoSession.queryBuilder(FileDB.class).where(
                FileDBDao.Properties.FileName.eq(fileName),
                FileDBDao.Properties.FilePath.eq(filePath),
                FileDBDao.Properties.Url.eq(fileUrl)
        ).orderAsc(FileDBDao.Properties.RangeId).list();
    }

    public List<FileDB> querySingleFileDB(String rangeId){
        return daoSession.queryBuilder(FileDB.class).where(
                FileDBDao.Properties.RangeId.eq(rangeId)
        ).orderAsc(FileDBDao.Properties.RangeId).list();
    }

}
