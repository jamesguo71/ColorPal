package com.cs65.colorpal.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;

import java.util.List;

public class SwatchDetailsListAdapter extends RecyclerView.Adapter<SwatchDetailsListAdapter.SwatchDetailsListViewHolder> {
    private List<Integer> swatchValues;

    public SwatchDetailsListAdapter(List<Integer> swatchValues) {
        this.swatchValues = swatchValues;
    }

    @NonNull
    @Override
    public SwatchDetailsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swatch_details, parent, false);
        return new SwatchDetailsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwatchDetailsListViewHolder holder, int position) {
        Integer swatchValue = swatchValues.get(position);

        View swatchSquareView = holder.swatchSquareView;
        swatchSquareView.setBackgroundColor(swatchValue);
        TextView swatchValueView = holder.swatchValueView;
        swatchValueView.setText(Integer.toHexString(swatchValue));
    }

    @Override
    public int getItemCount() {
        return swatchValues.size();
    }

    class SwatchDetailsListViewHolder extends RecyclerView.ViewHolder {
        private final View swatchSquareView;
        private final TextView swatchValueView;

        public SwatchDetailsListViewHolder(@NonNull View itemView) {
            super(itemView);
            swatchSquareView = itemView.findViewById(R.id.swatch_square);
            swatchValueView = (TextView) itemView.findViewById(R.id.swatch_value);
        }
    }
}
