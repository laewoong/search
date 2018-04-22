package com.laewoong.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by laewoong on 2018. 4. 22..
 *
 * title        : 검색 결과 이미지의 제목을 나타낸다.
 * link         : 검색 결과 이미지의 하이퍼텍스트 link를 나타낸다.
 * thumbnail    : 검색 결과 이미지의 썸네일 link를 나타낸다.
 * sizeheight   : 검색 결과 이미지의 썸네일 높이를 나타낸다.
 * sizewidth    : 검색 결과 이미지의 너비를 나타낸다. 단위는 pixel이다.
 */

public class ImageInfo implements java.io.Serializable{

    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("sizeheight")
    private String sizeheight;

    @SerializedName("sizewidth")
    private String sizewidth;


    public ImageInfo() {
        this.title = "title";
        this.link = "link";
        this.thumbnail = "thumbnail";
        this.sizeheight = "sizeheight";
        this.sizewidth = "sizewidth";
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSizeheight() {
        return sizeheight;
    }

    public String getSizewidth() {
        return sizewidth;
    }

    @Override
    public String toString() {
        return "title : " + title + ", link : " + link + ", thumbnail : " + thumbnail +
                ", sizeheight : " + sizeheight + ", sizewidth : " + sizewidth + "\n";
    }
}
