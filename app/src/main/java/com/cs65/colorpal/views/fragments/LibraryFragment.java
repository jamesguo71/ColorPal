package com.cs65.colorpal.views.fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.PaletteViewModel;
import com.cs65.colorpal.views.activities.MainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LibraryFragment extends Fragment {

    private static final String LOG_TAG = "LibraryFragment";
    private FloatingActionButton addPictureButton;
    private ImageView imageView;
    private MainActivity activity;
    private CharSequence[] imageSelectionOptions = {
            "Take a new picture",
            "Select a picture from your gallery",
            };
    private View view;
    private PaletteViewModel paletteViewModel;

    private final String DIALOG_TITLE = "Choose or take an image";

    private void handleOptionSelection(int userChoice) throws IOException {
        if(userChoice == 0){
            activity.dispatchTakePictureIntent();
        } else if(userChoice == 1){
            activity.dispatchSelectPictureIntent();
        }
        paletteViewModel.getSelectedImage().observe(this, Observer -> {
            Log.d(LOG_TAG, "A new image was selected");
        });
    }

    private void initializeButtons(){
        addPictureButton = view.findViewById(R.id.add_button);
        addPictureButton.setOnClickListener(v -> showImageOptionsDialog());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_library, container, false);
        initializeVariables();
        return view;
    }

    public void initializeVariables(){
        activity = ((MainActivity) getActivity());
        imageView = (ImageView) activity.findViewById(R.id.image);
        paletteViewModel = new ViewModelProvider(requireActivity()).get(PaletteViewModel.class);
        initializeButtons();
    }

    public void onResume() {
        super.onResume();
        initializeVariables();
        if(paletteViewModel.getSelectedImage().getValue() != null){
            setImage(paletteViewModel.getSelectedImage().getValue());
        }
    }

    public void setImage(Uri uri)  {
        if(uri == null) return;
        try {
            InputStream imageStream = activity.getContentResolver().openInputStream(uri);
            Bitmap image = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showImageOptionsDialog(){

        MaterialAlertDialogBuilder optionsDialog = new MaterialAlertDialogBuilder(getActivity())
                .setIcon(R.drawable.ic_baseline_photo_camera_24)
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
}
