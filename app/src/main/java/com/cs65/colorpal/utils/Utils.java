package com.cs65.colorpal.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Palette.Swatch> toSwatches(ArrayList<Integer> swatches) {
        int DUMMY = 100;

        List<Palette.Swatch> newSwatches = new ArrayList<>();
        for (Integer swatch: swatches){
            newSwatches.add(new Palette.Swatch(swatch, DUMMY));
        }
        return newSwatches;
    }

    /**
     * get uri to drawable or any other resource type if u wish
     * @param context - context
     * @param drawableId - drawable res id
     * @return - uri
     */
    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
        return imageUri;
    }

}
