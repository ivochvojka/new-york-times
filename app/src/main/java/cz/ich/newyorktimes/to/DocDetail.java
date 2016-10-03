package cz.ich.newyorktimes.to;

import android.os.Parcel;
import android.os.Parcelable;

import cz.ich.newyorktimes.pojo.Doc;
import cz.ich.newyorktimes.provider.NyTimesContract;

/**
 * {@link Doc} as parcelable.
 *
 * @author Ivo Chvojka
 */
public class DocDetail implements Parcelable {

    private long mArticleId;
    private String mTitle;
    private String mImage;
    private String mPubDate;
    private String mArticleUrl;
    private String mLeadParagraph;
    private String mSource;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mArticleId);
        dest.writeString(mTitle);
        dest.writeString(mImage);
        dest.writeString(mPubDate);
        dest.writeString(mArticleUrl);
        dest.writeString(mLeadParagraph);
        dest.writeString(mSource);
    }

    public static final Parcelable.Creator<DocDetail> CREATOR = new Parcelable.Creator<DocDetail>() {
        public DocDetail createFromParcel(Parcel in) {
            return new DocDetail(in);
        }

        public DocDetail[] newArray(int size) {
            return new DocDetail[size];
        }
    };

    /**
     * Create parcelable for detail from {@link Doc}.
     * @param doc Instance of {@link Doc}
     * @return Detail of {@link Doc} as parcelable
     */
    public static DocDetail fromDoc(Doc doc) {
        final long articleId = (Long) doc.getAdditionalProperties().get(NyTimesContract.Articles._ID);
        final String title = doc.getSnippet();
        final String image = (String) doc.getAdditionalProperties().get(NyTimesContract.Articles.WIDE_IMAGE);
        final String pubDate = doc.getPub_date();
        final String articleUrl = doc.getWeb_url();
        final String leadParagraph = doc.getLead_paragraph();
        final String source = doc.getSource();

        return new DocDetail(articleId, title, image, pubDate, articleUrl, leadParagraph, source);
    }

    private DocDetail(Parcel in) {
        mArticleId = in.readLong();
        mTitle = in.readString();
        mImage = in.readString();
        mPubDate = in.readString();
        mArticleUrl = in.readString();
        mLeadParagraph = in.readString();
        mSource = in.readString();
    }

    private DocDetail(long articleId, String title, String image, String pubDate, String articleUrl, String leadParagraph,
                      String source) {
        mArticleId = articleId;
        mTitle = title;
        mImage = image;
        mPubDate = pubDate;
        mArticleUrl = articleUrl;
        mLeadParagraph = leadParagraph;
        mSource = source;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImage() {
        return mImage;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getArticleUrl() {
        return mArticleUrl;
    }

    public String getLeadParagraph() {
        return mLeadParagraph;
    }

    public String getSource() {
        return mSource;
    }

    public long getArticleId() {
        return mArticleId;
    }

}
