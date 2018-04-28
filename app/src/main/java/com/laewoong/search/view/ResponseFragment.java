package com.laewoong.search.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.laewoong.search.R;
import com.laewoong.search.SearchContract;
import com.laewoong.search.util.Util;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by laewoong on 2018. 4. 26..
 */

public abstract class ResponseFragment<T> extends Fragment implements SearchContract.View {

    public static abstract class ResponseListAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

        protected List<T> mDataset;
        protected String mKeyword;
        protected Handler mUiHandler;
        protected boolean mIsInfinityScrollActive;

        protected WeakReference<SearchContract.Presenter> mPresenter;;

        public ResponseListAdapter() {

            mUiHandler = new Handler(Looper.getMainLooper());
            mIsInfinityScrollActive = true;
        }

        public void setPresenter(SearchContract.Presenter presenter) {
            mPresenter = new WeakReference<SearchContract.Presenter>(presenter);
        }

        public void setItem(List<T> list) {
            mDataset = list;
        }
        public void clearItem() {
            mDataset.clear();
        }

        public void setQuery(String query) {
            mKeyword = query;
        }

        @Override
        public int getItemCount() {

            if(mDataset == null) {
                return 0;
            }

            return mDataset.size();
        }

        @Override
        public void onBindViewHolder(VH holder, final int position) {

            if(position >= mDataset.size()) {
                throw new ArrayIndexOutOfBoundsException("ResponseListAdapter.onBindView() : invalid position : " + position + " // item size : " + mDataset.size());
            }

            onBindView(holder, position);

            if(mIsInfinityScrollActive && (position == getItemCount() - 1)) {

                if(mPresenter != null) {
                    SearchContract.Presenter presenter = mPresenter.get();
                    if(presenter != null) {

                        presenter.loadMoreQueryResult();;
                        //TODO: add Loading bar
                    }
                }
            }
        }

        public abstract void onBindView(VH holder, int position);

        public void setInfinityScroll(boolean isActive) {
            mIsInfinityScrollActive = isActive;
        }

    }

    protected SearchContract.Presenter mPresenter;

    protected RecyclerView mRecyclerView;
    protected ResponseListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_query_response, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclelistview_search_result);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = createLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = createResponseListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try{
            mPresenter = (SearchContract.Presenter)getActivity();
            mAdapter.setPresenter(mPresenter);
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement SearchContract.Presenter");
        }

        mAdapter.setQuery(mPresenter.getQuery());
        mAdapter.setItem(getResponseList());
    }

    @Override
    public void handleEmptyQueryResult() {

        mAdapter.clearItem();
        mAdapter.notifyDataSetChanged();

        String message = String.format(getString(R.string.guide_empty_query_response), mPresenter.getQuery());

        Util.showToastShort(getContext(), message);
    }

    @Override
    public void updateQueryResult() {

        mAdapter.setQuery(mPresenter.getQuery());
        mAdapter.setItem(getResponseList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void handleFinalQueryResult() {

        mAdapter.setInfinityScroll(false);
        Util.showToastLong(getContext(), getString(R.string.guide_final_query_response));
    }

    @Override
    public void showErrorMessage(String errorMessage) {

        Util.showToastShort(getContext(), errorMessage);
    }

    @Override
    public void clearQueryResult() {
        if(mAdapter != null) {
            mAdapter.clearItem();
            mAdapter.notifyDataSetChanged();
        }
    }

    public abstract RecyclerView.LayoutManager createLayoutManager();
    public abstract ResponseListAdapter createResponseListAdapter();
    public abstract List<T> getResponseList();
}
