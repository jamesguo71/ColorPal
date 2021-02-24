package com.cs65.colorpal.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cs65.colorpal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class LibraryFragment extends Fragment {

    private FloatingActionButton addPictureButton;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_library, container, false);
        initializeButtons();
        return view;
    }

    public void initializeButtons(){
        addPictureButton = view.findViewById(R.id.add_button);
        addPictureButton.setOnClickListener(v -> Log.d("papelog","addPicture"));
    }
}
