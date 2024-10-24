package com.in_sync.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.in_sync.activities.ImageDetailActivity;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageGalleryHolder> {
    private Context context;
    private List<String> imageList;

    public ImageGalleryAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageGalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageGalleryHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ImageGalleryHolder holder, @SuppressLint("RecyclerView") int position) {
        File imgFile = new File(imageList.get(position));
        Picasso.with(context).load(imgFile).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageGalleryHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ImageGalleryHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.idIVImage);
        }
    }



}
