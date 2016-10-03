package cz.ich.newyorktimes.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.ich.newyorktimes.R;
import cz.ich.newyorktimes.pojo.Articlesearch;
import cz.ich.newyorktimes.pojo.Doc;
import cz.ich.newyorktimes.pojo.Keyword;
import cz.ich.newyorktimes.pojo.Multimedium;
import cz.ich.newyorktimes.provider.NyTimesContract;
import cz.ich.newyorktimes.rest.INyTimesCaller;
import cz.ich.newyorktimes.rest.NyTimesCaller;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Intent service for obtain NyTimes articles data and save them to DB.
 *
 * @author Ivo Chvojka
 */
public class ArticleSearchIntentService extends IntentService implements Callback<Articlesearch>{

    private final String TAG = getClass().getSimpleName();

    public static final String ACTION = "cz.ich.newyorktimes.service.ArticleSearchIntentService";

    public static final String RESULT = "RESULT";
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_ERROR = 2;

    private static final String SUBTYPE_THUMBNAIL = "thumbnail";
    private static final String SUBTYPE_WIDE_IMAGE = "wide";
    private static final String SUBTYPE_XLARGE_IMAGE = "xlarge";

    /**
     * Default constructor.
     */
    public ArticleSearchIntentService() {
        super("ArticleSearchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Search articles.");

        final String baseApiUrl = getBaseContext().getString(R.string.base_api_url);
        final INyTimesCaller nyTimesCaller = new NyTimesCaller(baseApiUrl);

        nyTimesCaller.searchArticles(this);
    }

    @Override
    public void onResponse(Call<Articlesearch> call, Response<Articlesearch> response) {
        if (response.isSuccessful()) {
            Log.i(TAG, "Articles successfuly searched.");

            final List<Doc> docs = response.body().getResponse().getDocs();

            int insertIndex = 0;
            final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < docs.size(); i++) {
                final Doc doc = docs.get(i);

                // articles
                final ContentProviderOperation.Builder builder =
                        ContentProviderOperation.newInsert(NyTimesContract.Articles.CONTENT_URI)
                        .withValues(createArticleContentValues(doc));

                ops.add(builder.build());

                // keywords
                final List<ContentValues> keywordsContentValues = createKeywordsContentValues(doc);
                for (ContentValues keyword : keywordsContentValues) {
                    final ContentProviderOperation.Builder keywordBuilder =
                            ContentProviderOperation.newInsert(NyTimesContract.KeyWords.CONTENT_URI)
                                    .withValueBackReference(NyTimesContract.KeyWords.ARTICLE_ID, insertIndex)
                                    .withValues(keyword);
                    ops.add(keywordBuilder.build());
                }

                insertIndex += keywordsContentValues.size() + 1;
            }

            try {
                getContentResolver().applyBatch(NyTimesContract.AUTHORITY, ops);
                Log.i(TAG, "Articles successfuly stored in DB.");
                publishResult(RESULT_SUCCESS);
            } catch (Exception e) {
                Log.e(TAG, "Error during applyBatch", e);
                publishResult(RESULT_ERROR);
            }
        } else {
            Log.e(TAG, "Call articleSearch - response is not successful.");
            publishResult(RESULT_ERROR);
        }
    }

    @Override
    public void onFailure(Call<Articlesearch> call, Throwable t) {
        Log.e(TAG, "Call articleSearch failed.", t);
        publishResult(RESULT_ERROR);
    }

    /**
     * Publish result of service by broadcast.
     *
     * @param result Result status
     */
    private void publishResult(int result) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Create <code>ContentValues</code> for articles.
     *
     * @param doc Instance of {@link Doc}
     * @return Article content values
     */
    private ContentValues createArticleContentValues(Doc doc) {
        final ContentValues values = new ContentValues();

        values.put(NyTimesContract.Articles.TITLE, doc.getHeadline().getMain());
        values.put(NyTimesContract.Articles.LEAD_PARAGRAPH, doc.getLead_paragraph());
        values.put(NyTimesContract.Articles.SOURCE, doc.getSource());
        values.put(NyTimesContract.Articles.URL, doc.getWeb_url());
        final String thumbnail = getImage(doc, SUBTYPE_THUMBNAIL);
        if (thumbnail != null) {
            values.put(NyTimesContract.Articles.THUMBNAIL, thumbnail);
        }
        final String wideImage = getImage(doc, SUBTYPE_XLARGE_IMAGE, SUBTYPE_WIDE_IMAGE);
        if (wideImage != null) {
            values.put(NyTimesContract.Articles.WIDE_IMAGE, wideImage);
        }
        values.put(NyTimesContract.Articles.PUB_DATE, doc.getPub_date());

        return values;
    }

    /**
     * Create <code>ContentValues</code> for keywords.
     *
     * @param doc Instance of {@link Doc}
     * @return List of keywords
     */
    private List<ContentValues> createKeywordsContentValues(Doc doc) {
        final List<Keyword> keywords = doc.getKeywords();

        final List<ContentValues> valuesList = new ArrayList<>();

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                ContentValues values = new ContentValues();
                values.put(NyTimesContract.KeyWords.NAME, keyword.getName());
                values.put(NyTimesContract.KeyWords.VALUE, keyword.getValue());
                values.put(NyTimesContract.KeyWords.RANK, keyword.getRank());
                valuesList.add(values);
            }
        }

        return valuesList;
    }

    /**
     * Get appropriate image from {@link Doc} by its image types (subtypes). <br/>
     * If image not found for image type then find for next image type.
     *
     * @param doc Instance of {@link Doc}
     * @param imageTypes List of image types
     * @return
     */
    private String getImage(Doc doc, String... imageTypes) {
        final List<Multimedium> multimedia = doc.getMultimedia();
        if (multimedia != null) {
            for (String subtype : imageTypes) {
                for (Multimedium multimedium : multimedia) {
                    if (subtype.equals(multimedium.getSubtype())) {
                        if (!TextUtils.isEmpty(multimedium.getUrl())) {
                            return multimedium.getUrl();
                        }
                    }
                }
            }
        }

        return null;
    }
}