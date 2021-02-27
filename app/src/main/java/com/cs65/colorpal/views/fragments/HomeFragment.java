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
import com.cs65.colorpal.models.User;
import com.cs65.colorpal.services.FirebaseCallback;
import com.cs65.colorpal.services.FirebaseService;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.cs65.colorpal.views.adapter.PaletteListAdapter;

import java.util.List;

public class HomeFragment extends Fragment implements FirebaseCallback {

    private View view;
    private PaletteViewModel paletteViewModel;
    private FragmentHomeBinding fragmentHomeBinding;
    private RecyclerView palettesRecyclerView;
    private static FirebaseService firebaseService;
    private static MainActivity mainActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        fragmentHomeBinding.setLifecycleOwner(requireActivity());
        view = fragmentHomeBinding.getRoot();
        initializeVariables();
        mainActivity.isLoadingHandler("Loading Palettes...");
        getData();

        return view;

    }

    private void displaySavedPalettes(View view, List<ColorPalette> colorPaletteList) {
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PaletteListAdapter adapter = new PaletteListAdapter(getActivity());
        palettesRecyclerView.setAdapter(adapter);
//        adapter.setPalettes(getData());
        adapter.setPalettes(colorPaletteList);
//        paletteViewModel = ViewModelProviders.of(this).get(PaletteViewModel.class);
//        paletteViewModel.getColorPalette().observe(getViewLifecycleOwner(), palette -> addSwatches(palette));
//        paletteViewModel.extractColorPalette(BitmapFactory.decodeResource(getResources(), R.drawable.nature_photo));
    }

    // TODO: Remove after db is ready
    private void getData() {
        firebaseService.fetchAllPalettes(this);
//        return paletteViewModel.fetchAllPaletteFromDB();
//        List<ColorPalette> palettes = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ColorPalette p = new ColorPalette();
//            ArrayList<Integer> colors = new ArrayList<Integer>();
//            Random rnd = new Random();
//            int lower = rnd.nextInt(3);
//            for (int j = 0; j < lower + 7; j++)
//                colors.add(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
//            p.setSwatches(colors);
//            palettes.add(p);
//        }
//        return palettes;
    }

    public void initializeVariables(){
        firebaseService = new FirebaseService();
        paletteViewModel = ViewModelProviders.of(requireActivity()).get(PaletteViewModel.class);
        paletteViewModel.fetchHomePagePalettes();
        paletteViewModel.homePagePalettes.observe(getViewLifecycleOwner(), Observer -> {
//             Log.d("papelog", String.valueOf(paletteViewModel.homePagePalettes.getValue()));
        });
        mainActivity = (MainActivity) getActivity();
    }

    //Display palettes when fetching is done
    @Override
    public void onCallback(List<ColorPalette> list) {
        Log.d("HomeFragmentPape",list.get(0).toString());
        displaySavedPalettes(view,list);
        mainActivity.doneLoadingHanddler();
    }
}