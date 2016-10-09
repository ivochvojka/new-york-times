package cz.ich.newyorktimes.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cz.ich.newyorktimes.pojo.Articlesearch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Default implementation of {@link INyTimesCaller}.
 *
 * @author Ivo Chvojka
 */
public class NyTimesCaller implements INyTimesCaller {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private final String TAG = getClass().getSimpleName();

    private String mBaseUrl;

    public NyTimesCaller(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    @Override
    public void searchArticles(Callback<Articlesearch> callback) {
        final Gson gson = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .registerTypeAdapterFactory(new DocTypeAdapterFactory())
                .create();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        final NyTimesAPI nyTimesAPI = retrofit.create(NyTimesAPI.class);

        final Call<Articlesearch> nyTimesCall = nyTimesAPI.articleSearch();
        nyTimesCall.enqueue(callback);
    }

}

