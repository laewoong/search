package com.laewoong.search.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laewoong.search.OnReachedListEndListener;
import com.laewoong.search.R;
import com.laewoong.search.presenter.SearchContract;
import com.laewoong.search.model.WebInfo;
import com.laewoong.search.util.Util;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class WebResponseFragment extends ResponseFragment<WebInfo> {

//    private static final String KEY_ITEM_LIST = "com.laewoong.search.view.WebResponseFragment.KEY_ITEM_LIST";
//    private static final String KEY_QUERY = "com.laewoong.search.view.WebResponseFragment.KEY_QUERY";

    public WebResponseFragment() {
        TAG = WebResponseFragment.class.getSimpleName();
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext().getApplicationContext());
    }

    @Override
    public ResponseListAdapter createResponseListAdapter() {
        return new WebResponseAdapter();
    }

    @Override
    public List<WebInfo> getResponseList() {
        return mPresenter.getWebQueryResponseList();
    }


    public static class WebResponseAdapter extends ResponseFragment.ResponseListAdapter<WebResponseAdapter.ViewHolder, WebInfo> {

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

        public WebResponseAdapter() {

        }

        // Create new views (invoked by the layout manager)
        @Override
        public WebResponseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_web_response, parent, false);

            return new WebResponseAdapter.ViewHolder(v);
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindView(ViewHolder holder, int position) {

            final WebInfo info = mDataset.get(position);
            if(info == null) {
                //TODO throw exception
                return;
            }

            holder.mTitleTextView.setText(Util.fromHtml(info.getTitle()));
            holder.mDescTextView.setText(Util.fromHtml(info.getDescription()));
            holder.mLinkTextView.setText(info.getLink());
            Linkify.addLinks(holder.mLinkTextView, Linkify.WEB_URLS);
        }
    }
}
