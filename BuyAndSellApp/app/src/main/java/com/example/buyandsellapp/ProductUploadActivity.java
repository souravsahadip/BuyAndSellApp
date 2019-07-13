package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class ProductUploadActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private TextView productName;
    private TextView productPrice;
    private TextView productCategory;
    private TextView productCondition;
    private Button buttonLogout;
    private Button buttonProductUpload;
    String productSellerName;
    FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productupload);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Product");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        productName = (TextView) findViewById(R.id.inputProductName);
        productPrice = (TextView) findViewById(R.id.inputProductPrice);
        productCategory = (TextView) findViewById(R.id.inputProducCategory);
        productCondition = (TextView) findViewById(R.id.inputProductCondition);

        buttonProductUpload = findViewById(R.id.buttonUploadProduct);
        buttonProductUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("uploadingProduct", "uploadingProduct");
                Product product = new Product(productName.getText().toString(),
                        Double.parseDouble(productPrice.getText().toString()),
                        productCondition.getText().toString(), uid,
                        productCategory.getText().toString());
                mRef.push().setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent();
                        intent.setClass(ProductUploadActivity.this, WelcomeScreenActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductUploadActivity.this, "Product Upload Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                /*if(mRef.push().setValue(product).isSuccessful()==true){

                }
                else {

                }*/
            }
        });


        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("loggingout", "loggingout");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent();
                intent.setClass(ProductUploadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    public void onStart() {
        super.onStart();
        // Check auth on Activity start
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(ProductUploadActivity.this, WelcomeScreenActivity.class);
        startActivity(intent);
    }
}
