package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.cs65.colorpal.R;
import com.cs65.colorpal.databinding.FragmentHomeBinding;
import com.cs65.colorpal.models.User;
import com.cs65.colorpal.viewmodels.LoginViewModel;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

public class HomeFragment extends Fragment {

    private View view;
    private PaletteViewModel paletteViewModel;
    private FragmentHomeBinding fragmentHomeBinding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        fragmentHomeBinding.setLifecycleOwner(requireActivity());
        view = fragmentHomeBinding.getRoot();
        initializeVariables();
        return view;
    }

    public void initializeVariables(){

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
}