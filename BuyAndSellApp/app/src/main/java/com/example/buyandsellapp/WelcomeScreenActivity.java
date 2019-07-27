package com.example.buyandsellapp;

import android.app.usage.NetworkStats;
import android.os.Bundle;
import android.content.Intent;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WelcomeScreenActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference mRef;
    private Button viewProductButton;
    private Button uploadProductButton;
    private Button buttonLogout;
    private Button buttonViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomescreen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        viewProductButton= findViewById(R.id.viewProductButton);
        uploadProductButton = findViewById(R.id.uploadProductButton);
        viewProductButton.setOnClickListener(this);
        uploadProductButton.setOnClickListener(this);
        buttonLogout=findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("loggingout","loggingout");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent();
                intent.setClass(WelcomeScreenActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        buttonViewCart=findViewById(R.id.buttonViewCart);
        buttonViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("buttonViewCart","buttonViewCart");
                Intent intent = new Intent();
                intent.setClass(WelcomeScreenActivity.this,WishListActivity.class);
                startActivity(intent);
            }
        });
    }


    public void onStart() {
        super.onStart();
        // Check auth on Activity start
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.viewProductButton) {
            Intent intent = new Intent();
            Log.d("clickedProductList","clickedProductList");
            intent.setClass(WelcomeScreenActivity.this, ProductListActivity.class);
            startActivity(intent);
        } else if (i == R.id.uploadProductButton) {
            Intent intent = new Intent();
            Log.d("clickeduploadProduct","clickeduploadProduct");
            intent.setClass(WelcomeScreenActivity.this, ProductUploadActivity.class);
            startActivity(intent);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        return true;
    }

}
