package com.example.revibemarket.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExploreProductAdapter extends RecyclerView.Adapter<ExploreProductAdapter.ViewHolder> implements Filterable {
    private List<Product> productList;
    private List<Product> originalList;
    private OnItemClickListener onItemClickListener;

    public ExploreProductAdapter(Context context, List<Product> productList) {
        this.productList = productList;
        this.originalList = productList;
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

        holder.txtName.setText(item.getProductName());
        holder.txtPrice.setText( "$" + item.getProductType().getPrice());
        holder.txtPrice.setPaintFlags(holder.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtNewPrice.setText("$" + item.getProductType().getPrice()*(100-item.getProductType().getDiscount())/100);
        holder.txtDiscount.setText("-" + item.getProductType().getDiscount() + "%");

        Log.d("Product img",productList.get(position).getProductType().getImages().size()+"");
        if (!productList.get(position).getProductType().getImages().isEmpty()) {
            Glide.with(holder.imageView.getContext())
                    .load(productList.get(position).getProductType().getImages().get(0))
                    .error(R.drawable.avatar)
                    .override(330, 330)
                    .into(holder.imageView);
        }

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
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


    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productList = (List<Product>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Product> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = originalList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

        };

    }

    private List<Product> getFilteredResults(String toLowerCase) {
        List<Product> results = new ArrayList<>();

        for (Product item : originalList) {
            if (item.getProductName().toLowerCase().contains(toLowerCase)) {
                results.add(item);
            }
        }
        return results;
    }

    public Filter getFilterCategory() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productList = (List<Product>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Product> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = originalList;
                } else {
                    filteredResults = getFilteredCategory(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

        };

    }

    private List<Product> getFilteredCategory(String toLowerCase) {
        List<Product> results = new ArrayList<>();
        List<String> resultList = splitInsideSquareBrackets(toLowerCase);
        if (toLowerCase.equals("[]")) {
            return originalList;
        }

        for (Product item : originalList) {
            if (resultList.contains(item.getCategory().toLowerCase())) {
                results.add(item);
            }
        }
        return results;
    }

    private static List<String> splitInsideSquareBrackets(String input) {
        List<String> resultList = new ArrayList<>();

        // Check if the input has square brackets
        if (input.startsWith("[") && input.endsWith("]")) {
            // Remove square brackets and split by commas
            String contentInsideBrackets = input.substring(1, input.length() - 1);
            String[] items = contentInsideBrackets.split(",\\s*");

            // Add items to the result list
            resultList.addAll(Arrays.asList(items));
        }

        return resultList;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtPrice;
        public TextView txtDiscount;
        public ImageView imageView;
        public TextView txtNewPrice;
        public Button btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNewPrice = itemView.findViewById(R.id.tv_new_price);
            txtName = itemView.findViewById(R.id.BestDealTiTle);
            txtPrice = itemView.findViewById(R.id.tv_new_price);
            txtDiscount = itemView.findViewById(R.id.tv_discount);
            imageView = itemView.findViewById(R.id.BestDealImg);
            btnDetails = itemView.findViewById(R.id.btn_see_product);

        }
    }
}