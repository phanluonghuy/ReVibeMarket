package com.example.revibemarket.Adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.R;

import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;

    public HomeProductAdapter(Context context, List<Product> productList) {
        this.productList = productList;
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_bestdeal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = productList.get(position);

        holder.titleTextView.setText(item.getProductName());
        holder.priceTextView.setText(String.valueOf(item.getProductType().getPrice() + " $"));

        List<String> imageUrls = item.getProductType().getImages();

//        if (imageUrls != null && !imageUrls.isEmpty()) {
//            String imageUrl = imageUrls.get(0);
//            Glide.with(holder.imageView.getContext())
//                    .load(imageUrl)
//                    .into(holder.imageView);
//        } else {
//            holder.imageView.setImageResource(R.drawable.sofa_cut);
//        }
//        Glide.with(holder.itemView.getContext())
//                .load(imageUrls.get(0))
//                .placeholder(R.drawable.sofa_cut)
//                .error(R.drawable.sofa_cut)
//                .into(holder.imageView);
        //holder.imageView.setImageResource(R.drawable.sofa_cut);
        Log.d("Product img",productList.get(position).getProductType().getImages().size()+"");
        if (!productList.get(position).getProductType().getImages().isEmpty()) {
            Glide.with(holder.imageView.getContext())
                    .load(productList.get(position).getProductType().getImages().get(0))
                    .error(R.drawable.avatar)
                    .override(330, 330)
                    .into(holder.imageView);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newItems) {
        productList.clear();
        productList.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView priceTextView;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.BestDealTiTle);
            priceTextView = itemView.findViewById(R.id.BestDealPrice);
            imageView = itemView.findViewById(R.id.BestDealImg);
        }
    }
}
