package com.laewoong.search.util;

import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class Util {

    public static SpannableStringBuilder makeKeywordToBold(String keyword, String string) {

        string = string.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");

        SpannableStringBuilder str = new SpannableStringBuilder(string);

        String regex = "(?i)"+keyword;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);


        while(matcher.find() == true){
            str.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return str;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
