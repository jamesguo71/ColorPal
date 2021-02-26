package com.cs65.colorpal.views.fragments;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.databinding.FragmentHomeBinding;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.adapter.PaletteListAdapter;
import com.cs65.colorpal.views.activities.MainActivity;
import com.cs65.colorpal.views.adapter.SwatchListAdapter;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private View view;
    private PaletteViewModel paletteViewModel;
    private FragmentHomeBinding fragmentHomeBinding;
    private RecyclerView palettesRecyclerView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        fragmentHomeBinding.setLifecycleOwner(requireActivity());
        initializeVariables();
        displaySavedPalettes(fragmentHomeBinding.getRoot());
        return fragmentHomeBinding.getRoot();
    }

    private void displaySavedPalettes(View view) {
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PaletteListAdapter adapter = new PaletteListAdapter(getActivity());
        palettesRecyclerView.setAdapter(adapter);
        adapter.setPalettes(getData());
//        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);
//        paletteViewModel.getColorPalette().observe(getViewLifecycleOwner(), palette -> addSwatches(palette));
//        paletteViewModel.extractColorPalette(BitmapFactory.decodeResource(getResources(), R.drawable.nature_photo));
    }

    // Todo: Remove after db is ready
    private List<ColorPalette> getData() {
        List<ColorPalette> palettes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ColorPalette p = new ColorPalette();
            ArrayList<Integer> colors = new ArrayList<Integer>();
            Random rnd = new Random();
            int lower = rnd.nextInt(3);
            for (int j = 0; j < lower + 7; j++)
                colors.add(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
            p.setSwatches(colors);
            palettes.add(p);
        }
        return palettes;
    }

    public void initializeVariables(){
        paletteViewModel = ViewModelProviders.of(requireActivity()).get(PaletteViewModel.class);
        paletteViewModel.fetchHomePagePalettes();
        paletteViewModel.homePagePalettes.observe(getViewLifecycleOwner(), Observer -> {
            // paletteViewModel.homePagePalettes.getValue();
        });

        MainActivity activity = (MainActivity) getActivity();
        if( activity.getLoginViewModelInstance().authenticatedUser!= null){
            String username = activity.getLoginViewModelInstance().authenticatedUser.getValue().getName();
            String welomeMessage = " Welcome, " + username + "!";
            fragmentHomeBinding.setName(welomeMessage);
        }
    }
}