package com.laewoong.search.presenter;

import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 24..
 */

public interface SearchContract {

    interface View {

        void updateQueryResult();
        void handleEmptyQueryResult();
        void handleFinalQueryResult();
        void showErrorMessage(String errorMessage);
    }

    interface Presenter {

        String getQuery();
        List<WebInfo> getWebQueryResponseList();
        List<ImageInfo> getImageQueryResponseList();
        void onSelectedThumbnail(int position);
    }
}
