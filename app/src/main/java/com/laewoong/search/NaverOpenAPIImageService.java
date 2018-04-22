package com.laewoong.search;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public interface NaverOpenAPIImageService {

    @Headers({
            "X-Naver-Client-Id:" + Constants.CLIENT_ID,
            "X-Naver-Client-Secret:" + Constants.CLIENT_SECRET
    })
    @GET("image.json")
    Call<QueryResponseImage> repoContributors(
            @Query("query") String query,
            @Query("start") int start,
            @Query("display") int display,
            @Query("sort") String sort,
            @Query("filter") String filter);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
