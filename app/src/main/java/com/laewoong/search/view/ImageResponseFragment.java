package com.laewoong.search.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laewoong.search.util.Constants;
import com.laewoong.search.model.ImageInfo;
import com.laewoong.search.R;
import com.laewoong.search.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class ImageResponseFragment extends ResponseFragment<ImageInfo> implements OnSelectedItemListener {

    public static final String TAG = ImageResponseFragment.class.getSimpleName();

    //private static final String KEY_ITEM_LIST = "com.laewoong.search.view.ImageResponseFragment.KEY_ITEM_LIST";
    //private static final String KEY_QUERY = "com.laewoong.search.view.ImageResponseFragment.KEY_QUERY";

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(getContext().getApplicationContext(), Constants.DEFAULT_GRID_SPAN_COUNT);
    }

    @Override
    public ResponseListAdapter createResponseListAdapter() {

        MyAdapter apdater = new ImageResponseFragment.MyAdapter(getContext().getApplicationContext());
        apdater.setOnSelectedItemListener(this);
        return apdater;
    }

    @Override
    public List<ImageInfo> getResponseList() {
        return mPresenter.getImageQueryResponseList();
    }

    @Override
    public void onSelectedItem(int position) {
        mPresenter.onSelectedThumbnail(position);
    }

    public static class MyAdapter extends ResponseFragment.ResponseListAdapter<MyAdapter.ViewHolder, ImageInfo>{


        private Context mContext;
        private OnSelectedItemListener mOnSelectedItemListener;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mThumbnailImageView;
            public TextView mTitleTextView;

            public ViewHolder(View v) {
                super(v);
                mThumbnailImageView = (ImageView)v.findViewById(R.id.imageview_thumbnail);
                mTitleTextView = (TextView) v.findViewById(R.id.textview_title);
            }
        }

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void setOnSelectedItemListener(OnSelectedItemListener listener) {
            mOnSelectedItemListener = listener;
        }
        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_response, parent, false);
            return new MyAdapter.ViewHolder(v);
        }

        @Override
        public void onBindView(ViewHolder holder, final int position) {

            final ImageInfo info = mDataset.get(position);
            if(info == null) {
                //TODO throw exception

                return;
            }

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

            Picasso.with(mContext).load(url).into(holder.mThumbnailImageView);
        }
    }

}
