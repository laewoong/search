package com.laewoong.search.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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

import com.laewoong.search.R;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.Util;
import com.laewoong.search.viewmodel.SearchViewModel;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class WebResponseFragment extends ResponseFragment<WebInfo> {

    public static final String TAG = WebResponseFragment.class.getSimpleName();

    private SearchViewModel searchViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        searchViewModel.getQuery().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String query) {
                searchViewModel.queryWeb(query);
            }
        });

        searchViewModel.getWebInfoList().observe(this, new Observer<List<WebInfo>>() {
            @Override
            public void onChanged(@Nullable List<WebInfo> webInfos) {
                mAdapter.setItem(webInfos);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
            }
        });

        searchViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String message) {
                showErrorMessage(message);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mAdapter.setItem(searchViewModel.getWebInfoList().getValue());
        mAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext().getApplicationContext());
    }

    @Override
    public ResponseListAdapter createResponseListAdapter() {
        return new WebResponseListAdapter();
    }

    public static class WebResponseListAdapter extends ResponseFragment.ResponseListAdapter<WebResponseListAdapter.ViewHolder, WebInfo> {

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

        @Override
        public WebResponseListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_web_response, parent, false);

            return new WebResponseListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindView(ViewHolder holder, int position) {

            final WebInfo info = mDataset.get(position);

            holder.mTitleTextView.setText(Util.fromHtml(info.getTitle()));
            holder.mDescTextView.setText(Util.fromHtml(info.getDescription()));
            holder.mLinkTextView.setText(info.getLink());
            Linkify.addLinks(holder.mLinkTextView, Linkify.WEB_URLS);
        }
    }
}
