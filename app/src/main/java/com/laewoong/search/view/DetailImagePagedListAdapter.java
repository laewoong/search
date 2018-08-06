package com.laewoong.search.view;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.laewoong.search.R;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.util.Util;
import com.laewoong.search.viewmodel.SearchViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class DetailImagePagedListAdapter extends PagedListAdapter<ImageInfo, DetailImagePagedListAdapter.ViewHolder> {

    private Context mContext;

    public DetailImagePagedListAdapter(Context context, SearchViewModel viewModel) {
        super(ImageInfo.DIFF_CALLBACK);
        this.mContext = context;
    }

    @Override
    public DetailImagePagedListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_image, parent, false);

        DetailImagePagedListAdapter.ViewHolder vh = new DetailImagePagedListAdapter.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ImageInfo info = getItem(position);

        String url = info.getLink();

        holder.mLoadingProgressbar.setVisibility(View.VISIBLE);

        Picasso.with(mContext).load(url).priority(Picasso.Priority.HIGH).into(holder.mDetailImageView, new Callback() {
            @Override
            public void onSuccess() {

                holder.mLoadingProgressbar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

                Observable.just(mContext.getString(R.string.guide_internal_error))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(msg -> {
                            Util.showToastLong(mContext.getApplicationContext(), msg);
                        });
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mDetailImageView;
        public ProgressBar mLoadingProgressbar;

        public ViewHolder(View v) {
            super(v);
            mDetailImageView = (ImageView)v.findViewById(R.id.imageview_detail);
            mLoadingProgressbar = (ProgressBar)v.findViewById(R.id.progressbar_original_image_loading);

            mDetailImageView.setOnTouchListener(new ImageMatrixTouchHandler(mDetailImageView.getContext()));
        }
    }
}