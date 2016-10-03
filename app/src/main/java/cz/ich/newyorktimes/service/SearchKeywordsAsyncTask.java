package cz.ich.newyorktimes.service;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import cz.ich.newyorktimes.provider.NyTimesContract;

/**
 * Async task to search keywords for article in DB.
 *
 * @author Ivo Chvojka
 */
public class SearchKeywordsAsyncTask extends AsyncTask<Long, Void, String> {

    private final String TAG = getClass().getSimpleName();

    private TextView mKeywordsView;
    private ContentResolver mContentResolver;

    /**
     * Constructor.
     *
     * @param keywordsView View for keywords
     * @param contentResolver Instance of <code>ContentResolver</code>
     */
    public SearchKeywordsAsyncTask(TextView keywordsView, ContentResolver contentResolver) {
        this.mKeywordsView = keywordsView;
        this.mContentResolver = contentResolver;
    }

    @Override
    protected String doInBackground(Long... params) {
        final StringBuilder keywordsBuilder = new StringBuilder();

        Cursor cursor = null;
        try {
            cursor = queryKeywords(params[0]);

            boolean first = true;
            while (cursor.moveToNext()) {
                if (first) {
                    first = false;
                } else {
                    keywordsBuilder.append(", ");
                }
                keywordsBuilder.append(
                        cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.KeyWords.VALUE)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not query keywords.", e);
        } finally {
             if (cursor != null) {
                 cursor.close();
             }
        }

        return keywordsBuilder.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        mKeywordsView.setText(s);
    }

    /**
     * Query DB for article keywords.
     *
     * @param articleId ID of article
     * @return Cursor containing article keywords
     */
    private Cursor queryKeywords(long articleId) {
        final Cursor cursor = mContentResolver.query(NyTimesContract.KeyWords.CONTENT_URI,
                new String[]{NyTimesContract.KeyWords.VALUE},
                NyTimesContract.KeyWords.ARTICLE_ID + "=?",
                new String[]{String.valueOf(articleId)},
                NyTimesContract.KeyWords.RANK + " ASC");
        return cursor;
    }
}
