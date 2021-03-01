//package com.cs65.colorpal.views.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.cs65.colorpal.R;
//import com.cs65.colorpal.models.UnsplashImage;
//import com.cs65.colorpal.views.activities.InspectActivity;
//
//import java.util.List;
//
//public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {
//
//    private final LayoutInflater mInflater;
//    private final Context context;
//    private List<UnsplashImage> images;
//
//    public ImageListAdapter(Context context) {
//        mInflater = LayoutInflater.from(context);
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = mInflater.inflate(R.layout.image_item_view, parent, false);
//        return new ImageViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        Glide.with(context).load(images.get(position).getImage_view_url()).into(holder.imageView);
//    }
//
//    public void setImages(List<UnsplashImage> images) {
//        this.images = images;
//        notifyDataSetChanged();
//    }
//
//
//    @Override
//    public int getItemCount() {
//        if (images == null)
//            return 0;
//        else
//            return images.size();
//    }
//
//    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        public ImageView imageView;
//
//        public ImageViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.image_item);
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            int pos = getLayoutPosition();
//            Intent intent = new Intent(context, InspectActivity.class);
//            intent.putExtra(InspectActivity.PHOTO_URI, images.get(pos).getImage_view_url());
//            context.startActivity(intent);
//        }
//    }
//}
