package com.laewoong.search.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.R;
import com.laewoong.search.util.Util;
import com.squareup.picasso.Callback;
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
        return new GridLayoutManager(getContext().getApplicationContext(), ViewConstants.DEFAULT_GRID_SPAN_COUNT);
    }

    @Override
    public ResponseListAdapter createResponseListAdapter() {

        ImageResponseListAdapter apdater = new ImageResponseFragment.ImageResponseListAdapter(getContext().getApplicationContext());
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

    public static class ImageResponseListAdapter extends ResponseFragment.ResponseListAdapter<ImageResponseListAdapter.ViewHolder, ImageInfo>{


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

        public ImageResponseListAdapter(Context context) {
            mContext = context;
        }

        public void setOnSelectedItemListener(OnSelectedItemListener listener) {
            mOnSelectedItemListener = listener;
        }
        // Create new views (invoked by the layout manager)
        @Override
        public ImageResponseListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_response, parent, false);
            return new ImageResponseListAdapter.ViewHolder(v);
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

            Picasso.with(mContext).load(url).fit().centerCrop().into(holder.mThumbnailImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(mContext.getApplicationContext(), mContext.getString(R.string.guide_internal_error), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

}
