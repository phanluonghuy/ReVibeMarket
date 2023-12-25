package com.example.revibemarket.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.Models.Order;
import com.example.revibemarket.Models.OrderItem;
import com.example.revibemarket.Models.User;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.example.revibemarket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MySoldAdapter extends RecyclerView.Adapter<MySoldAdapter.ViewHolder> {

    public List<OrderItem> orderItems = new ArrayList<>();

    private List<User> userList = new ArrayList<>();
    private TextView textView;
    public MySoldAdapter(TextView textView) {
        this.textView = textView;
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference();
        Query orderQuery = orderRef.child("orders").orderByChild("customerId");
        orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Product with productTypeSku exists, update it
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // Perform the update in the database
                        Order order = productSnapshot.getValue(Order.class);
                        String customerID = order.getCustomerId();
                        for (OrderItem orderItem: order.getItems()) {
                           if (orderItem.getSellerID().equals(UserSession.getInstance().getId())) {
                               orderItems.add(orderItem);
                               Query userQuery = orderRef.child("users").orderByChild("id").equalTo(customerID);
                               userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                           User user = userSnapshot.getValue(User.class);
                                           userList.add(user);
                                       }
                                       notifyDataSetChanged();
                                       textView.setText(userList.size()+"");
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {

                                   }
                               });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

    }
    @NonNull
    @Override
    public MySoldAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mysoldproduct_item_layout , parent, false);
        return new MySoldAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MySoldAdapter.ViewHolder holder, int position) {
        try {

                OrderItem orderItem = orderItems.get(position);
                holder.productNameTextView.setText(orderItem.getProductTitle());
                holder.textViewDiscount.setText("Discount: " + orderItem.getDiscount() +"%");
                holder.tvPrice.setText("$" + orderItem.getPrice());
                holder.tvProductQuantity.setText("Quantity : " + orderItem.getQuantity());
                holder.tvTotal.setText("Total " + orderItem.getQuantity()*(orderItem.getPrice()*(orderItem.getPrice()*(100-orderItem.getDiscount()))/100));

                holder.userName.setText("Name : " + userList.get(position).getName());
                holder.userAddress.setText("Address : " + userList.get(position).getAddress());
                holder.userPhone.setText("Phone : " + userList.get(position).getPhone());
                holder.userEmail.setText("Email : " + userList.get(position).getEmail());

                String image = ProductSingleton.getInstance().getProductList()
                        .stream()
                        .filter(product -> orderItem.getSku().equals(product.getSku()))
                        .findFirst()
                        .get()
                        .getProductType()
                        .getImages()
                        .get(0);
                Glide.with(holder.imageView.getContext())
                        .load(image)
                        .error(R.drawable.sofa_cut)
                        .override(330, 330)
                        .into(holder.imageView);


        } catch (Exception e){

        }
    }

    @Override public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView productNameTextView;
        TextView tvPrice,textViewDiscount,tvPriceAfter,userName,userAddress,userPhone,userEmail,tvTotal;
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
            userName = itemView.findViewById(R.id.userName);
            userAddress = itemView.findViewById(R.id.userAddress);
            userPhone = itemView.findViewById(R.id.userPhone);
            userEmail = itemView.findViewById(R.id.userEmail);
            tvTotal = itemView.findViewById(R.id.tvTotal);
//            btnRemove = itemView.findViewById(R.id.btnRemove);
//            btnEdit = itemView.findViewById(R.id.buttonEdit);

//            btnRemove.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String sku = productList.get(getAdapterPosition()).getSku();
//                    productList.remove(getAdapterPosition());
//                    ProductSingleton.getInstance().setModify(true);
//                    notifyDataSetChanged();
//                    //Toast.makeText(imageView.getContext(), productList.get(getAdapterPosition()).getSku(),Toast.LENGTH_SHORT).show();
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//                    Query query = ref.child("products").orderByChild("sku").equalTo(sku);
//                    query.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
//                                appleSnapshot.getRef().removeValue();
//                                Toast.makeText(imageView.getContext(), "Delete successful !",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(imageView.getContext(), "Delete failed !",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                   StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Images_Product/" + sku);
//                   storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                       @Override
//                       public void onSuccess(ListResult listResult) {
//                           for (StorageReference item : listResult.getItems()) {
//                               item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                   @Override
//                                   public void onSuccess(Void unused) {
//                                       Toast.makeText(imageView.getContext(), "Delete image successful !",Toast.LENGTH_SHORT).show();
//                                   }
//                               }).addOnFailureListener(new OnFailureListener() {
//                                   @Override
//                                   public void onFailure(@NonNull Exception e) {
//                                       Toast.makeText(imageView.getContext(), "Delete image 1 failed !",Toast.LENGTH_SHORT).show();
//                                   }
//                               });
//                           }
//                       }
//                   }).addOnFailureListener(new OnFailureListener() {
//                       @Override
//                       public void onFailure(@NonNull Exception e) {
//                           Toast.makeText(imageView.getContext(), "Delete image failed !",Toast.LENGTH_SHORT).show();
//                       }
//                   });
//
//                }
//            });
//            btnEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(itemView.getContext(), EditActivity.class);
//                    intent.putExtra("sku",productList.get(getAdapterPosition()).getSku());
//                    itemView.getContext().startActivity(intent);
//                }
//            });
        }
    }
}
