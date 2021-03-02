package com.cs65.colorpal.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cs65.colorpal.data.UnsplashRepo;
import com.cs65.colorpal.models.UnsplashImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnsplashViewModel extends AndroidViewModel {
    private static UnsplashRepo unsplashRepo;
    private static MutableLiveData<ArrayList<UnsplashImage>> unsplashImages;

    public UnsplashViewModel(Application application) throws JSONException {
        super(application);
        unsplashRepo = new UnsplashRepo(application);
        unsplashImages = new MutableLiveData<>();
    }

    public void  runQuery (String query) throws JSONException {
        unsplashRepo.fetchImages(query,unsplashImages);
    }

    public static LiveData<ArrayList<UnsplashImage>> getUnsplashImages() {
        return unsplashImages;
    }
}
