package drgn.cafemap.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Nobu on 2017/06/20.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private Context m_context;
    private final String TAG = "DBOpenHelper";
    private static final String DB_NAME = "cafemap_db";
    private static final int DB_VERSION = 1;

    public DBOpenHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.m_context = context;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.d(TAG, "onCreate version: " + db.getVersion());
        this.execFileSQL(db, "sql/cafe_master_tbl.sql");
        this.execFileSQL(db, "sql/cafe_user_tbl.sql");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.d(TAG, "onUpgrade version : " + db.getVersion());
        Log.d(TAG, "onUpgrade oldVersion : " + oldVersion);
        Log.d(TAG, "onUpgrade newVersion : " + newVersion);
        this.dropCafeMasterTbl(db);
        this.execFileSQL(db, "sql/cafe_master_tbl.sql");
    }

    /**
     * Executes sql file in assets folder.
     *
     * @param db
     * @param fileName
     */
    private void execFileSQL(SQLiteDatabase db, String fileName) {
        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader reader = null;
        int line = 0;
        try {
            // 文字コード(UTF-8)を指定して、ファイルを読み込み
            in = m_context.getAssets().open(fileName);
            inReader = new InputStreamReader(in, "UTF-8");
            reader = new BufferedReader(inReader);

            // ファイル内の全ての行を処理
            String s;
            while ((s = reader.readLine()) != null) {
                // 先頭と末尾の空白除去
                s = s.trim();

                // 文字が存在する場合（空白行は処理しない）
                if (0 < s.length()) {
                    line++;
                    System.out.println("Line: "+line);
                    // SQL実行
                    db.execSQL(s);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Executes drop table sql.
     *
     */
    private void dropCafeMasterTbl(SQLiteDatabase db){
        String dropCafeMasterTbl = "DROP TABLE cafe_master_tbl";

        db.execSQL(dropCafeMasterTbl);
    }

}
