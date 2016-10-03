package cz.ich.newyorktimes.rest;

import cz.ich.newyorktimes.pojo.Articlesearch;
import retrofit2.Callback;

/**
 * Caller for NyTimes api.
 *
 * @author Ivo Chvojka
 */
public interface INyTimesCaller {

    void searchArticles(Callback<Articlesearch> callback);
}
