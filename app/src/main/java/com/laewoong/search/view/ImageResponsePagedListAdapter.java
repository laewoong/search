package com.laewoong.search.view;

import android.arch.paging.PagedListAdapter;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Singleton
public class ImageResponsePagedListAdapter extends PagedListAdapter<ImageInfo, ImageResponsePagedListAdapter.ViewHolder> {

    private Context mContext;
    private OnSelectedItemListener onSelectedItemListener;

    @Inject
    public ImageResponsePagedListAdapter(Context context) {
        super(ImageInfo.DIFF_CALLBACK);
        this.mContext = context;
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

                if(onSelectedItemListener != null) {
                    onSelectedItemListener.onSelectedItem(position);
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

                Observable.just(mContext.getString(R.string.guide_internal_error))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(msg -> {
                            Util.showToastLong(mContext.getApplicationContext(), msg);
                        });
            }
        });
    }

    public void setOnSelectedItemListener(OnSelectedItemListener listener) {
        onSelectedItemListener = listener;
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