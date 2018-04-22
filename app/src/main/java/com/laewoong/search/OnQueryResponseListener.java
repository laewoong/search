package com.laewoong.search;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public interface OnQueryResponseListener {

    public void onResponseWeb(List<WebInfo> list);
    public void onResponseImage(List<ImageInfo> list);
}
