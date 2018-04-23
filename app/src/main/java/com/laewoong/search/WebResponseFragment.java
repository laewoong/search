package com.laewoong.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laewoong.search.util.Util;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class WebResponseFragment extends Fragment {

    public static final String TAG = WebResponseFragment.class.getSimpleName();

    private static final String KEY_ITEM_LIST = "com.laewoong.search.WebResponseFragment.KEY_ITEM_LIST";
    private static final String KEY_QUERY = "com.laewoong.search.WebResponseFragment.KEY_QUERY";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinkedList<WebInfo> mItemList;

    public void setQuery(String query) {
        mAdapter.setQuery(query);
    }

    public void clearList() {
        mAdapter.clearItems();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "WebResponseFragment.onCreate() : " + this);

        setRetainInstance(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_response, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclelistview_search_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(TAG, "WebResponseFragment.onSaveInstanceState() : " + this);
        outState.putString(KEY_QUERY, mAdapter.getQuery());
        outState.putSerializable(KEY_ITEM_LIST, mItemList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "WebResponseFragment.onActivityCreated() : " + this);
        try{
            OnReachedListEndListener listner = (OnReachedListEndListener)getActivity();
            mAdapter.setOnReachedListEndListener(listner);
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnReachedListEndListener");
        }

        if (savedInstanceState != null) {
            LinkedList<WebInfo> list = (LinkedList<WebInfo>) savedInstanceState.getSerializable(KEY_ITEM_LIST);
            mItemList = list;

            String query = savedInstanceState.getString(KEY_QUERY);
            if(query != null) {
                mAdapter.setQuery(query);
            }

        } else {
            // specify an adapter
            mItemList = new LinkedList<WebInfo>();
        }

        mAdapter.setItem(mItemList);
    }

    public void addItems(List<WebInfo> list) {
        mAdapter.addItem(list);
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<WebInfo> mDataset;
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
            public TextView mTitleTextView;
            public TextView mDescTextView;
            public TextView mLinkTextView;
            public ViewHolder(View v) {
                super(v);
                mTitleTextView = (TextView) v.findViewById(R.id.textview_title);
                mDescTextView = (TextView) v.findViewById(R.id.textview_desc);
                mLinkTextView = (TextView) v.findViewById(R.id.textview_link);
            }
        }

        public void addItem(List<WebInfo> infoList) {
            if(mDataset != null) {
                mDataset.addAll(infoList);
                notifyDataSetChanged();
            }
        }

        public void clearItems() {
            mDataset.clear();
            notifyDataSetChanged();
        }

        public void setItem(List<WebInfo> list) {
            mDataset = list;
        }

        public MyAdapter() {

        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<WebInfo> myDataset) {
            mDataset = myDataset;
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
                    .inflate(R.layout.item_web_response, parent, false);
            // set the view's size, margins, paddings and layout parameters

            MyAdapter.ViewHolder vh = new MyAdapter.ViewHolder(v);
            return vh;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final WebInfo info = mDataset.get(position);
            if(info == null) {
                //TODO throw exception
                return;
            }

            //holder.mTitleTextView.setText(Util.makeKeywordToBold(mKeyword, info.getTitle()));
            holder.mTitleTextView.setText(Util.fromHtml(info.getTitle()));
            holder.mDescTextView.setText(Util.fromHtml(info.getDescription()));
            holder.mLinkTextView.setText(info.getLink());
            Linkify.addLinks(holder.mLinkTextView, Linkify.WEB_URLS);


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
