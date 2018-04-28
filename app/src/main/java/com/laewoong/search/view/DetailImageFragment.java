package com.laewoong.search.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.R;
import com.laewoong.search.SearchContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class DetailImageFragment extends ResponseFragment<ImageInfo> {

    public static final String TAG = DetailImageFragment.class.getSimpleName();

    public static final String KEY_POSITION = "com.laewoong.search.view.DetailImageFragment.KEY_POSITION";

    private Button mPrevButton;
    private Button mNextButton;
    private SnapHelper mSnapHelper;

    private int mPosition = 0;

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
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DetailImageListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    View centerView = mSnapHelper.findSnapView(mLayoutManager);
                    mPosition = mLayoutManager.getPosition(centerView);

                    setButtonVisibleState();
                }
            }
        });

        mPrevButton = (Button)view.findViewById(R.id.button_prev);
        mNextButton = (Button)view.findViewById(R.id.button_next);

        mPosition = getArguments().getInt(KEY_POSITION);

        setButtonVisibleState();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, mPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try{
            mPresenter = (SearchContract.Presenter)getActivity();
            mAdapter.setPresenter(mPresenter);
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement SearchContract.Presenter");
        }

        mAdapter.setQuery(mPresenter.getQuery());
        mAdapter.setItem(mPresenter.getImageQueryResponseList());

        mRecyclerView.scrollToPosition(mPosition);

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if date reached the first
                if(mPosition -1 < 0 ) {
                    return;
                }

                mRecyclerView.smoothScrollToPosition(mPosition -1);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if data reaches the end
                if(mPosition + 1 >= mAdapter.getItemCount()) {
                    return;
                }

                mRecyclerView.smoothScrollToPosition(mPosition + 1);
            }
        });

        if(savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(KEY_POSITION);
        }

        setButtonVisibleState();
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public ResponseListAdapter createResponseListAdapter() {
        return new DetailImageListAdapter(getContext());
    }

    @Override
    public List<ImageInfo> getResponseList() {
        return mPresenter.getImageQueryResponseList();
    }

    private void setButtonVisibleState() {

        if(mAdapter.getItemCount() <= 1) {
            mPrevButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.INVISIBLE);
        }
        else if(mPosition == 0) { // if first item
            mPrevButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        }
        else if(mPosition == (mAdapter.getItemCount()-1)) { // if last item
            mPrevButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.INVISIBLE);
        }
        else {
            mPrevButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        }
    }


    public static class DetailImageListAdapter extends ResponseListAdapter<DetailImageListAdapter.ViewHolder, ImageInfo> {

        private Context mContext;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mDetailImageView;
            public ProgressBar mLoadingProgressbar;

            public ViewHolder(View v) {
                super(v);
                mDetailImageView = (ImageView)v.findViewById(R.id.imageview_detail);
                mLoadingProgressbar = (ProgressBar)v.findViewById(R.id.progressbar_original_image_loading);

                mDetailImageView.setOnTouchListener(new ImageMatrixTouchHandler(mDetailImageView.getContext()));
            }
        }

        public DetailImageListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public DetailImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_detail_image, parent, false);

            DetailImageListAdapter.ViewHolder vh = new DetailImageListAdapter.ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindView(final ViewHolder holder, int position) {

            if(position >= mDataset.size()) {
                throw new ArrayIndexOutOfBoundsException("DetailImageListAdapter.onBindView() : invalid position : " + position + " // item size : " + mDataset.size());
            }

            final ImageInfo info = mDataset.get(position);

            String url = info.getLink();

            holder.mLoadingProgressbar.setVisibility(View.VISIBLE);

            Picasso.with(mContext).load(url).priority(Picasso.Priority.HIGH).into(holder.mDetailImageView, new Callback() {
                @Override
                public void onSuccess() {

                    holder.mLoadingProgressbar.setVisibility(View.GONE);
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
