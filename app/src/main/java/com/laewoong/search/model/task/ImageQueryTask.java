package com.laewoong.search.model.task;

import android.util.Log;

import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.response.QueryResponseImage;
import com.laewoong.search.util.Constants;
import retrofit2.Call;

/**
 * Created by laewoong on 2018. 4. 23..
 */

public class ImageQueryTask extends QueryTask<QueryResponseImage, ImageInfo> {

    public static final String TAG = ImageQueryTask.class.getSimpleName();

    public ImageQueryTask(NaverOpenAPIService service,String query, OnQueryResponseListener<ImageInfo> listener) {
        this(service, query, 1, listener);
    }

    public ImageQueryTask(NaverOpenAPIService service, String query, int start, OnQueryResponseListener<ImageInfo> listener) {
        super(service, query, start, listener);
    }

    @Override
    public Call<QueryResponseImage> getQuery() {
        return mService.queryImage(mQuery, mStart,
                Constants.DEFAULT_IMAGE_DISPALY,
                Constants.DEFAULT_IMAGE_SORT,
                Constants.DEFAULT_IMAGE_FILTER);
    }
}
