package com.cs65.colorpal.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.cs65.colorpal.views.adapter.PaletteListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class HomeFragment extends Fragment{

    private View view;
    private PaletteViewModel paletteViewModel;

    private RecyclerView palettesRecyclerView;
    private static MainActivity mainActivity;
    private PaletteListAdapter adapter;
    private ConstraintLayout emptyTextView;
    private FloatingActionButton searchButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            initializeVariables();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainActivity.isLoadingHandler("Loading Palettes...");
        emptyPalettesHandler();

        return view;

    }

    private void updateHomePalettes(){
        adapter.setPalettes(paletteViewModel.mHomeColorPaletteList.getValue());
    }

    private void displaySavedPalettes(View view, List<ColorPalette> colorPaletteList) {
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PaletteListAdapter adapter = new PaletteListAdapter(getActivity(), true);
        palettesRecyclerView.setAdapter(adapter);
        adapter.setPalettes(colorPaletteList);
    }

    private void emptyPalettesHandler(){
        if(paletteViewModel.mHomeColorPaletteList.getValue() != null) {
            if(paletteViewModel.mHomeColorPaletteList.getValue().size() == 0){
                emptyTextView.setVisibility(View.VISIBLE);
                palettesRecyclerView.setVisibility(View.GONE);
            }
            else{
                palettesRecyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            }
        }
    }

    public void initializeVariables() throws InterruptedException {
        paletteViewModel = ViewModelProviders.of(requireActivity()).get(PaletteViewModel.class);
        paletteViewModel.fetchHomeColorPalettes();
        paletteViewModel.mHomeColorPaletteList.observe(getViewLifecycleOwner(), Observer -> {
            emptyPalettesHandler();
            updateHomePalettes();
            mainActivity.doneLoadingHanddler();
        });

        emptyTextView = (ConstraintLayout) view.findViewById(R.id.empty_text_view);
        mainActivity = (MainActivity) getActivity();
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PaletteListAdapter(getActivity(),true);
        palettesRecyclerView.setAdapter(adapter);

        searchButton = view.findViewById(R.id.search_by_tag_button);
        searchButton.setOnClickListener(v -> showSearchTagDialog());

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            initializeVariables();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        emptyPalettesHandler();
    }

    private void showSearchTagDialog() {
        final View customLayout = getLayoutInflater().inflate(R.layout.input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(customLayout);
        builder.setTitle("Enter the name of a tag.");
        builder.setIcon(R.drawable.logo_icon);

        builder.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TextInputEditText textInputEditText = customLayout.findViewById(R.id.tag_input_field);
                Log.d("papelog", textInputEditText.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}