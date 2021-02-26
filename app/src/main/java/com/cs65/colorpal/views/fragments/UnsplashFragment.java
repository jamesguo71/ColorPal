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

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.UnsplashViewModel;

import org.json.JSONException;

public class UnsplashFragment extends Fragment {

    private View view;
    private  SearchView searchView;
    private UnsplashViewModel unsplashViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_unsplash, container, false);
        setupSearchView();
        return view;
    }

    public void setupSearchView(){

        unsplashViewModel = ViewModelProviders.of(requireActivity()).get(UnsplashViewModel.class);
        UnsplashViewModel.getUnsplashImages().observe(getViewLifecycleOwner(), Observer -> {
            Log.d("papelog fragment", "data fetched");
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
}