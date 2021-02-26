package com.cs65.colorpal.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cs65.colorpal.data.UnsplashRepo;

import org.json.JSONException;
import org.json.JSONObject;

public class UnsplashViewModel extends AndroidViewModel {
    private UnsplashRepo unsplashRepo;
    public LiveData<JSONObject> unsplashImages;

    public UnsplashViewModel(Application application) throws JSONException {
        super(application);
        unsplashRepo = new UnsplashRepo(application);
        unsplashImages = new MutableLiveData<>();
    }

    public void  runQuery (String query) throws JSONException {
        unsplashImages = unsplashRepo.fetchImages(query);
    }
}
