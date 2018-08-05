package com.laewoong.search.view;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import com.laewoong.search.R;
import com.laewoong.search.util.BackPressCloseHandler;
import com.laewoong.search.util.NetworkState;
import com.laewoong.search.viewmodel.SearchViewModel;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity implements OnSelectedItemListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private SearchView  mSearchView;
    private BackPressCloseHandler mBackPressCloseHandler;
    private SearchViewModel searchViewModel;
    private RecyclerView queryResponseRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = (SearchView)findViewById(R.id.searchview_query);
        mBackPressCloseHandler = new BackPressCloseHandler(this);
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        queryResponseRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_query_response);

        init();
    }

    private void init() {

        // Init SearchView
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                query = query.trim();

                if(query.isEmpty()) {
                    return false;
                }

                mSearchView.clearFocus();
                searchViewModel.selectedTabButton(searchViewModel.getCurSelectedTab().getValue());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryRefinementEnabled(true);

        queryResponseRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        queryResponseRecyclerView.setHasFixedSize(true);

        searchViewModel.getCurSelectedTab().observe(this, tab -> {

            String query = mSearchView.getQuery().toString();

            switch (tab) {
                case WEB:
                    queryResponseRecyclerView.setAdapter(new WebResponsePagedListAdapter());
                    queryResponseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

                    if((query != null) && (query.isEmpty() == false)) {
                        searchViewModel.queryWeb(query);
                    }
                break;
                case IMAGE:
                    ImageResponsePagedListAdapter adapter = new ImageResponsePagedListAdapter(getApplicationContext());
                    adapter.setOnSelectedItemListener(this);
                    queryResponseRecyclerView.setAdapter(adapter);
                    queryResponseRecyclerView.setLayoutManager(new GridLayoutManager(this, ViewConstants.DEFAULT_GRID_SPAN_COUNT));

                    if((query != null) && (query.isEmpty() == false)) {
                        searchViewModel.queryImage(query);
                    }
                    break;
            }
        });

        Observable<ViewConstants.TAB> webButtonObservable = RxView.clicks(findViewById(R.id.button_web))
                .map(event -> ViewConstants.TAB.WEB);

        Observable<ViewConstants.TAB> imageButtonObservale = RxView.clicks(findViewById(R.id.button_image))
                .map(event -> ViewConstants.TAB.IMAGE);

        Observable.merge(webButtonObservable, imageButtonObservale)
                .subscribe(tab -> {
                    mSearchView.clearFocus();
                    searchViewModel.selectedTabButton(tab);
                });

        // Init tab view's hint color
        SegmentedGroup tabGroup = (SegmentedGroup)findViewById(R.id.container_tab);
        tabGroup.setTintColor(Color.parseColor("#F06292"));

        searchViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
                @Override
                public void onChanged(@Nullable NetworkState networkState) {

                    Toast.makeText(MainActivity.this, networkState.getMsg(), Toast.LENGTH_SHORT).show();
                }
            });

        searchViewModel.getWebInfoList().observe(this, pagedList -> {

            if(searchViewModel.getCurSelectedTab().getValue() != ViewConstants.TAB.WEB) {
                return;
            }

            ((WebResponsePagedListAdapter)(queryResponseRecyclerView.getAdapter())).submitList(pagedList);
        });

        searchViewModel.getImageInfoList().observe(this, pagedList -> {

            if(searchViewModel.getCurSelectedTab().getValue() != ViewConstants.TAB.IMAGE) {
                return;
            }

            ((ImageResponsePagedListAdapter)(queryResponseRecyclerView.getAdapter())).submitList(pagedList);
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

    @Override
    public void onSelectedItem(int position) {

        searchViewModel.getSelectedDetailImagePosition().setValue(position);

        FragmentManager fm = getSupportFragmentManager();
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