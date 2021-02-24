package com.cs65.colorpal.data;

import android.graphics.Bitmap;

import androidx.palette.graphics.Palette;

import io.reactivex.Single;

public class PaletteRepo {
    private static final int MAX_COLOR_COUNT = 32;

    public PaletteRepo(){}

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).maximumColorCount(MAX_COLOR_COUNT).generate());
    }
}
