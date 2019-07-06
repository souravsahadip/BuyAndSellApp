package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductViewActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mRef;
    private DatabaseReference productIDref;
    private TextView productName;
    private TextView productPrice;
    private TextView productSeller;
    private Button buttonLogout;
    String productSellerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdesc);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        Intent intent = getIntent();
        productIDref = database.getReferenceFromUrl( intent.getStringExtra("productID"));
        Log.d("productID",productIDref.toString());

        productName=(TextView)findViewById(R.id.productdescName);
        productPrice=(TextView)findViewById(R.id.productdescPrice);
        productSeller=(TextView)findViewById(R.id.productdescSeller);

        buttonLogout=findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("loggingout","loggingout");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent();
                intent.setClass(ProductViewActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        productIDref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Product product = dataSnapshot.getValue(Product.class);
                productName.setText(product.getProductName());
                productPrice.setText(Double.toString(product.getPrice()));
                 productSellerName="";
                mRef.child("User").child( product.getUid()).child("fullName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            if (snapshot.getValue() != null) {
                                try {
                                    Log.e("TAG", "" + snapshot.getValue()); // your name values you will get here
                                    productSellerName=snapshot.getValue().toString();
                                    productSeller.setText(productSellerName);
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
                productSeller.setText(productSellerName);
                //add location
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DatabaseError", "Failed to read value.", error.toException());
            }
        });
    }


    public void onStart() {
        super.onStart();
        // Check auth on Activity start
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

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.setClass(ProductViewActivity.this,WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
