package cz.ich.newyorktimes.utils;

import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.ich.newyorktimes.pojo.Doc;
import cz.ich.newyorktimes.provider.NyTimesContract;

/**
 * Utility methods.
 *
 * @author Ivo Chvojka
 */
public class ArticleUtils {

    private static final String TAG = "ArticleUtils";

    private static final DateFormat GMT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

    static {
        GMT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Create article object from database cursor.
     *
     * @param cursor The database cursor
     * @return Instance of {@link Doc}
     */
    public static Doc createDocFromCursor(Cursor cursor) {
        final Doc doc = new Doc();

        doc.getAdditionalProperties().put(NyTimesContract.Articles._ID,
                cursor.getLong(cursor.getColumnIndexOrThrow(NyTimesContract.Articles._ID)));
        doc.setSnippet(cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.Articles.TITLE)));
        doc.setPub_date(cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.Articles.PUB_DATE)));
        doc.setLead_paragraph(cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.Articles.LEAD_PARAGRAPH)));
        doc.setSource(cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.Articles.SOURCE)));
        doc.setWeb_url(cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.Articles.URL)));
        doc.setAdditionalProperty(NyTimesContract.Articles.THUMBNAIL, cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.Articles.THUMBNAIL)));
        doc.setAdditionalProperty(NyTimesContract.Articles.WIDE_IMAGE, cursor.getString(cursor.getColumnIndexOrThrow(NyTimesContract.Articles.WIDE_IMAGE)));

        return doc;
    }

    /**
     * Get locale date (with appropriate timezone) as string from GMT date string.
     *
     * @param gmtDate Date as string in GMT
     * @return Date as string in locale format
     */
    public static String getLocaleDateFromGMT(String gmtDate) {
        try {
            final Date parsed = GMT_DATE_FORMAT.parse(gmtDate);
            final DateFormat localeDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            return localeDateFormat.format(parsed);
        } catch (ParseException e) {
            Log.e(TAG, "Could not parse publication date", e);
        }

        return "";
    }

}
