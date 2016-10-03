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

//    public void call() {
//        final Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
////                .registerTypeAdapter(Multimedium.class, new MultimediaDeserializer())
//                .registerTypeAdapterFactory(new DocTypeAdapterFactory())
//                .registerTypeAdapterFactory(new MultimediumTypeAdapterFactory())
//                .create();
//        final Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://api.nytimes.com")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        final NyTimesAPI nyTimesAPI = retrofit.create(NyTimesAPI.class);
//        final Call<Articlesearch> search = nyTimesAPI.articleSearch();
//        search.enqueue(new Callback<Articlesearch>() {
//            @Override
//            public void onResponse(Call<Articlesearch> call, Response<Articlesearch> response) {
//                Log.d(TAG, "onResponse");
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "onResponse");
//                    final Doc doc = response.body().getResponse().getDocs().get(0);
//                    final List<Multimedium> multimedia = doc.getMultimedia();
//                    for (int i = 0; i < multimedia.size(); i++) {
//                        Multimedium multimedium = multimedia.get(i);
//                        Log.i(TAG, "Multimedium " + i + " =" + multimedia.toString());
//                    }
//                    Log.i(TAG, "Keys=" + doc.getKeywords().get(0).getName());
//                    Log.i(TAG, "Timezone=" + TimeZone.getDefault().toString());
//                    Log.i(TAG, "Locale=" + Locale.getDefault().toString());
//
//                    Calendar cal = Calendar.getInstance();
//                    TimeZone tz = cal.getTimeZone();
//                    Log.d(TAG, "Time zone="+tz.getDisplayName() + ", " + tz.toString());
//
//                    DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//                    m_ISO8601Local.setTimeZone(TimeZone.getTimeZone("GMT"));
////                    m_ISO8601Local.setTimeZone(TimeZone.getDefault());
////                    final int offset = TimeZone.getDefault().getRawOffset();
//                    try {
//                        Date parsed = m_ISO8601Local.parse("2016-09-11T06:08:12Z");
//                        Log.d(TAG, "Time parsed=" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM , DateFormat.MEDIUM).format(parsed));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
////                    Log.i(TAG, "getThumbnail=" + multimedia.get(0).getLegacy().getThumbnail());
////                    Log.i(TAG, "getAdditionalProperties=" + multimedia.get(0).getLegacy().getAdditionalProperties().toString());
////                    Log.i(TAG, "getThumbnail=" + multimedia.get(1).getLegacy().getThumbnail());
////                    Log.i(TAG, "getAdditionalProperties=" + multimedia.get(1).getLegacy().getAdditionalProperties().toString());
////                    Log.i(TAG, "getThumbnail=" + multimedia.get(2).getLegacy().getThumbnail());
////                    Log.i(TAG, "getAdditionalProperties=" + multimedia.get(2).getLegacy().getAdditionalProperties().toString());
//                } else {
//                    Log.e(TAG, "Call articleSearch - response is not successful.");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<Articlesearch> call, Throwable t) {
//                Log.e(TAG, "Call articleSearch failed.", t);
//            }
//        });
//    }



}

