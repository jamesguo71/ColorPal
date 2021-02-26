package com.cs65.colorpal.views.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.viewmodels.EditViewModel;
import com.cs65.colorpal.views.activities.EditActivity;

import java.util.List;

import static java.lang.Math.min;

public class SwatchGridAdapter extends RecyclerView.Adapter<SwatchGridAdapter.SwatchGridViewHolder> {

    List<Palette.Swatch> swatches;
    EditViewModel editViewModel;
    boolean clickable;

    public SwatchGridAdapter(List<Palette.Swatch> swatches, EditViewModel editViewModel, boolean clickable) {
        this.swatches = swatches;
        this.editViewModel = editViewModel;
        this.clickable = clickable;
    }

    @NonNull
    @Override
    public SwatchGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swatch, parent, false);
        return new SwatchGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwatchGridViewHolder holder, int position) {
        Palette.Swatch swatch = swatches.get(position);

        View swatchView = holder.getSwatchView();
        swatchView.setBackgroundColor(swatch.getRgb());
        if(clickable) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editViewModel.setSelectedColor(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return swatches.size();
    }

    class SwatchGridViewHolder extends RecyclerView.ViewHolder {
        private final View swatchView;

        public SwatchGridViewHolder(@NonNull View itemView) {
            super(itemView);
            swatchView = itemView.findViewById(R.id.swatch_item);
            ViewGroup.LayoutParams layoutParams = swatchView.getLayoutParams();
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            layoutParams.width = width / min(8,swatches.size());
            layoutParams.height = width / min(8,swatches.size());
        }

        public View getSwatchView() {
            return swatchView;
        }
    }
}
