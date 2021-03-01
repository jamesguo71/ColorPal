package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.UnsplashImage;
import com.cs65.colorpal.viewmodels.UnsplashViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.cs65.colorpal.views.adapter.UnsplashImagesAdapter;

import org.json.JSONException;

import java.util.ArrayList;

public class UnsplashFragment extends Fragment {

    private View view;
    private  SearchView searchView;
    private UnsplashViewModel unsplashViewModel;
    private ArrayList<UnsplashImage> unsplashImages;
    private RecyclerView rvUnsplashImages;
    private UnsplashImagesAdapter  adapter;
    private MainActivity mainActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_unsplash, container, false);
        unsplashImages = new ArrayList<>();
        mainActivity = (MainActivity) getActivity();
        setupSearchView();
        setupRecyclerView();
        return view;
    }

    public void setupSearchView(){

        unsplashViewModel = ViewModelProviders.of(requireActivity()).get(UnsplashViewModel.class);
        UnsplashViewModel.getUnsplashImages().observe(getViewLifecycleOwner(), unsplashImagesResponse -> {
            rvUnsplashImages.setAdapter(new UnsplashImagesAdapter(unsplashImagesResponse));
            adapter.notifyDataSetChanged();
            mainActivity.doneLoadingHanddler();
        });

        searchView = (SearchView) view.findViewById(R.id.unsplash_searchview);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                try {
                    unsplashViewModel.runQuery(query);
                    mainActivity.isLoadingHandler("Searching images for " + query + "...");
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

    public void setupRecyclerView(){
        rvUnsplashImages = (RecyclerView) view.findViewById(R.id.unsplash_recycle_view);
        adapter = new UnsplashImagesAdapter(unsplashImages);
        rvUnsplashImages.setAdapter(adapter);
        rvUnsplashImages.setLayoutManager(new LinearLayoutManager((getActivity())));
    }

}