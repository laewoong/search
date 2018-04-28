package com.laewoong.search;

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
        void clearQueryResult();
    }

    interface Controller {

        String getQuery();
        List<WebInfo> getWebQueryResponseList();
        List<ImageInfo> getImageQueryResponseList();
        void loadMoreQueryResult();
    }
}
