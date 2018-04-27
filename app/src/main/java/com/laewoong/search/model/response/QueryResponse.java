package com.laewoong.search.model.response;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 27..
 */

public interface QueryResponse<T> {

    int getTotal();
    int getStart();
    int getDisplay();
    List<T> getItems();
}
