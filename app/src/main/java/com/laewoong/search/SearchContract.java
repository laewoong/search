package com.laewoong.search;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 24..
 */

public interface SearchContract {

    interface View {

        void updateQueryResult();
    }

    interface Presenter {

        String getQuery();
        List<WebInfo> getWebQueryResponseList();
        List<ImageInfo> getImageQueryResponseList();
    }
}
