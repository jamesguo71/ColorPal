package com.cs65.colorpal.services;

import com.cs65.colorpal.models.ColorPalette;

import java.util.List;

public interface FirebaseCallback {
    void onCallback(List<ColorPalette> list);
}
