package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.util.Log;
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

import static com.cs65.colorpal.views.activities.PaletteDetailActivity.TAG_KEY;

public class UnsplashFragment extends Fragment {
    public static String UNSPLASHFRAGMENT_TAG = "UnsplashFragment";
    private View view;
    private SearchView searchView;
    private UnsplashViewModel unsplashViewModel;
    private ArrayList<UnsplashImage> unsplashImages;
    private RecyclerView rvUnsplashImages;
    private UnsplashImagesAdapter  adapter;
    private MainActivity mainActivity;
    public final static String UNSPLASH_FRAGMENT = "unsplash_fragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_unsplash, container, false);
        unsplashImages = new ArrayList<>();
        mainActivity = (MainActivity) getActivity();
        mainActivity.setActivityTitle("Images");
        try {
            setupSearchView(savedInstanceState);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupRecyclerView();
        return view;
    }

    public void setupSearchView(Bundle savedInstanceState) throws JSONException {

        unsplashViewModel = ViewModelProviders.of(requireActivity()).get(UnsplashViewModel.class);
        Bundle args = getArguments();
        if (args != null) {
            String requestedQuery = getArguments().getString(TAG_KEY);
            searchTag(requestedQuery);
        } else {
            if (savedInstanceState == null) unsplashViewModel.runQuery("color palettes");
        }

        UnsplashViewModel.getUnsplashImages().observe(getViewLifecycleOwner(), unsplashImagesResponse -> {
            rvUnsplashImages.setAdapter(new UnsplashImagesAdapter(unsplashImagesResponse));
            adapter.notifyDataSetChanged();
            mainActivity.doneLoadingHanddler();
        });

        searchView = view.findViewById(R.id.unsplash_searchview);
//        searchView.onActionViewExpanded();


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

    public void searchTag(String tag) {
        try {
            unsplashViewModel.runQuery(tag);
            mainActivity.isLoadingHandler("Searching images for " + tag + "...");
        } catch (Exception e) {
            Log.e(UNSPLASHFRAGMENT_TAG, e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        searchView.clearFocus();
    }
}