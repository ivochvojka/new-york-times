package cz.ich.newyorktimes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cz.ich.newyorktimes.pojo.Doc;
import cz.ich.newyorktimes.provider.NyTimesContract;
import cz.ich.newyorktimes.to.DocDetail;
import cz.ich.newyorktimes.utils.ArticleUtils;
import cz.ich.newyorktimes.utils.FontUtils;

/**
 * Cursor adapter for NyTimes article items.
 *
 * @author Ivo Chvojka
 */
public class ArticleItemCursorAdapter extends RecyclerView.Adapter<ArticleItemCursorAdapter.ViewHolder> {

    private String mBaseUrl;
    private Context mContext;
    private Cursor mCursor;
    private boolean mTwoPane;

    /**
     * Default constructor.
     *
     * @param context Parent activity context
     * @param twoPane Determine actual view configuration (single of twoPane mode on bigger devices - w900dp)
     */
    public ArticleItemCursorAdapter(Context context, boolean twoPane) {
        this.mContext = context;
        this.mTwoPane = twoPane;
        this.mBaseUrl = context.getString(R.string.base_url);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.article_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);

            final Doc doc = ArticleUtils.createDocFromCursor(mCursor);

            holder.mTitleView.setText(doc.getSnippet());
            holder.mPublicationView.setText(ArticleUtils.getLocaleDateFromGMT(doc.getPub_date()));

            final String thumbnailUrl = (String) doc.getAdditionalProperties().get(NyTimesContract.Articles.THUMBNAIL);
            setupThumbnail(holder.mThumbnailView, thumbnailUrl);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DocDetail docDetail = DocDetail.fromDoc(doc);

                    final Bundle docDetailBundle = new Bundle();
                    docDetailBundle.putParcelable(ArticleDetailFragment.ARG_DOC_DETAIL, docDetail);

                    if (mTwoPane) {
                        showDetailFragmentInSecondPane(docDetailBundle);
                        setupTitle(docDetail.getTitle());
                    } else {
                        final Intent detailIntent = new Intent(mContext, ArticleDetailActivity.class);
                        detailIntent.putExtras(docDetailBundle);
                        mContext.startActivity(detailIntent);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * Setup activity title.
     *
     * @param title String representing activity title
     */
    private void setupTitle(String title) {
        final TextView toolbarTitle = (TextView) ((AppCompatActivity) mContext).findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);
        toolbarTitle.setTypeface(FontUtils.getInstance(mContext).getTitleFont());
    }

    /**
     * Setup thumbnail image view.
     *
     * @param thumbnailView Image view for thumbnail
     * @param thumbnailUrl Thumbnail URL
     */
    private void setupThumbnail(ImageView thumbnailView, String thumbnailUrl) {
        if (TextUtils.isEmpty(thumbnailUrl)) {
            Glide.clear(thumbnailView);
            thumbnailView.setVisibility(View.GONE);
        } else {
            final String imageUrl = mBaseUrl + thumbnailUrl;
            Glide.with(mContext)
                    .load(imageUrl)
                    .into(thumbnailView);
        }
    }

    /**
     * In two-pane mode show detail fragment.
     *
     * @param docDetailBundle Bundle with article detail
     */
    private void showDetailFragmentInSecondPane(Bundle docDetailBundle) {
        final ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(docDetailBundle);

        final FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.article_detail_container, fragment)
                .commit();
    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
    }

    /**
     * Holder for view.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleView;
        public TextView mPublicationView;
        public ImageView mThumbnailView;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.list_title);
            mPublicationView = (TextView) view.findViewById(R.id.list_date);
            mThumbnailView = (ImageView) view.findViewById(R.id.list_thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
