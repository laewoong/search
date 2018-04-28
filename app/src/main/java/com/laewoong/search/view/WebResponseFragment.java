package com.laewoong.search.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laewoong.search.R;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.Util;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class WebResponseFragment extends ResponseFragment<WebInfo> {

    public static final String TAG = WebResponseFragment.class.getSimpleName();

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext().getApplicationContext());
    }

    @Override
    public ResponseListAdapter createResponseListAdapter() {
        return new WebResponseListAdapter();
    }

    @Override
    public List<WebInfo> getResponseList() {
        return mController.getWebQueryResponseList();
    }


    public static class WebResponseListAdapter extends ResponseFragment.ResponseListAdapter<WebResponseListAdapter.ViewHolder, WebInfo> {

        public static class ViewHolder extends RecyclerView.ViewHolder {

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

        @Override
        public WebResponseListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_web_response, parent, false);

            return new WebResponseListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindView(ViewHolder holder, int position) {

            final WebInfo info = mDataset.get(position);

            holder.mTitleTextView.setText(Util.fromHtml(info.getTitle()));
            holder.mDescTextView.setText(Util.fromHtml(info.getDescription()));
            holder.mLinkTextView.setText(info.getLink());
            Linkify.addLinks(holder.mLinkTextView, Linkify.WEB_URLS);
        }
    }
}
