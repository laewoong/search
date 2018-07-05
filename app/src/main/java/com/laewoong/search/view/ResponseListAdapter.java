package com.laewoong.search.view;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class ResponseListAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    protected List<T> mDataset;
    protected String mKeyword;
    protected Handler mUiHandler;
    protected boolean mIsInfinityScrollActive;

    public ResponseListAdapter() {

        mUiHandler = new Handler(Looper.getMainLooper());
        mIsInfinityScrollActive = true;
    }

    public void setItem(List<T> list) {
        mDataset = list;
    }
    public void clearItem() {
        if(mDataset == null) {
            return;
        }
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

//                if(mController != null) {
//                    SearchContract.Controller controller = mController.get();
//                    if(controller != null) {
//
//                        controller.loadMoreQueryResult();;
//                        //TODO: add Loading bar
//                    }
//                }
        }
    }

    public abstract void onBindView(VH holder, int position);

    public void setInfinityScroll(boolean isActive) {
        mIsInfinityScrollActive = isActive;
    }

}