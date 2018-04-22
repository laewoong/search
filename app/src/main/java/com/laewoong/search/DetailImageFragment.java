package com.laewoong.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class DetailImageFragment extends Fragment {

    public static final String TAG = DetailImageFragment.class.getSimpleName();

    public static final String KEY_POSITION = "com.laewoong.search.DetailImageFragment.KEY_POSITION";
    public static final String KEY_ITEM_LIST = "com.laewoong.search.DetailImageFragment.KEY_ITEM_LIST";
    public static final String KEY_QUERY = "com.laewoong.search.DetailImageFragment.KEY_QUERY";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinkedList<ImageInfo> mItemList;

    private Button mPrevButton;
    private Button mNextButton;
    private SnapHelper mSnapHelper;

    private int mPosition = 0;

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
        View view = inflater.inflate(R.layout.fragment_image_detail, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclelistview_detail_image);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new StaggeredGridLayoutManager(Constants.DEFAULT_GRID_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getContext().getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mRecyclerView);

        mPosition = getArguments().getInt(KEY_POSITION);
        mItemList = (LinkedList<ImageInfo>)getArguments().getSerializable(KEY_ITEM_LIST);
        if(mItemList != null) {
            mAdapter.setItem(new LinkedList<ImageInfo>());
            mAdapter.addItem(mItemList);
            mRecyclerView.scrollToPosition(mPosition);
        }



        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    View centerView = mSnapHelper.findSnapView(mLayoutManager);
                    mPosition = mLayoutManager.getPosition(centerView);
                }
            }
        });

        mPrevButton = (Button)view.findViewById(R.id.button_prev);
        mNextButton = (Button)view.findViewById(R.id.button_next);

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mPosition -1 < 0 ) {
                    return;
                }
                mRecyclerView.smoothScrollToPosition(mPosition -1);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mPosition + 1 >= mItemList.size() ) {
                    //TODO : load more data
                    return;
                }
                mRecyclerView.smoothScrollToPosition(mPosition + 1);
            }
        });

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

            OnSelectedThumbnailListener onSelectedThumbnailListener = (OnSelectedThumbnailListener)getActivity();
            mAdapter.setOnSelectedThumbnailListener(onSelectedThumbnailListener);

        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnReachedListEndListener");
        }

        if(mItemList == null) {
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
    }

    public void addItems(List<ImageInfo> list) {
        mAdapter.addItem(list);
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context mContext;
        private List<ImageInfo> mDataset;
        private String mKeyword;
        private int mPosition;

        private WeakReference<OnReachedListEndListener> mOnReachedListEndListener;
        private WeakReference<OnSelectedThumbnailListener> mOnSelectedThumbnailListener;

        public void setOnReachedListEndListener(OnReachedListEndListener listener) {
            mOnReachedListEndListener = new WeakReference<OnReachedListEndListener>(listener);
        }

        public void setOnSelectedThumbnailListener(OnSelectedThumbnailListener listener) {
            mOnSelectedThumbnailListener = new WeakReference<OnSelectedThumbnailListener>(listener);;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mDetailImageView;

            public ViewHolder(View v) {
                super(v);
                mDetailImageView = (ImageView)v.findViewById(R.id.imageview_detail);
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
                    .inflate(R.layout.item_detail_image, parent, false);
            // set the view's size, margins, paddings and layout parameters

            MyAdapter.ViewHolder vh = new MyAdapter.ViewHolder(v);
            return vh;
        }

        public int getPosition() {
            return mPosition;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            mPosition = position;
            final ImageInfo info = mDataset.get(position);
            if(info == null) {
                //TODO throw exception
                return;
            }

//            holder.mThumbnailImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(mOnSelectedThumbnailListener != null) {
//                        OnSelectedThumbnailListener listener = mOnSelectedThumbnailListener.get();
//                        if(listener != null) {
//                            //TODO : validate list size;
//                            listener.onSelectedThumbnail(mDataset, position);
//
//                            //TODO: add Loading bar
//                            //TODO: network check. timeout.
//                        }
//                    }
//
//                }
//            });

            String url = info.getLink();
            Picasso.with(mContext).load(url).into(holder.mDetailImageView);

            if(position == getItemCount() - 1) {

                if(mOnReachedListEndListener != null) {
                    OnReachedListEndListener listener = mOnReachedListEndListener.get();
                    if(listener != null) {
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
