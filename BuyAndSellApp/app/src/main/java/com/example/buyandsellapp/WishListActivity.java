package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class WishListActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mRef;
    ListView listView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartlist);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Cart");
        recyclerView = findViewById(R.id.cartListView);
        linearLayoutManager = new LinearLayoutManager(WishListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        fetch();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Cart")
                .child(FirebaseAuth.getInstance().getUid());

        Log.d("fetch", "fetch");
        /*ObservableSnapshotArray<String> snaps=new ObservableSnapshotArray<String>() {
            @NonNull
            @Override
            protected List<DataSnapshot> getSnapshots() {
                return null;
            }
        };*/
        //Iterable<DataSnapshot> contactChildren = contactSnapshot.getChildren();
        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(query, String.class)
                        .build();
        Log.d("beforeAdapter", "beforeAdapter");
        adapter = new FirebaseRecyclerAdapter<String, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, String model) {
                Log.d("onBindViewHolder", adapter.getRef(holder.getAdapterPosition()).getKey());
                // holder.setTxtTitle(model.getProductID());
                // holder.setTxtDesc(model.getTimestamp().toString());

               FirebaseDatabase.getInstance().getReference().child("Product").
                       child(adapter.getRef(holder.getAdapterPosition()).getKey())
                        .addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                try {
                                    if (snapshot.getValue() != null) {
                                        try {
                                            Log.d("productName", "" + snapshot.getValue()); // your name values you will get here
                                            Product prod = snapshot.getValue(Product.class);
                                            holder.setTxtTitle(prod.getProductName());
                                            holder.setTxtDesc(Double.toString(prod.getPrice()));
                                            Log.d("title set","title set");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.e("TAG", " it's null.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                                Log.e("onCancelled", " cancelled");
                            }
                        });


                /*FirebaseDatabase.getInstance().getReference().child("Product").
                        child(model).child("productName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            if (snapshot.getValue() != null) {
                                try {
                                    Log.e("productName", "" + snapshot.getValue()); // your name values you will get here
                                    productName = snapshot.getValue().toString();
                                    holder.setTxtTitle(productName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e("TAG", " it's null.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("onCancelled", " cancelled");
                    }
                });
                */

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  Toast.makeText(ProductListActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        //+adapter.getRef(1)
                        //Intent intent = new Intent();
                        //intent.setClass(CartListActivity.this, ProductViewActivity.class);
                        //intent.putExtra("productID",adapter.getRef(holder.getAdapterPosition()).toString());
                        //startActivity(intent);
                    }
                });

            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public void onStart() {
        super.onStart();
        // Check auth on Activity start
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClick(View view) {
        //Intent intent=new Intent();
        //intent.setClass(this,ThirdScreenActivity.class);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        Log.d("gotAuthData", mAuth.getUid());
        // startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView txtTitle;
        public TextView txtDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }


        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(WishListActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
