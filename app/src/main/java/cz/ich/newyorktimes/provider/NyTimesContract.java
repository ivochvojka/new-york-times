package cz.ich.newyorktimes.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for NyTimes articles and keywords.
 *
 * @author Ivo Chvojka
 */
public class NyTimesContract {

    public static final String AUTHORITY = "cz.ich.newyorktimes.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Contract for articles.
     */
    public static final class Articles implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(NyTimesContract.CONTENT_URI, "articles");

        public static final String URI_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/cz.ich.newyorktimes.article";
        public static final String URI_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/cz.ich.newyorktimes.article";

        public static final String TABLE = "Articles";

        public static final String TITLE = "_title";
        public static final String PUB_DATE = "_pub_date";
        public static final String THUMBNAIL = "_thumbnail";
        public static final String WIDE_IMAGE = "_wide_image";
        public static final String URL = "_url";
        public static final String LEAD_PARAGRAPH = "_lead_paragraph";
        public static final String SOURCE = "_source";

        public static final String[] PROJECTION_ALL =
                {_ID, TITLE, PUB_DATE, THUMBNAIL, WIDE_IMAGE, URL, LEAD_PARAGRAPH, SOURCE};

        public static final String SORT_ORDER_DEFAULT =
                PUB_DATE + " ASC";
    }

    /**
     * Contract for article keywords.
     */
    public static final class KeyWords implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(NyTimesContract.CONTENT_URI, "keywords");

        public static final String URI_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/cz.ich.newyorktimes.keyword";
        public static final String URI_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/cz.ich.newyorktimes.keyword";

        public static final String TABLE = "Keywords";

        public static final String ARTICLE_ID = "_articleId";
        public static final String RANK = "_rank";
        public static final String NAME = "_name";
        public static final String VALUE = "_value";

        public static final String[] PROJECTION_ALL =
                {_ID, ARTICLE_ID, RANK, NAME, VALUE};

        public static final String SORT_ORDER_DEFAULT =
                RANK + " ASC";

    }
}
