package cz.ich.newyorktimes.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Content provider for the New York Times articles and their keywords.
 * For more information look at {@link NyTimesContract}.
 *
 * @author Ivo Chvojka
 */
public class NyTimesContentProvider extends ContentProvider {

    // constants for UriMatcher
    private static final int ARTICLE_LIST = 1;
    private static final int ARTICLE_ID = 2;
    private static final int KEYWORD_LIST = 3;
    private static final int KEYWORD_ID = 4;
    private static final UriMatcher URI_MATCHER;

    private final String TAG = getClass().getSimpleName();

    private NyTimesDatabaseHelper mHelper = null;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(NyTimesContract.AUTHORITY, "articles", ARTICLE_LIST);
        URI_MATCHER.addURI(NyTimesContract.AUTHORITY, "articles/#", ARTICLE_ID);
        URI_MATCHER.addURI(NyTimesContract.AUTHORITY, "keywords", KEYWORD_LIST);
        URI_MATCHER.addURI(NyTimesContract.AUTHORITY, "keywords/#", KEYWORD_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new NyTimesDatabaseHelper(getContext());
        return mHelper != null;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ARTICLE_LIST:
                return NyTimesContract.Articles.URI_TYPE;
            case ARTICLE_ID:
                return NyTimesContract.Articles.URI_ITEM_TYPE;
            case KEYWORD_LIST:
                return NyTimesContract.KeyWords.URI_TYPE;
            case KEYWORD_ID:
                return NyTimesContract.KeyWords.URI_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, String.format("DB insert, uri='%s'", uri));

        final String table = getTableName(uri);
        final SQLiteDatabase db = mHelper.getWritableDatabase();

        final long id = doInsert(uri, values, db, table);

        final Uri itemUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(itemUri, null);

        return itemUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.i(TAG, String.format("DB bulk insert, uri='%s'", uri));

        final String table = getTableName(uri);
        final SQLiteDatabase db = mHelper.getWritableDatabase();

        int count = 0;

        try {
            db.beginTransaction();

            for (ContentValues value : values) {
                doInsert(uri, value, db, table);
                count++;
            }

            db.setTransactionSuccessful();

            if (count > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        } finally {
            db.endTransaction();
        }

        return count;
    }

    /**
     * Get table name from <i>Uri</i>.
     *
     * @param uri Resource identifier
     * @return The DB table
     */
    private String getTableName(Uri uri) {
        String table = null;
        switch (URI_MATCHER.match(uri)) {
            case ARTICLE_LIST:
                table = NyTimesContract.Articles.TABLE;
                break;
            case KEYWORD_LIST:
                table = NyTimesContract.KeyWords.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for insert: " + uri);
        }

        return table;
    }

    /**
     * Insert <i>ContentValues</i> into DB table.
     *
     * @param uri Resource identifier
     * @param values The values to store
     * @param db Instance of <i>SQLiteDatabase</i>
     * @param table DB table
     * @return The row ID
     */
    private long doInsert(Uri uri, ContentValues values, SQLiteDatabase db, String table) {
        long id = db.insert(table, null, values);

        if (id == -1) {
            throw new SQLException("Problem while inserting into uri: " + uri);
        }

        return id;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "DB query, uri=" + uri);

        final SQLiteDatabase db = mHelper.getReadableDatabase();
        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case ARTICLE_LIST:
                builder.setTables(NyTimesContract.Articles.TABLE);
                break;
            case ARTICLE_ID:
                builder.setTables(NyTimesContract.Articles.TABLE);
                builder.appendWhere(NyTimesContract.Articles._ID + " = " + uri.getLastPathSegment());
                break;
            case KEYWORD_LIST:
                builder.setTables(NyTimesContract.KeyWords.TABLE);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = NyTimesContract.KeyWords.SORT_ORDER_DEFAULT;
                }
                break;
            case KEYWORD_ID:
                builder.setTables(NyTimesContract.KeyWords.TABLE);
                builder.appendWhere(NyTimesContract.KeyWords._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for query: " + uri);
        }

        final Cursor cursor = builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "DB delete, uri=" + uri);

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int rowsUpdated = 0;

        String table = null;
        switch (URI_MATCHER.match(uri)) {
            case ARTICLE_LIST:
                table = NyTimesContract.Articles.TABLE;
                break;
            case ARTICLE_ID:
                table = NyTimesContract.Articles.TABLE;
                selection =  modifySelectionForItem(uri, selection);
                break;
            case KEYWORD_LIST:
                table = NyTimesContract.KeyWords.TABLE;
                break;
            case KEYWORD_ID:
                table = NyTimesContract.KeyWords.TABLE;
                selection =  modifySelectionForItem(uri, selection);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for delete: " + uri);
        }

        rowsUpdated = db.delete(
                table,
                selection,
                selectionArgs);

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(TAG, "DB update, uri=" + uri);

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        int rowsUpdated = 0;

        String table = null;
        switch (URI_MATCHER.match(uri)) {
            case ARTICLE_LIST:
                table = NyTimesContract.Articles.TABLE;
                break;
            case ARTICLE_ID:
                table = NyTimesContract.Articles.TABLE;
                selection =  modifySelectionForItem(uri, selection);
                break;
            case KEYWORD_LIST:
                table = NyTimesContract.KeyWords.TABLE;
                break;
            case KEYWORD_ID:
                table = NyTimesContract.KeyWords.TABLE;
                selection =  modifySelectionForItem(uri, selection);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for update: " + uri);
        }

        rowsUpdated = db.update(
                table,
                values,
                selection,
                selectionArgs);

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private String modifySelectionForItem(Uri uri, String selection) {
        final String itemId = uri.getLastPathSegment();
        String where = BaseColumns._ID + " = " + itemId;
        if (!TextUtils.isEmpty(selection)) {
            where += " AND " + selection;
        }
        return where;
    }
}
