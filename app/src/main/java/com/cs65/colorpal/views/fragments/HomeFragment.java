package com.cs65.colorpal.views.fragments;

import android.app.SearchManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.SearchResultAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

import java.util.List;

public class HomeFragment extends Fragment {

    private View view;
    private PaletteViewModel paletteViewModel;
    private JSONArray palettes;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeVariables();
        displaySavedPalettes(view);
        return view;
    }

    private void displaySavedPalettes(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.searchRecyclerView);
        SearchResultAdapter adapter = new SearchResultAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setPalettes(getData());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private List<SearchResultAdapter.MyPalette> getData() {
        return null;
    }

    public void initializeVariables(){
        paletteViewModel = ViewModelProviders.of(requireActivity()).get(PaletteViewModel.class);
        palettes = paletteViewModel.getHomePagePalettes();
        Log.d("papelog", String.valueOf(palettes));
    }
}