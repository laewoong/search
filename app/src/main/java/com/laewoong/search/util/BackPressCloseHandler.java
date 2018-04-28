package com.laewoong.search.util;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.laewoong.search.R;

/**
 * Created by laewoong on 2018. 4. 22..
 */

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private final long PENDING_TIME = 2000;

    private AppCompatActivity mActivity;
    private Toast mToast;

    public BackPressCloseHandler(AppCompatActivity context) {
        mActivity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + PENDING_TIME) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + PENDING_TIME) {

            mActivity.finish();
            mToast.cancel();
        }
    }

    public void showGuide() {
        mToast = Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getResources().getString(R.string.guide_backpress_close), Toast.LENGTH_SHORT);
        mToast.show();
    }
}