package com.cs65.colorpal.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class ColourLoversService {

/**
 * From Android's documentation:
 * "The RequestQueue manages worker threads for running the network operations,
 * reading from and writing to the cache, and parsing responses"
 */
    private RequestQueue requestQueue;
    private static final String LOG_TAG =  "ColourLoversService";
    private static final String apiUrl = "https://www.colourlovers.com/api/palettes?format=json";
    private JSONArray palettes;

    public ColourLoversService(Context context){
        requestQueue = Volley.newRequestQueue(context);
        palettes = new JSONArray();
    }

    public JSONArray fetchPalettes(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, apiUrl, null,
                        new Response.Listener<JSONArray>() {

                    public void onResponse(JSONArray response) {
                        palettes = response;
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue.add(jsonArrayRequest);
        return palettes;
    }
}
