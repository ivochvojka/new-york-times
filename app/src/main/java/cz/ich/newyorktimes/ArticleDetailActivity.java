package cz.ich.newyorktimes;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import cz.ich.newyorktimes.to.DocDetail;
import cz.ich.newyorktimes.utils.FontUtils;

/**
 * NYTimes article detail.
 *
 * @author Ivo Chvojka
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private DocDetail mDocDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        this.mDocDetail = (DocDetail) getIntent().getParcelableExtra(ArticleDetailFragment.ARG_DOC_DETAIL);

        setupToolbar();

        if (savedInstanceState == null) {
            addDetailFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add detail fragment to activity.
     */
    private void addDetailFragment() {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ArticleDetailFragment.ARG_DOC_DETAIL, mDocDetail);

        final ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.article_detail_container, fragment)
                .commit();
    }

    /**
     * Setup toolbar for activity.
     */
    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

        final TextView toolbarTitle = (TextView) findViewById(R.id.detail_toolbar_title);
        toolbarTitle.setText(mDocDetail.getTitle());
        toolbarTitle.setTypeface(FontUtils.getInstance(this).getTitleFont());

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

}
