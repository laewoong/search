package com.laewoong.search.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.R;
import com.laewoong.search.util.Util;
import com.laewoong.search.viewmodel.SearchViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class ImageResponseFragment extends ResponseFragment<ImageInfo> implements OnSelectedItemListener {

    public static final String TAG = ImageResponseFragment.class.getSimpleName();

    private SearchViewModel searchViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        searchViewModel.getQuery().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String query) {
                searchViewModel.queryImage(query);
            }
        });

        searchViewModel.getImageInfoList().observe(this, new Observer<List<ImageInfo>>() {
            @Override
            public void onChanged(@Nullable List<ImageInfo> imageInfos) {

                mAdapter.setItem(imageInfos);
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

        mAdapter.setItem(searchViewModel.getImageInfoList().getValue());
        mAdapter.notifyDataSetChanged();

        return view;
    }

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
    public void onSelectedItem(int position) {

        searchViewModel.getSelectedDetailImagePosition().setValue(position);

        final FragmentActivity activity = getActivity();

        if(activity != null) {

            FragmentManager fm = activity.getSupportFragmentManager();
            DetailImageFragment mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

            if (mDetailImageFragment == null) {

                mDetailImageFragment = new DetailImageFragment();
            }

//            if(mDetailImageFragment.isAdded())
//            {
//                //fm.beginTransaction().show(mDetailImageFragment);
//                return; //or return false/true, based on where you are calling from
//            }

            Bundle b = new Bundle();
            b.putInt(DetailImageFragment.KEY_POSITION, position);
            mDetailImageFragment.setArguments(b);

            fm.beginTransaction().add(R.id.container_root, mDetailImageFragment, DetailImageFragment.TAG).addToBackStack(null).commit();
        }
    }

    public static class ImageResponseListAdapter extends ResponseFragment.ResponseListAdapter<ImageResponseListAdapter.ViewHolder, ImageInfo>{

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

        public ImageResponseListAdapter(Context context) {
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
}
