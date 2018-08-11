package com.laewoong.search.view;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
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
import com.laewoong.search.R;
import com.laewoong.search.databinding.FragmentImageDetailBinding;
import com.laewoong.search.viewmodel.SearchViewModel;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class DetailImageFragment extends Fragment {

    public static final String TAG = DetailImageFragment.class.getSimpleName();
    public static final String KEY_POSITION = "com.laewoong.search.view.DetailImageFragment.KEY_POSITION";

    private SearchViewModel searchViewModel;
    private FragmentImageDetailBinding binding;
    private RecyclerView.LayoutManager mLayoutManager;
    private DetailImagePagedListAdapter mAdapter;
    private SnapHelper mSnapHelper;
    private int mPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_detail, container, false);
        binding.setLifecycleOwner(this);

        binding.recyclelistviewDetailImage.getRecycledViewPool().setMaxRecycledViews(0, 0);
        binding.recyclelistviewDetailImage.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclelistviewDetailImage.setLayoutManager(mLayoutManager);

        mAdapter = new DetailImagePagedListAdapter(getContext());
        binding.recyclelistviewDetailImage.setAdapter(mAdapter);

        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(binding.recyclelistviewDetailImage);

        binding.recyclelistviewDetailImage.addOnScrollListener(new RecyclerView.OnScrollListener() {

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

        searchViewModel.getImageInfoList().observe(this, pagedList -> {

            ((DetailImagePagedListAdapter)(binding.recyclelistviewDetailImage.getAdapter())).submitList(pagedList);
            setButtonVisibleState();
        });

        mPosition = getArguments().getInt(KEY_POSITION, 0);
        binding.recyclelistviewDetailImage.scrollToPosition(mPosition);

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, mPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.recyclelistviewDetailImage.scrollToPosition(mPosition);
        binding.buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if date reached the first
                if(mPosition -1 < 0 ) {
                    return;
                }

                binding.recyclelistviewDetailImage.smoothScrollToPosition(mPosition -1);
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if data reaches the end
                if(mPosition + 1 >= mAdapter.getItemCount()) {
                    return;
                }

                binding.recyclelistviewDetailImage.smoothScrollToPosition(mPosition + 1);
            }
        });

        if(savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(KEY_POSITION);
        }
    }

    private void setButtonVisibleState() {

        if(mAdapter.getItemCount() <= 1) {
            binding.buttonPrev.setVisibility(View.INVISIBLE);
            binding.buttonNext.setVisibility(View.INVISIBLE);
        }
        else if(mPosition == 0) { // if first item
            binding.buttonPrev.setVisibility(View.INVISIBLE);
            binding.buttonNext.setVisibility(View.VISIBLE);
        }
        else if(mPosition == (mAdapter.getItemCount()-1)) { // if last item
            binding.buttonPrev.setVisibility(View.VISIBLE);
            binding.buttonNext.setVisibility(View.INVISIBLE);
        }
        else {
            binding.buttonPrev.setVisibility(View.VISIBLE);
            binding.buttonNext.setVisibility(View.VISIBLE);
        }
    }
}
