package com.cs65.colorpal.data;

import android.graphics.Bitmap;

import androidx.palette.graphics.Palette;

import io.reactivex.Single;

public class PaletteRepo {
    public PaletteRepo(){}

    public Single<Palette> extractColorPalette(Bitmap bitmap) {
        return Single.fromCallable(() -> Palette.from(bitmap).generate());
    }
}
