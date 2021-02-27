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
import com.cs65.colorpal.models.UnsplashImage;
import com.cs65.colorpal.viewmodels.UnsplashViewModel;
import com.cs65.colorpal.views.adapter.ImageListAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UnsplashFragment extends Fragment {

    private View view;
    private  SearchView searchView;
    private FragmentUnsplashBinding fragmentUnsplashBinding;
    private UnsplashViewModel unsplashViewModel;
    private List<UnsplashImage> images;

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
        unsplashViewModel.getUnsplashImages().observe(getViewLifecycleOwner(), Observer -> {
            Log.d("papelog fragment", "data fetched");
            fragmentUnsplashBinding.setNumberOfPics(Observer.toString());
            images = Observer;
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
        if (images == null)
            loadImages();
        adapter.setImages(images);
    }

    // Todo: remove later. for demo purpose.
    private void loadImages() {
        images = new ArrayList<>();
        images.add(new UnsplashImage("T", "T", "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MXwxfHNlYXJjaHwxfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400", "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MXwxfHNlYXJjaHwxfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400"));
        images.add(new UnsplashImage("T", "T", "https://images.unsplash.com/photo-1539627831859-a911cf04d3cd?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwyfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400", "https://images.unsplash.com/photo-1539627831859-a911cf04d3cd?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwyfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400"));
        images.add(new UnsplashImage("T", "T","https://images.unsplash.com/photo-1603394151492-5e9b974b090b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwzfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400", "https://images.unsplash.com/photo-1603394151492-5e9b974b090b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwzfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400"));
        images.add(new UnsplashImage("T", "T","https://images.unsplash.com/photo-1603394151492-5e9b974b090b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwzfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400", "https://images.unsplash.com/photo-1603394151492-5e9b974b090b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwzfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400"));
        images.add(new UnsplashImage("T", "T","https://images.unsplash.com/photo-1603394151492-5e9b974b090b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwzfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400", "https://images.unsplash.com/photo-1603394151492-5e9b974b090b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MXwyMTAyMTZ8MHwxfHNlYXJjaHwzfHxzbWFydHxlbnwwfHx8&ixlib=rb-1.2.1&q=80&w=400"));
    }
}