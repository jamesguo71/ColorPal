package com.cs65.colorpal.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UnsplashRepo {

    private static final String LOG_TAG = "UnsplashRepo";
    private static String URL = "https://api.unsplash.com/search/photos?client_id=cRvYU4AAY1UWQeYh6W7uQl3hyq3r867VyLDGDX5Z6AQ&query=";
    private static RequestQueue requestQueue;
    private MutableLiveData<JSONObject> data;

    public UnsplashRepo(Application application) {
        requestQueue = Volley.newRequestQueue(application);
        data = new MutableLiveData<>();
    }

    public MutableLiveData<JSONObject> fetchImages(String query) throws JSONException {

        data = new MutableLiveData<>();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest
                (Request.Method.GET, URL + query ,null,
                        new Response.Listener<JSONObject>() {
                            public void onResponse(JSONObject response) {
                                data.setValue(response);
                                Log.d("papelog repo", String.valueOf(response));
                            }
                        },
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Log.d(LOG_TAG, String.valueOf(error));
                            }
                        });
        requestQueue.add(jsonArrayRequest);
        return data;
    }
}
