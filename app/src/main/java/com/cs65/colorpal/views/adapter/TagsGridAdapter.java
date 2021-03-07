package com.cs65.colorpal.views.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.colorpal.R;
import com.cs65.colorpal.models.PaletteTag;
import com.cs65.colorpal.views.activities.PaletteDetailActivity;

import java.util.List;

public class TagsGridAdapter extends RecyclerView.Adapter<TagsGridAdapter.TagsGridViewHolder> {

    List<PaletteTag> tags;
    Context mContext;
    private boolean allowEdit = true;

    public TagsGridAdapter(List<PaletteTag> tags, Context mContext) {
        this.tags = tags;
        this.mContext = mContext;
    }

    public TagsGridAdapter(List<PaletteTag> tags, Context mContext, boolean allowEdit) {
        this.tags = tags;
        this.mContext = mContext;
        this.allowEdit = allowEdit;
    }

    public void setTags(List<PaletteTag> tags){
        this.tags = tags;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagsGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag, parent, false);
        return new TagsGridViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull TagsGridViewHolder holder, int position) {
        PaletteTag tag = tags.get(position);
        holder.textView.setText(tag.getText());
        // IF in Detail Activity, direct to search the tag when clicked on
        if (!allowEdit) {
            holder.textView.setOnClickListener(v -> {
                holder.setUpTagSearch();
            });
        } else {
            holder.textView.setOnClickListener(v -> {
                //TODO
                EditText edittext = new EditText(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                edittext.setInputType(InputType.TYPE_CLASS_TEXT);
                edittext.setText(tags.get(position).getText());
                builder.setView(edittext);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    tags.set(position, new PaletteTag(edittext.getText().toString()));
                    notifyDataSetChanged();
                    dialog.dismiss();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            });
            holder.removeView.setOnClickListener(v -> {
                tags.remove(position);
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagsGridViewHolder extends RecyclerView.ViewHolder {
        public final View tagView;
        public final TextView textView;
        public final ImageView removeView;
        public final TextView spaceView;
        private final Context context;

        public TagsGridViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tagView = itemView.findViewById(R.id.tag_view);
            textView = itemView.findViewById(R.id.tag_text);
            removeView = itemView.findViewById(R.id.tag_remove);
            spaceView = itemView.findViewById(R.id.tag_space);
            this.context = context;
            if (!allowEdit) {
                removeView.setVisibility(View.GONE);
                spaceView.setVisibility(View.GONE);
            }
        }

        public View getTagView() {
            return tagView;
        }

        public void setUpTagSearch() {
            PaletteDetailActivity paletteDetailActivity = (PaletteDetailActivity)context;
            String tag = textView.getText().toString();
            paletteDetailActivity.searchImagesByTag(tag);
        }
    }
}
