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

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.databinding.FragmentHomeBinding;
import com.cs65.colorpal.viewmodels.LoginViewModel;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.SearchResultAdapter;
import com.cs65.colorpal.views.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

import java.util.List;

public class HomeFragment extends Fragment {

    private View view;
    private PaletteViewModel paletteViewModel;
    private FragmentHomeBinding fragmentHomeBinding;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        fragmentHomeBinding.setLifecycleOwner(requireActivity());
        initializeVariables();
        displaySavedPalettes(view);
        return fragmentHomeBinding.getRoot();
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
        paletteViewModel.fetchHomePagePalettes();
        paletteViewModel.homePagePalettes.observe(getViewLifecycleOwner(), Observer -> {
            // paletteViewModel.homePagePalettes.getValue();
        });

        MainActivity activity = (MainActivity) getActivity();
        String username = activity.getLoginViewModelInstance().authenticatedUser.getValue().getName();
        String welomeMessage = " Welcome, " + username + "!";
        fragmentHomeBinding.setName(welomeMessage);
    }
}