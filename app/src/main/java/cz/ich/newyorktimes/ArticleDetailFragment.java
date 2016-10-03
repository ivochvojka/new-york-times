package cz.ich.newyorktimes;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cz.ich.newyorktimes.service.SearchKeywordsAsyncTask;
import cz.ich.newyorktimes.to.DocDetail;
import cz.ich.newyorktimes.utils.ArticleUtils;

/**
 * NyTimes article detail fragment.
 *
 * @author Ivo Chvojka
 */
public class ArticleDetailFragment extends Fragment {
    /**
     * The fragment argument representing article detail.
     */
    public static final String ARG_DOC_DETAIL = "doc_detail";

    private static final String MIME_TYPE_TEXT = "text/plain";

    /**
     * The article detail object.
     */
    private DocDetail mDocDetail;

    /**
     * Default constructor.
     */
    public ArticleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments().containsKey(ARG_DOC_DETAIL)) {
            this.mDocDetail = (DocDetail) getArguments().getParcelable(ARG_DOC_DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.article_detail, container, false);

        final TextView pubDateView = (TextView) rootView.findViewById(R.id.detail_pub_date);
        pubDateView.setText(ArticleUtils.getLocaleDateFromGMT(mDocDetail.getPubDate()));

        final TextView leadParagraphView = (TextView) rootView.findViewById(R.id.detail_lead_paragraph);
        leadParagraphView.setText(mDocDetail.getLeadParagraph());

        final TextView sourceView = (TextView) rootView.findViewById(R.id.detail_source);
        sourceView.setText(mDocDetail.getSource());

        final TextView keywordsView = (TextView) rootView.findViewById(R.id.detail_keywords);
        final ContentResolver contentResolver = getActivity().getContentResolver();
        new SearchKeywordsAsyncTask(keywordsView, contentResolver).execute(mDocDetail.getArticleId());

        final ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);
        final String imagePath = mDocDetail.getImage();
        showDetailImage(imageView, imagePath);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);

        final MenuItem item = menu.findItem(R.id.detail_menu_share);

        final ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        final Intent shareIntent = createShareIntent(mDocDetail.getArticleUrl());
        mShareActionProvider.setShareIntent(shareIntent);
    }

    /**
     * Show detail image if such exists.
     *
     * @param imageView View for image
     * @param imagePath The image path (without context path)
     */
    private void showDetailImage(ImageView imageView, String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            final String baseUrl = getContext().getString(R.string.base_url);
            final String imageUrl = baseUrl + imagePath;
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(imageView);
        }
    }

    /**
     * Create share intent for text.
     *
     * @param shareText Text to share
     * @return The share intent
     */
    private Intent createShareIntent(String shareText) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType(MIME_TYPE_TEXT);

        return shareIntent;
    }
}
