package com.example.revibemarket.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    protected List<Uri> mImageUris;

    public ImageAdapter(List<Uri> imageUris) {
        mImageUris = imageUris;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_img_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(mImageUris.get(position))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mImageUris.size();
    }

    public void addImg(List<Uri> uriList) {
        this.mImageUris.addAll(uriList);
        notifyDataSetChanged();
    }

    public void clearImg() {
        this.mImageUris.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView,imageViewDelete;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageViewImage);
            imageViewDelete = view.findViewById(R.id.imageViewDelete);

            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageUris.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                }
            });
        }
    }
}
