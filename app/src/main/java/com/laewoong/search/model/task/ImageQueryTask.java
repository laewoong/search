package com.laewoong.search.model.task;

import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.response.QueryResponseImage;
import com.laewoong.search.model.ModelConstants;
import retrofit2.Call;

/**
 * Created by laewoong on 2018. 4. 23..
 */

public class ImageQueryTask extends QueryTask<QueryResponseImage, ImageInfo> {

    public static final String TAG = ImageQueryTask.class.getSimpleName();

    public ImageQueryTask(NaverOpenAPIService service,String query, OnQueryTaskResultListener<ImageInfo> listener) {
        this(service, query, 1, listener);
    }

    public ImageQueryTask(NaverOpenAPIService service, String query, int start, OnQueryTaskResultListener<ImageInfo> listener) {
        super(service, query, start, listener);
    }

    @Override
    public Call<QueryResponseImage> getQuery() {
        return mService.queryImage(mQuery, mStart,
                ModelConstants.DEFAULT_IMAGE_DISPALY,
                ModelConstants.DEFAULT_IMAGE_SORT,
                ModelConstants.DEFAULT_IMAGE_FILTER);
    }
}
