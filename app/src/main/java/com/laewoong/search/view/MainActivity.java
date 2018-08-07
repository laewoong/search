package com.laewoong.search.view;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import com.laewoong.search.R;
import com.laewoong.search.databinding.ActivityMainBinding;
import com.laewoong.search.util.BackPressCloseHandler;
import com.laewoong.search.util.NetworkState;
import com.laewoong.search.viewmodel.SearchViewModel;
import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private SearchViewModel searchViewModel;
    private ActivityMainBinding binding;

    private BackPressCloseHandler mBackPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewmodel(searchViewModel);
        binding.setLifecycleOwner(this);

        mBackPressCloseHandler = new BackPressCloseHandler(this);

        init();
        observeLiveData();
    }

    private void init() {

        // Init SearchView
        binding.searchviewQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                query = query.trim();

                if(query.isEmpty()) {
                    return false;
                }

                binding.searchviewQuery.clearFocus();
                searchViewModel.updatedQuery(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {

            searchViewModel.invalidateQuery();
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        binding.searchviewQuery.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        binding.searchviewQuery.setIconifiedByDefault(false);
        binding.searchviewQuery.setSubmitButtonEnabled(true);
        binding.searchviewQuery.setQueryRefinementEnabled(true);

        binding.recyclerviewQueryResponse.getRecycledViewPool().setMaxRecycledViews(0, 0);
        binding.recyclerviewQueryResponse.setHasFixedSize(true);

        // Init tab view's hint color
        SegmentedGroup tabGroup = (SegmentedGroup)findViewById(R.id.container_tab);
        tabGroup.setTintColor(Color.parseColor("#F06292"));
    }

    private void observeLiveData() {

        searchViewModel.getQuery().observe(this, query -> {

            if((query == null) || query.isEmpty()) {

                return;
            }

            switch( searchViewModel.getCurSelectedTab().getValue() ) {
                case WEB:
                    searchViewModel.queryWeb(query);
                    break;
                case IMAGE:
                    searchViewModel.queryImage(query);
                    break;
            }
        });

        searchViewModel.getCurSelectedTab().observe(this, tab -> {

            binding.searchviewQuery.clearFocus();
            String query = binding.searchviewQuery.getQuery().toString();

            switch (tab) {
                case WEB:
                    binding.recyclerviewQueryResponse.setAdapter(new WebResponsePagedListAdapter());
                    binding.recyclerviewQueryResponse.setLayoutManager(new LinearLayoutManager(this));

                    if((query != null) && (query.isEmpty() == false)) {
                        searchViewModel.queryWeb(query);
                    }
                    break;
                case IMAGE:
                    ImageResponsePagedListAdapter adapter = new ImageResponsePagedListAdapter(getApplicationContext(), searchViewModel);
                    binding.recyclerviewQueryResponse.setAdapter(adapter);
                    binding.recyclerviewQueryResponse.setLayoutManager(new GridLayoutManager(this, ViewConstants.DEFAULT_GRID_SPAN_COUNT));

                    if((query != null) && (query.isEmpty() == false)) {
                        searchViewModel.queryImage(query);
                    }
                    break;
            }
        });

        searchViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {

                if (networkState.getStatus() == NetworkState.Status.FAILED) {
                    Toast.makeText(MainActivity.this, networkState.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchViewModel.getWebInfoList().observe(this, pagedList -> {

            if(searchViewModel.getCurSelectedTab().getValue() != ViewConstants.TAB.WEB) {
                return;
            }

            ((WebResponsePagedListAdapter)(binding.recyclerviewQueryResponse.getAdapter())).submitList(pagedList);
        });

        searchViewModel.getImageInfoList().observe(this, pagedList -> {

            if(searchViewModel.getCurSelectedTab().getValue() != ViewConstants.TAB.IMAGE) {
                return;
            }

            ((ImageResponsePagedListAdapter)(binding.recyclerviewQueryResponse.getAdapter())).submitList(pagedList);
        });

        searchViewModel.getSelectedDetailImagePosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {

                FragmentManager fm = getSupportFragmentManager();
                DetailImageFragment mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

                if (mDetailImageFragment == null) {

                    mDetailImageFragment = new DetailImageFragment();
                }
                else if(mDetailImageFragment.isAdded()){
                    return;
                }

                Bundle b = new Bundle();
                b.putInt(DetailImageFragment.KEY_POSITION, position);
                mDetailImageFragment.setArguments(b);

                fm.beginTransaction().add(R.id.container_root, mDetailImageFragment, DetailImageFragment.TAG).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            mBackPressCloseHandler.onBackPressed();
        }
    }
}