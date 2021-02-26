package com.cs65.colorpal.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.views.activities.InspectActivity;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private final LayoutInflater mInflater;
    private final Context context;
    private List<Palette> mPalettes;

    public SearchResultAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.search_item_view, parent, false);
        return new SearchResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        if (mPalettes != null) {
            Palette entry = mPalettes.get(position);
        } else {
            holder.textView.setText("just a placeholder...");
        }
    }

    public void setPalettes(List<Palette> palettes) {
        mPalettes = palettes;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (mPalettes == null)
            return 0;
        else
            return mPalettes.size();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.searchResultTitle);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            Palette palette =  mPalettes.get(pos);
            Intent intent = new Intent(context, InspectActivity.class);
            context.startActivity(intent);
            }
        }
}
