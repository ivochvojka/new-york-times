package cz.ich.newyorktimes;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import cz.ich.newyorktimes.provider.NyTimesContract;
import cz.ich.newyorktimes.service.ArticleSearchIntentService;
import cz.ich.newyorktimes.utils.FontUtils;

/**
 * An activity with list of NyTimes articles.
 *
 * @author Ivo Chvojka
 */
public class ArticleListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();

    private static final int ARTICLES_LOADER = 1;

    private ArticleItemCursorAdapter mAdapter;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        Log.d(TAG, "ArticleListActivity started. onCreate..." + " savedInstanceState=" + savedInstanceState);

        if (savedInstanceState == null && networkConnectionAvailable()) {
            refreshNetworkData();
        }

        setupToolbar();

        initRecyclerView();

        getLoaderManager().initLoader(ARTICLES_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReceiver == null) {
            mReceiver = createReceiver();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(ArticleSearchIntentService.ACTION));
    }

    @Override
    protected void onPause() {
        if(mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NyTimesContract.Articles.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        mAdapter.setCursor(cursor);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    /**
     * Determine if network connection is available or not.
     * @return
     */
    private boolean networkConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Delete data in DB tables and download new data from network.
     */
    private void refreshNetworkData() {
        // delete DB entities
        getContentResolver().delete(NyTimesContract.Articles.CONTENT_URI, null, null);
        getContentResolver().delete(NyTimesContract.KeyWords.CONTENT_URI, null, null);

        // download new online data
        final Intent downloadIntent = new Intent(this, ArticleSearchIntentService.class);
        startService(downloadIntent);
    }

    /**
     * Init recycler view and its adapter.
     */
    private void initRecyclerView() {
        final boolean twoPane = findViewById(R.id.article_detail_container) != null;
        this.mAdapter = new ArticleItemCursorAdapter(this, twoPane);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.article_list);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Setup toolbar for activity.
     */
    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.app_name));
        toolbarTitle.setTypeface(FontUtils.getInstance(this).getTitleFont());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Broadcast receiver for {@link ArticleSearchIntentService}.
     * @return
     */
    private BroadcastReceiver createReceiver() {
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final int result = intent.getIntExtra(ArticleSearchIntentService.RESULT, -1);

                // when error occured
                if (ArticleSearchIntentService.RESULT_ERROR == result) {
                    final ContextThemeWrapper contextWrapper = new ContextThemeWrapper(ArticleListActivity.this, R.style.ErrorDialog);
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(contextWrapper);
                    final AlertDialog alertDialog = alertBuilder.setMessage(R.string.error_message).create();
                    alertDialog.show();
                }
            }
        };

        return broadcastReceiver;
    }

}
