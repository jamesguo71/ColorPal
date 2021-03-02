package com.cs65.colorpal.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cs65.colorpal.models.UnsplashImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnsplashRepo {

    private static final String LOG_TAG = "UnsplashRepo";
    private static String URL = "https://api.unsplash.com/search/photos?client_id=cRvYU4AAY1UWQeYh6W7uQl3hyq3r867VyLDGDX5Z6AQ&query=";
    private static RequestQueue requestQueue;
    private MutableLiveData<ArrayList<UnsplashImage>>data;

    public UnsplashRepo(Application application) {
        requestQueue = Volley.newRequestQueue(application);
        data = new MutableLiveData<>();
    }

    public void fetchImages(String query, MutableLiveData<ArrayList<UnsplashImage>> data) throws JSONException {
        new Thread(){
            @Override
            public void run() {
                super.run();
                JsonObjectRequest jsonArrayRequest = new JsonObjectRequest
                        (Request.Method.GET, URL + query ,null,
                                new Response.Listener<JSONObject>() {
                                    public void onResponse(JSONObject response) {
                                        try {

                                            data.postValue(convertToUnsplashImages(response));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(LOG_TAG, String.valueOf(error));
                                    }
                                });
                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonArrayRequest);
            }
        }.start();
    }

    public ArrayList<UnsplashImage> convertToUnsplashImages(JSONObject response) throws JSONException {
        ArrayList<UnsplashImage> images = new ArrayList<>();
        JSONArray results = (JSONArray) response.get("results");

        for(int i = 0; i < results.length(); ++i){
            JSONObject imageResult =  (JSONObject) results.get(i);

            String description = imageResult.getString("description");
            String nameOfUser = imageResult.getJSONObject("user").getString("name");
            String image_view_url = imageResult.getJSONObject("urls").getString("small");
            String image_download_url = imageResult.getJSONObject("links").getString("download");

            UnsplashImage unsplashImage = new UnsplashImage(nameOfUser, description, image_view_url, image_download_url);
            images.add(unsplashImage);
        }
        return images;
    }
}
