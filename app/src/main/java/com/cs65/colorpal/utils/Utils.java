package com.cs65.colorpal.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs65.colorpal.models.PaletteTag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.List;

public class Utils {

    public static List<Palette.Swatch> toSwatches(ArrayList<Integer> swatches) {
        int DUMMY = 100;

        List<Palette.Swatch> newSwatches = new ArrayList<>();
        for (Integer swatch: swatches){
            if (swatch == null)
                throw new IllegalArgumentException("Swatch value can't be null!");
            newSwatches.add(new Palette.Swatch(swatch, DUMMY));
        }
        return newSwatches;
    }

    public static void generateImageLabel(Bitmap bitmap, MutableLiveData<ArrayList<PaletteTag>> imageLabelsLiveData) {
        ArrayList<PaletteTag> imageLabels = new ArrayList<>();
        InputImage image = null;
        image = InputImage.fromBitmap(bitmap, 0);
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            int index = label.getIndex();
                            imageLabels.add(new PaletteTag(text));
                        }
                        imageLabelsLiveData.getValue().addAll(imageLabels.subList(0, Math.min(imageLabels.size(), 4)));
                        imageLabelsLiveData.postValue(imageLabelsLiveData.getValue());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", "onFailure: Image Labeling Failed.");
                    }
                });
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
