package com.laewoong.search.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.R;
import com.laewoong.search.viewmodel.SearchViewModel;
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

        observeViewModel();
    }

    private void observeViewModel() {

        searchViewModel.getQuery().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String query) {
                searchViewModel.queryImage(query);
                mRecyclerView.scrollToPosition(0);
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

        ImageResponseListAdapter apdater = new ImageResponseListAdapter(getContext().getApplicationContext(), searchViewModel);
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

            Bundle b = new Bundle();
            b.putInt(DetailImageFragment.KEY_POSITION, position);
            mDetailImageFragment.setArguments(b);

            fm.beginTransaction().add(R.id.container_root, mDetailImageFragment, DetailImageFragment.TAG).addToBackStack(null).commit();
        }
    }
}
