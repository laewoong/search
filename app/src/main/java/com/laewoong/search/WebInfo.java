package com.laewoong.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by laewoong on 2018. 4. 21..
 *
 * title        : 검색 결과 문서의 제목을 나타낸다. 제목에서 검색어와 일치하는 부분은 태그로 감싸져 있다.
 * link         : 검색 결과 문서의 하이퍼텍스트 link를 나타낸다.
 * description  : 검색 결과 문서의 내용을 요약한 패시지 정보이다. 문서 전체의 내용은 link를 따라가면 읽을 수 있다. 패시지에서 검색어와 일치하는 부분은 태그로 감싸져 있다.
 */

public class WebInfo {

    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("description")
    private String description;

    public WebInfo() {
        this.title = "title";
        this.link = "link";
        this.description = "description";
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "title : " + title + ", link : " + link + ", description : " + description + "\n";
    }
}
