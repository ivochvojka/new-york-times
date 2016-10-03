package cz.ich.newyorktimes.rest;

import cz.ich.newyorktimes.pojo.Articlesearch;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Rest API.
 *
 * @author Ivo Chvojka
 */
public interface NyTimesAPI {

    @GET("/svc/search/v2/articlesearch.json?q=new+york+times&sort=newest&api-key=sample-key")
    Call<Articlesearch> articleSearch();

}
