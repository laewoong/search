package com.laewoong.search.view;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laewoong.search.R;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageResponsePagedListAdapter extends PagedListAdapter<ImageInfo, ImageResponsePagedListAdapter.ViewHolder> {

    private Context mContext;
    private OnSelectedItemListener mOnSelectedItemListener;
    protected Handler mUiHandler;

    public ImageResponsePagedListAdapter(Context context) {
        super(ImageInfo.DIFF_CALLBACK);
        this.mContext = context;
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public ImageResponsePagedListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_response, parent, false);
        return new ImageResponsePagedListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageResponsePagedListAdapter.ViewHolder holder, final int position) {

        final ImageInfo info = getItem(position);

        holder.mTitleTextView.setText(Util.fromHtml(info.getTitle()));

        holder.mThumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mOnSelectedItemListener != null) {
                    mOnSelectedItemListener.onSelectedItem(position);
                }
            }
        });

        String url = info.getThumbnail();

        Picasso.with(mContext).load(url).fit().centerCrop().into(holder.mThumbnailImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Util.showToastLong(mContext.getApplicationContext(), mContext.getString(R.string.guide_internal_error));
                    }
                });
            }
        });
    }

    public void setOnSelectedItemListener(OnSelectedItemListener listener) {
        mOnSelectedItemListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mThumbnailImageView;
        public TextView mTitleTextView;

        public ViewHolder(View v) {
            super(v);
            mThumbnailImageView = (ImageView)v.findViewById(R.id.imageview_thumbnail);
            mTitleTextView = (TextView) v.findViewById(R.id.textview_title);
        }
    }
}