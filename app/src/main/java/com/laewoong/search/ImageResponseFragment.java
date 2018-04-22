package com.laewoong.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laewoong.search.util.Util;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class ImageResponseFragment extends Fragment {

    public static final String TAG = ImageResponseFragment.class.getSimpleName();

    private static final String KEY_ITEM_LIST = "com.laewoong.search.WebResponseFragment.KEY_ITEM_LIST";
    private static final String KEY_QUERY = "com.laewoong.search.WebResponseFragment.KEY_QUERY";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinkedList<ImageInfo> mItemList;

    public void setQuery(String query) {
        mAdapter.setQuery(query);
    }

    public void clearList() {
        mAdapter.clearItems();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_response, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclelistview_search_result);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new StaggeredGridLayoutManager(Constants.DEFAULT_GRID_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager = new GridLayoutManager(getContext().getApplicationContext(), Constants.DEFAULT_GRID_SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getContext().getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_QUERY, mAdapter.getQuery());
        outState.putSerializable(KEY_ITEM_LIST, mItemList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try{
            OnReachedListEndListener listner = (OnReachedListEndListener)getActivity();
            mAdapter.setOnReachedListEndListener(listner);
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnReachedListEndListener");
        }

        if (savedInstanceState != null) {
            LinkedList<ImageInfo> list = (LinkedList<ImageInfo>) savedInstanceState.getSerializable(KEY_ITEM_LIST);
            mItemList = list;

            String query = savedInstanceState.getString(KEY_QUERY);
            if(query != null) {
                mAdapter.setQuery(query);
            }

        } else {
            // specify an adapter
            mItemList = new LinkedList<ImageInfo>();
        }

        mAdapter.setItem(mItemList);
    }

    public void addItems(List<ImageInfo> list) {
        mAdapter.addItem(list);
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context mContext;
        private List<ImageInfo> mDataset;
        private String mKeyword;

        private WeakReference<OnReachedListEndListener> mOnReachedListEndListener;

        public void setOnReachedListEndListener(OnReachedListEndListener listener) {
            mOnReachedListEndListener = new WeakReference<OnReachedListEndListener>(listener);
        }

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

        public void addItem(List<ImageInfo> infoList) {
            if(mDataset != null) {
                mDataset.addAll(infoList);
                notifyDataSetChanged();
            }
        }

        public void clearItems() {
            mDataset.clear();
            notifyDataSetChanged();
        }

        public void setItem(List<ImageInfo> list) {
            mDataset = list;
        }

        public MyAdapter(Context context) {
            this.mContext = context;

        }

        public void setQuery(String query) {
            mKeyword = query;
        }
        public String getQuery() { return mKeyword; }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_response, parent, false);
            // set the view's size, margins, paddings and layout parameters

            MyAdapter.ViewHolder vh = new MyAdapter.ViewHolder(v);
            return vh;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final ImageInfo info = mDataset.get(position);
            if(info == null) {
                //TODO throw exception
                return;
            }

            holder.mTitleTextView.setText(Util.makeKeywordToBold(mKeyword, info.getTitle()));

            int width = Integer.parseInt(info.getSizewidth());
            int height = Integer.parseInt(info.getSizeheight());
            Log.i("fff", "======= w(" + width +") h(" + height +")");
            String url = info.getThumbnail();

            Picasso.with(mContext).load(url).into(holder.mThumbnailImageView);

            if(position == getItemCount() - 1) {

                if(mOnReachedListEndListener != null) {
                    OnReachedListEndListener listener = mOnReachedListEndListener.get();
                    if(listener != null) {
                        Log.i(TAG , "======= Called Listenr!!! : " + mKeyword);
                        //TODO : validate list size;
                        listener.onReachedListEndListener(mKeyword);

                        //TODO: add Loading bar
                        //TODO: network check. timeout.
                    }
                }

            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
