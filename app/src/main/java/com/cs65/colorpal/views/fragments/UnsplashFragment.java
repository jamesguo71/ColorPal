package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs65.colorpal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class UnsplashFragment extends Fragment {

    private FloatingActionButton addPictureButton;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_unsplash, container, false);
         SearchView searchView = (SearchView) view.findViewById(R.id.unsplash_searchview);
         searchView.onActionViewExpanded();
        return view;
    }
}