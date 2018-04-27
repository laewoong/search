package com.laewoong.search.presenter;

import com.laewoong.search.model.ImageInfo;
import com.laewoong.search.model.WebInfo;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 24..
 */

public interface SearchContract {

    interface View {

        void updateQueryResult();
        void handleEmptyQueryResult();
        void handleFinalQueryResult();
    }

    interface Presenter {

        String getQuery();
        List<WebInfo> getWebQueryResponseList();
        List<ImageInfo> getImageQueryResponseList();
        void onSelectedThumbnail(int position);
    }
}
