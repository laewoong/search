package com.laewoong.search.model;

import com.laewoong.search.model.response.QueryResponseImage;
import com.laewoong.search.model.response.QueryResponseWeb;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public interface NaverOpenAPIService {

    @Headers({
            "X-Naver-Client-Id:" + ModelConstants.CLIENT_ID,
            "X-Naver-Client-Secret:" + ModelConstants.CLIENT_SECRET
    })

    @GET("webkr.json")
    Flowable<Response<QueryResponseWeb>> queryWeb(
            @Query("query") String query,
            @Query("start") int start,
            @Query("display") int display);


    @Headers({
            "X-Naver-Client-Id:" + ModelConstants.CLIENT_ID,
            "X-Naver-Client-Secret:" + ModelConstants.CLIENT_SECRET
    })

    @GET("image.json")
    Flowable<Response<QueryResponseImage>> queryImage(
            @Query("query") String query,
            @Query("start") int start,
            @Query("display") int display,
            @Query("sort") String sort,
            @Query("filter") String filter);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ModelConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}
