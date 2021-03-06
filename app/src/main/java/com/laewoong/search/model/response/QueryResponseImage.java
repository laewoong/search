package com.laewoong.search.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class QueryResponseImage implements QueryResponse<ImageInfo> {

    @SerializedName("lastBuildDate")
    private String lastBuildDate;

    @SerializedName("total")
    private int total;

    @SerializedName("start")
    private int start;

    @SerializedName("display")
    private int display;

    @SerializedName("items")
    List<ImageInfo> items;

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public int getTotal() {
        return total;
    }

    public int getStart() {
        return start;
    }

    public int getDisplay() {
        return display;
    }

    public List<ImageInfo> getItems() {
        return items;
    }


}
