package com.laewoong.search.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.Toast;

import com.laewoong.search.R;
import com.laewoong.search.model.response.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class Util {

    private static Toast mToast;

    public static void showToastShort(Context context, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    public static void showToastLong(Context context, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static String convertErrorCodeToMessage(Context context, String query, String errorCode) {

        String message;
        ErrorCode code = ErrorCode.valueOf(errorCode);

        switch (code) {
            case NAVER_MAX_START_VALUE_POLICY:
                message = context.getString(R.string.guide_naver_max_start_value_policy);
                break;
            case FAIL_NETWORK:
                message = context.getString(R.string.guide_check_network_state);
                break;
            case ARRIVED_FINAL_RESPONSE:
                message = context.getString(R.string.guide_final_query_response);
                break;
            case ARRIVED_EMPTY_RESPONSE:
                message = String.format(context.getString(R.string.guide_empty_query_response), query);
                break;
            default:
                message = context.getString(R.string.guide_internal_error);
        }

        return message;
    }
}
