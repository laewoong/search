package com.laewoong.search.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.R;
import com.laewoong.search.util.Util;
import com.laewoong.search.viewmodel.SearchViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class DetailImageFragment extends Fragment {

    public static final String TAG = DetailImageFragment.class.getSimpleName();

    public static final String KEY_POSITION = "com.laewoong.search.view.DetailImageFragment.KEY_POSITION";

    private Button mPrevButton;
    private Button mNextButton;
    private SnapHelper mSnapHelper;

    private int mPosition = 0;

    private SearchViewModel searchViewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DetailImagePagedListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);

        searchViewModel.getSelectedDetailImagePosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                mPosition = position;
                mRecyclerView.scrollToPosition(mPosition);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_image_detail, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclelistview_detail_image);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DetailImagePagedListAdapter(getContext(), searchViewModel);
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

        //mPosition = getArguments().getInt(KEY_POSITION);

        setButtonVisibleState();

        searchViewModel.getImageInfoList().observe(this, pagedList -> {

            ((DetailImagePagedListAdapter)(mRecyclerView.getAdapter())).submitList(pagedList);
        });

        mPosition = searchViewModel.getSelectedDetailImagePosition().getValue();
        mRecyclerView.scrollToPosition(mPosition);

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
}
