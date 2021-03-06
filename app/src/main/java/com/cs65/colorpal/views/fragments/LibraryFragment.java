package com.cs65.colorpal.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.ColorPalette;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.cs65.colorpal.views.adapter.PaletteListAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LibraryFragment extends Fragment{

    private static final String LOG_TAG = "LibraryFragment";
    private FloatingActionButton addPictureButton;
    private ImageView imageView;
    private MainActivity mainActivity;
    private CharSequence[] imageSelectionOptions = {
            "Take a new picture",
            "Select a picture from your gallery",
            };
    private View view;
    private PaletteViewModel paletteViewModel;
    private RecyclerView palettesRecyclerView;
    private PaletteListAdapter adapter;
    private ConstraintLayout emptyTextView;

    private final String DIALOG_TITLE = "Choose or take an image";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_library, container, false);
        try {
            initializeVariables();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        emptyPalettesHandler();
        return view;
    }

    private void displaySavedPalettes(View view, List<ColorPalette> colorPaletteList) {
        palettesRecyclerView = view.findViewById(R.id.homePalettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PaletteListAdapter adapter = new PaletteListAdapter(getActivity(), false);
        palettesRecyclerView.setAdapter(adapter);
        adapter.setPalettes(colorPaletteList);
    }

    private void emptyPalettesHandler(){
        if(paletteViewModel.mHomeColorPaletteList.getValue() != null) {
            if(paletteViewModel.mUserLibraryColorPaletteList.getValue().size() == 0){
                emptyTextView.setVisibility(View.VISIBLE);
                palettesRecyclerView.setVisibility(View.GONE);
            }
            else{
                palettesRecyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            }
        }
    }

    private void handleOptionSelection(int userChoice) throws IOException {
        if(userChoice == 0){
            mainActivity.dispatchTakePictureIntent();
        } else if(userChoice == 1){
            mainActivity.dispatchSelectPictureIntent();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        emptyPalettesHandler();
    }

    public void initializeVariables() throws InterruptedException {
        paletteViewModel = ViewModelProviders.of(requireActivity()).get(PaletteViewModel.class);
        paletteViewModel.fetchUserLibraryColorPalettes();
        paletteViewModel.mUserLibraryColorPaletteList.observe(getViewLifecycleOwner(), Observer -> {
            emptyPalettesHandler();
            updateLibraryPalettes();
            mainActivity.doneLoadingHanddler();
        });

        emptyTextView = (ConstraintLayout) view.findViewById(R.id.empty_text_view);

        palettesRecyclerView = view.findViewById(R.id.recycle_view_my_palettes);
        palettesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PaletteListAdapter(getActivity(), false);
        palettesRecyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(deleteItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(palettesRecyclerView);

        mainActivity = ((MainActivity) getActivity());
        mainActivity.isLoadingHandler("Loading Palettes...");

        imageView = (ImageView) mainActivity.findViewById(R.id.image);

        addPictureButton = view.findViewById(R.id.add_button);
        addPictureButton.setOnClickListener(v -> showImageOptionsDialog());

    }

    public void onResume() {
        super.onResume();
        try {
            initializeVariables();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        emptyPalettesHandler();
    }

    private void showImageOptionsDialog(){

        MaterialAlertDialogBuilder optionsDialog = new MaterialAlertDialogBuilder(getActivity())
                .setIcon(R.drawable.logo_icon)
                .setTitle(DIALOG_TITLE)
                .setItems(imageSelectionOptions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            handleOptionSelection(which);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        optionsDialog.show();
    }

    private void updateLibraryPalettes(){
        adapter.setPalettes(paletteViewModel.mUserLibraryColorPaletteList.getValue());
    }

    ItemTouchHelper.SimpleCallback deleteItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete this palette permanently?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        paletteViewModel.deletePaletteFromDb(adapter.get(position).getDocId());
                        adapter.remove(position);
                        adapter.notifyItemRemoved(position);
                        emptyPalettesHandler();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };
}
