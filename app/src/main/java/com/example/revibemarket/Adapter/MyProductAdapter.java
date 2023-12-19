package com.example.revibemarket.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.EditActivity;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.CartSession;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.example.revibemarket.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ViewHolder> {
    private List<Product> productList;

    public MyProductAdapter() {
        this.productList = ProductSingleton.getInstance().getProductList()
                .stream()
                .filter(product -> product.getUserID().equals(UserSession.getInstance().getId()))
                .collect(Collectors.toList());

    }
    @NonNull
    @Override
    public MyProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        return new MyProductAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyProductAdapter.ViewHolder holder, int position) {
            holder.productNameTextView.setText(productList.get(position).getProductName());
            holder.textViewDiscount.setText("Discount: " + productList.get(position).getProductType().getDiscount()+"%");
            holder.tvPrice.setText("$" + productList.get(position).getProductType().getPrice());
            holder.tvProductQuantity.setText("Quantity : " + productList.get(position).getProductType().getStock());
            Glide.with(holder.imageView.getContext())
                .load(productList.get(position).getProductType().getImages().get(0))
                .error(R.drawable.sofa_cut)
                .override(330, 330)
                .into(holder.imageView);

    }

    @Override public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView productNameTextView;
        TextView tvPrice,textViewDiscount,tvPriceAfter;
        TextView tvProductQuantity;
        Button btnRemove,btnEdit;
        ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageView = itemView.findViewById(R.id.imgProduct);
            textViewDiscount = itemView.findViewById(R.id.textViewDiscount);
            tvProductQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnEdit = itemView.findViewById(R.id.buttonEdit);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sku = productList.get(getAdapterPosition()).getSku();
                    Product product = productList.get(getAdapterPosition());
                    productList.remove(getAdapterPosition());
                    ProductSingleton.getInstance().setModify(true);
                    notifyDataSetChanged();
                    //Toast.makeText(imageView.getContext(), productList.get(getAdapterPosition()).getSku(),Toast.LENGTH_SHORT).show();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query query = ref.child("products").orderByChild("sku").equalTo(sku);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                                appleSnapshot.getRef().removeValue();
                                Toast.makeText(imageView.getContext(), "Delete successful !",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(imageView.getContext(), "Delete failed !",Toast.LENGTH_SHORT).show();
                        }
                    });

                   StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Images_Product/" + sku);
                   storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                       @Override
                       public void onSuccess(ListResult listResult) {
                           for (StorageReference item : listResult.getItems()) {
                               item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       Toast.makeText(imageView.getContext(), "Delete image successful !",Toast.LENGTH_SHORT).show();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(imageView.getContext(), "Delete image 1 failed !",Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(imageView.getContext(), "Delete image failed !",Toast.LENGTH_SHORT).show();
                       }
                   });

                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), EditActivity.class);
                    intent.putExtra("sku",productList.get(getAdapterPosition()).getSku());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
