package cz.ich.newyorktimes.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Database helper for <i>Articles</i> and <i>Keywords</i> tables.
 *
 * @author Ivo Chvojka
 */
public class NyTimesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NyTimesArticle.db";
    private static final int DATABASE_VERSION = 1;

    private final String TAG = getClass().getSimpleName();

    private static final String ARTICLES_DATABASE_CREATE = "create table " + NyTimesContract.Articles.TABLE
            + "(" + NyTimesContract.Articles._ID + " integer primary key autoincrement, "
            + NyTimesContract.Articles.TITLE + " text, "
            + NyTimesContract.Articles.PUB_DATE + " text, "
            + NyTimesContract.Articles.THUMBNAIL + " blob, "
            + NyTimesContract.Articles.WIDE_IMAGE + " blob, "
            + NyTimesContract.Articles.URL + " text, "
            + NyTimesContract.Articles.LEAD_PARAGRAPH + " text, "
            + NyTimesContract.Articles.SOURCE + " text);";

    private static final String KEYWORDS_DATABASE_CREATE = "create table " + NyTimesContract.KeyWords.TABLE
            + "(" + NyTimesContract.KeyWords._ID + " integer primary key autoincrement, "
            + NyTimesContract.KeyWords.ARTICLE_ID + " integer, "
            + NyTimesContract.KeyWords.RANK + " integer, "
            + NyTimesContract.KeyWords.NAME + " text, "
            + NyTimesContract.KeyWords.VALUE + " text);";

    public NyTimesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final long currTime = System.currentTimeMillis();

        db.execSQL(ARTICLES_DATABASE_CREATE);
        db.execSQL(KEYWORDS_DATABASE_CREATE);

        Log.d(TAG, "DB helper onCreate() finished in " + (System.currentTimeMillis() - currTime) + "ms.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NyTimesContract.Articles.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NyTimesContract.KeyWords.TABLE);
        onCreate(db);
    }
}
