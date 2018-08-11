package com.laewoong.search.view;

import android.arch.paging.PagedListAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laewoong.search.R;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.Util;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebResponsePagedListAdapter extends PagedListAdapter<WebInfo, WebResponsePagedListAdapter.ViewHolder> {

    @Inject
    public WebResponsePagedListAdapter() {
        super(WebInfo.DIFF_CALLBACK);
    }

    @Override
    public WebResponsePagedListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_web_response, parent, false);

        return new WebResponsePagedListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WebResponsePagedListAdapter.ViewHolder holder, final int position) {

        final WebInfo info = getItem(position);
        holder.mTitleTextView.setText(Util.fromHtml(info.getTitle()));
        holder.mDescTextView.setText(Util.fromHtml(info.getDescription()));
        holder.mLinkTextView.setText(info.getLink());
        Linkify.addLinks(holder.mLinkTextView, Linkify.WEB_URLS);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleTextView;
        public TextView mDescTextView;
        public TextView mLinkTextView;

        public ViewHolder(View v) {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.textview_title);
            mDescTextView = (TextView) v.findViewById(R.id.textview_desc);
            mLinkTextView = (TextView) v.findViewById(R.id.textview_link);
        }
    }
}