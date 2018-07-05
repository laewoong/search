package com.laewoong.search.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.laewoong.search.model.response.WebInfo;
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
}
