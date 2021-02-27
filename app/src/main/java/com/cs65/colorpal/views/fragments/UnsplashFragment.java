package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.databinding.FragmentUnsplashBinding;
import com.cs65.colorpal.viewmodels.UnsplashViewModel;
import com.cs65.colorpal.views.adapter.ImageListAdapter;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnsplashFragment extends Fragment {

    private View view;
    private  SearchView searchView;
    private FragmentUnsplashBinding fragmentUnsplashBinding;
    private UnsplashViewModel unsplashViewModel;
    private List<String> imageUrls = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentUnsplashBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_unsplash, container, false);
        fragmentUnsplashBinding.setLifecycleOwner(requireActivity());
        view = fragmentUnsplashBinding.getRoot();
        setupSearchView();
        displayImages();
        return view;
    }

    public void setupSearchView(){

        unsplashViewModel = ViewModelProviders.of(requireActivity()).get(UnsplashViewModel.class);
        unsplashViewModel.unsplashImages.observe(getViewLifecycleOwner(), Observer -> {
            Log.d("papelog fragment", "data fetched");
            fragmentUnsplashBinding.setNumberOfPics(Observer.toString());
            JSONArray imgResults = null;
            try {
                imgResults = Observer.getJSONArray("results");
                for (int i = 0; i < imgResults.length(); i++) {
                    imageUrls.add(imgResults.getJSONObject(i).getJSONObject("urls").getString("small"));
                }
            } catch (JSONException e) {
                // Todo: Add error message below the searchbar
                Log.d("guofei", "setupSearchView: Failed to get JSONArray");
                e.printStackTrace();
            }
        });

        searchView = (SearchView) view.findViewById(R.id.unsplash_searchview);
        searchView.onActionViewExpanded();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                try {
                    unsplashViewModel.runQuery(query);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void displayImages() {
        RecyclerView recyclerView = view.findViewById(R.id.unsplashImages);
        ImageListAdapter adapter = new ImageListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        // Todo: decide what to display when user enters for the first time
        if (imageUrls.size() == 0)
            loadImageUrls();
        adapter.setImageUrls(imageUrls);
    }

    private void loadImageUrls() {
        imageUrls.add("https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MXwxfHNlYXJjaHwxfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1539627831859-a911cf04d3cd?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwyfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1603394151492-5e9b974b090b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwzfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1548102245-c79dbcfa9f92?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHw0fHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1507646227500-4d389b0012be?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHw1fHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1485217988980-11786ced9454?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MXwxfHNlYXJjaHw2fHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1517055813639-0ae179305650?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHw3fHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1534407309409-d1a1cafd0fec?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHw4fHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1515736076039-a3ca66043b27?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHw5fHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400");
        imageUrls.add("https://images.unsplash.com/photo-1530530722414-a66e95a6ca5a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwxMHx8c21hcnR8ZW58MHx8fA&ixlib=rb-1.2.1&q=80&w=400");
    }
}