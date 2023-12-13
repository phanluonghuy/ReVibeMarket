package com.example.revibemarket.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final List<String> categories;
    private final Context context;

    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.txtTitle.setText(category);

        // Tạo một map để ánh xạ từ tên danh mục (category) sang ID ảnh
        Map<String, Integer> categoryImageMap = createCategoryImageMap();

        // Kiểm tra xem danh mục có hình ảnh không
        if (categoryImageMap.containsKey(category)) {
            int imageResId = categoryImageMap.get(category);

            // Kiểm tra xem tài nguyên hình ảnh có tồn tại không
            if (imageResId != 0) {
                Log.d("CategoryAdapter", "ImageResId: " + imageResId);
                holder.imageView.setImageResource(imageResId);
            } else {
                Log.e("CategoryAdapter", "ImageResId is not valid for category: " + category);
            }
        } else {
            Log.e("CategoryAdapter", "No image resource found for category: " + category);
        }
    }



    public void updateData(List<String> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    private Map<String, Integer> createCategoryImageMap() {
        Map<String, Integer> categoryImageMap = new HashMap<>();
        categoryImageMap.put("Clothing", R.drawable.laundry);
        categoryImageMap.put("Phones", R.drawable.smartphone);
        categoryImageMap.put("Electronics", R.drawable.electronic);
        categoryImageMap.put("Food and Drink", R.drawable.fastfood);
        categoryImageMap.put("Books", R.drawable.bookstack);
        categoryImageMap.put("Appliances", R.drawable.electricappliance);
        categoryImageMap.put("Medicals", R.drawable.pill);
        categoryImageMap.put("Beauty", R.drawable.products);
        categoryImageMap.put("Automobiles", R.drawable.car);
        categoryImageMap.put("Toys", R.drawable.toy);

        return categoryImageMap;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            imageView = itemView.findViewById(R.id.imageView2);
        }
    }
}

