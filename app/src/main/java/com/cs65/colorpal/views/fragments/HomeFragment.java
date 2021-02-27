package com.cs65.colorpal.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
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
import com.cs65.colorpal.models.User;
import com.cs65.colorpal.viewmodels.FirebaseViewModel;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.cs65.colorpal.views.adapter.PaletteListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment{

    private View view;
    private PaletteViewModel paletteViewModel;
    private FirebaseViewModel firebaseViewModel;
    private FragmentHomeBinding fragmentHomeBinding;
    private RecyclerView palettesRecyclerView;
    private PaletteListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        fragmentHomeBinding.setLifecycleOwner(requireActivity());
        view = fragmentHomeBinding.getRoot();

        firebaseViewModel = ViewModelProviders.of(this).get(FirebaseViewModel.class);
        firebaseViewModel.fetchHomeColorPalettesFromDB().observe(getViewLifecycleOwner(), homePalettes -> updateHomePalettes());

        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PaletteListAdapter(getActivity());
        palettesRecyclerView.setAdapter(adapter);

        initializeVariables();
//        getData();

        return view;

    }

    private void updateHomePalettes(){
        adapter.setPalettes(firebaseViewModel.getHomeColorPalettes().getValue());
    }

    private void displaySavedPalettes(View view, List<ColorPalette> colorPaletteList) {
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PaletteListAdapter adapter = new PaletteListAdapter(getActivity());
        palettesRecyclerView.setAdapter(adapter);
        adapter.setPalettes(colorPaletteList);
//        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);
//        paletteViewModel.getColorPalette().observe(getViewLifecycleOwner(), palette -> addSwatches(palette));
//        paletteViewModel.extractColorPalette(BitmapFactory.decodeResource(getResources(), R.drawable.nature_photo));
    }


    public void initializeVariables(){
//        firebaseService = new FirebaseService();
        paletteViewModel = ViewModelProviders.of(requireActivity()).get(PaletteViewModel.class);
        paletteViewModel.fetchHomePagePalettes();
        paletteViewModel.homePagePalettes.observe(getViewLifecycleOwner(), Observer -> {
//             Log.d("papelog", String.valueOf(paletteViewModel.homePagePalettes.getValue()));
        });

        MainActivity activity = (MainActivity) getActivity();
        if( activity.getLoginViewModelInstance().authenticatedUser!= null){
            User user = activity.getLoginViewModelInstance().authenticatedUser.getValue();
            fragmentHomeBinding.setMessage(" Welcome, " + user.getName() + "!");
        }
    }

//    @Override
//    public void onCallback(List<ColorPalette> list) {
//        displaySavedPalettes(view,list);
//    }
}