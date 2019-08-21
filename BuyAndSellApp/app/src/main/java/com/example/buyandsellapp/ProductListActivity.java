package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ProductListActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mRef;
    ListView listView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private Spinner spinner1;
    private Button buttonFilter;
    private Query query;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        buttonFilter = findViewById(R.id.buttonFilter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Product");
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.productListView);
        linearLayoutManager = new LinearLayoutManager(ProductListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setHasFixedSize(true);
        try {
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    //.child("Product").orderByChild("uid");
                    .child("Product").orderByChild(FirebaseAuth.getInstance().getUid()).equalTo(null);

            buttonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    query = FirebaseDatabase.getInstance().getReference()
                            .child("Product").orderByChild("category")
                            .equalTo(String.valueOf(spinner1.getSelectedItem()));
                    fetch();
                }
            });
            fetch();
        } catch (Exception e) {
            Log.e("null", e.toString());
        }
    }

    private void fetch() {

        Log.d("fetch", "fetch");
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();
        Log.d("beforeAdapter", "beforeAdapter");
        adapter = new FirebaseRecyclerAdapter<Product, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, Product model) {
                if(model.getUid().equals(mAuth.getUid())==false){
                    Log.d("insideAdapter", "insideAdapter");

                    holder.setTxtTitle(model.getProductName());
                    holder.setTxtDesc(model.getCategory());

                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Log.d("ClickedOn", Integer.toString(holder.getAdapterPosition()));
                            Intent intent = new Intent();
                            intent.setClass(ProductListActivity.this, ProductViewActivity.class);
                            intent.putExtra("productID", adapter.getRef(holder.getAdapterPosition()).toString());
                            startActivity(intent);
                        }
                    });
                }
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

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ProductListActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
