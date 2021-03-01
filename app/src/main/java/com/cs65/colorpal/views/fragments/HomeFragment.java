package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.databinding.FragmentHomeBinding;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.services.FirebaseService;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.cs65.colorpal.views.adapter.PaletteListAdapter;

import java.util.List;

public class HomeFragment extends Fragment{

    private View view;
    private PaletteViewModel paletteViewModel;

    private FragmentHomeBinding fragmentHomeBinding;
    private RecyclerView palettesRecyclerView;
    private static MainActivity mainActivity;
    private PaletteListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        fragmentHomeBinding.setLifecycleOwner(requireActivity());
        view = fragmentHomeBinding.getRoot();

        try {
            initializeVariables();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainActivity.isLoadingHandler("Loading Palettes...");

        return view;

    }

    private void updateHomePalettes(){
        adapter.setPalettes(paletteViewModel.mHomeColorPaletteList.getValue());
    }

    private void displaySavedPalettes(View view, List<ColorPalette> colorPaletteList) {
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PaletteListAdapter adapter = new PaletteListAdapter(getActivity());
        palettesRecyclerView.setAdapter(adapter);
        adapter.setPalettes(colorPaletteList);
    }


    public void initializeVariables() throws InterruptedException {
        paletteViewModel = ViewModelProviders.of(requireActivity()).get(PaletteViewModel.class);
        paletteViewModel.fetchHomeColorPalettes();
        paletteViewModel.mHomeColorPaletteList.observe(getViewLifecycleOwner(), Observer -> {
            updateHomePalettes();
            mainActivity.doneLoadingHanddler();
        });


        mainActivity = (MainActivity) getActivity();
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PaletteListAdapter(getActivity());
        palettesRecyclerView.setAdapter(adapter);
    }

}