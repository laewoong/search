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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import com.laewoong.search.R;
import com.laewoong.search.databinding.ActivityMainBinding;
import com.laewoong.search.util.BackPressCloseHandler;
import com.laewoong.search.util.NetworkState;
import com.laewoong.search.viewmodel.SearchViewModel;
import javax.inject.Inject;
import javax.inject.Named;
import dagger.android.AndroidInjection;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private SearchViewModel searchViewModel;
    private ActivityMainBinding binding;

    private BackPressCloseHandler mBackPressCloseHandler;

    private PublishSubject<String> querySubject;

    @Inject
    WebResponsePagedListAdapter webResponsePagedListAdapter;
    @Inject
    ImageResponsePagedListAdapter imageResponsePagedListAdapter;

    @Inject
    @Named("web_list_layout_manger")
    RecyclerView.LayoutManager webResponseListLayoutManager;
    @Inject
    @Named("image_list_layout_manger")
    RecyclerView.LayoutManager imageResponseListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
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

        initAdapter();

        querySubject = PublishSubject.create();

        Observable<String> queryObservable = querySubject
                .filter(query -> ((query != null) && (query.isEmpty() == false)))
                .doOnNext(query -> { binding.searchviewQuery.clearFocus(); });

        queryObservable.filter(notUsed -> (binding.buttonWeb.isChecked()))
                    .subscribe(query -> {

                        searchViewModel.queryWeb(query);
                        binding.recyclerviewQueryResponse.setAdapter(webResponsePagedListAdapter);
                        binding.recyclerviewQueryResponse.setLayoutManager(webResponseListLayoutManager);
                    });

        queryObservable.filter(notUsed -> (binding.buttonImage.isChecked()))
                    .subscribe(query -> {

                        searchViewModel.queryImage(query);
                        binding.recyclerviewQueryResponse.setAdapter(imageResponsePagedListAdapter);
                        binding.recyclerviewQueryResponse.setLayoutManager(imageResponseListLayoutManager);
                    });

        RxView.clicks(binding.buttonWeb)
                .subscribe(view -> {
                    searchViewModel.selectedTabButton(ViewConstants.TAB.WEB);
                    querySubject.onNext(binding.searchviewQuery.getQuery().toString());

                });

        RxView.clicks(binding.buttonImage)
                .subscribe(view -> {
                    searchViewModel.selectedTabButton(ViewConstants.TAB.IMAGE);
                    querySubject.onNext(binding.searchviewQuery.getQuery().toString());

                });

        querySubject.onNext(binding.searchviewQuery.getQuery().toString());

        // Init SearchView
        binding.searchviewQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                querySubject.onNext(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {

            querySubject.onNext(binding.searchviewQuery.getQuery().toString());
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

    private void initAdapter() {

        switch(searchViewModel.getCurSelectedTab().getValue()) {
            case WEB:
                binding.buttonWeb.setChecked(true);
                break;
            case IMAGE:
                binding.buttonImage.setChecked(true);
        }

        if(binding.buttonWeb.isChecked()) {
            binding.recyclerviewQueryResponse.setAdapter(webResponsePagedListAdapter);
            binding.recyclerviewQueryResponse.setLayoutManager(webResponseListLayoutManager);
        }
        else if(binding.buttonImage.isChecked()) {
            binding.recyclerviewQueryResponse.setAdapter(imageResponsePagedListAdapter);
            binding.recyclerviewQueryResponse.setLayoutManager(imageResponseListLayoutManager);
        }
    }

    private void observeLiveData() {

        searchViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {

                if (networkState.getStatus() == NetworkState.Status.FAILED) {
                    Toast.makeText(MainActivity.this, networkState.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchViewModel.getWebInfoList().observe(this, pagedList -> {
            webResponsePagedListAdapter.submitList(pagedList);
        });

        searchViewModel.getImageInfoList().observe(this, pagedList -> {
            imageResponsePagedListAdapter.submitList(pagedList);
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