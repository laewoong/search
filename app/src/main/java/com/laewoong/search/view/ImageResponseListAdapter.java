package com.laewoong.search.view;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.laewoong.search.R;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.util.Util;
import com.laewoong.search.viewmodel.SearchViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageResponseListAdapter extends ResponseListAdapter<ImageResponseListAdapter.ViewHolder, ImageInfo>{

    private Context mContext;
    private OnSelectedItemListener mOnSelectedItemListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mThumbnailImageView;
        public TextView mTitleTextView;

        public ViewHolder(View v) {
            super(v);
            mThumbnailImageView = (ImageView)v.findViewById(R.id.imageview_thumbnail);
            mTitleTextView = (TextView) v.findViewById(R.id.textview_title);
        }
    }

    public ImageResponseListAdapter(Context context, SearchViewModel viewModel) {
        super(viewModel);
        mContext = context;
    }

    public void setOnSelectedItemListener(OnSelectedItemListener listener) {
        mOnSelectedItemListener = listener;
    }

    @Override
    public ImageResponseListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_response, parent, false);
        return new ImageResponseListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindView(ViewHolder holder, final int position) {

        final ImageInfo info = mDataset.get(position);

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
}