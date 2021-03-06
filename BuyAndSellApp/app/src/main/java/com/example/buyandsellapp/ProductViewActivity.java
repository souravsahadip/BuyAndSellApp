package com.example.buyandsellapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ProductViewActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference mRef;
    private DatabaseReference productIDref;
    private TextView productName;
    private TextView productPrice;
    private TextView productSeller;
    private Button buttonLogout;
    private Button buttonAddtoWishlist;
    private Button buttonAddtoCart;
    private ImageView carticon;
    String productSellerName;
    String productID;
    FirebaseAuth mAuth;
    private ImageView productImage;
    StorageReference mstorageRef;
    File localFile;
    Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdesc);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference();
        mstorageRef = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        productID = intent.getStringExtra("productID");
        productIDref = database.getReferenceFromUrl(productID);
        Log.d("productID", productIDref.toString());

        productName = (TextView) findViewById(R.id.productdescName);
        productPrice = (TextView) findViewById(R.id.productdescPrice);
        productSeller = (TextView) findViewById(R.id.productdescSeller);
        productImage = (ImageView) findViewById(R.id.imageView);

        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setWindowHeight(800); // Optional
        imagePopup.setWindowWidth(800); // Optional
        //imagePopup.setBackgroundColor(Color.TRANSPARENT);  // Optional
        //imagePopup.setFullScreen(true); // Optional
        //imagePopup.setImageBitmap(myBitmap);
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("loggingout", "loggingout");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent();
                intent.setClass(ProductViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        productIDref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Product product = dataSnapshot.getValue(Product.class);
                productName.setText(product.getProductName());
                productPrice.setText(Double.toString(product.getPrice()));
                productSellerName = "";
                mRef.child("User").child(product.getUid()).child("fullName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        try {
                            if (snapshot.getValue() != null) {
                                try {
                                    Log.e("TAG", "" + snapshot.getValue()); // your name values you will get here
                                    productSellerName = snapshot.getValue().toString();
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
                // productSeller.setText(mRef.child("User").child( product.getUid()).child("fullName").getClass().toString());
                //add location
                //Show image

                try {
                    localFile = File.createTempFile("images", "jpg");
                    mstorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getImageuri());
                    mstorageRef.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Successfully downloaded data to local file
                                    // ...
                                    myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    productImage.setImageBitmap(myBitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...
                        }
                    });


                    imagePopup.initiatePopupWithGlide(product.getImageuri());


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DatabaseError", "Failed to read value.", error.toException());
            }
        });


        buttonAddtoWishlist = findViewById(R.id.buttonAddtoWishlist);
        buttonAddtoWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child("Wishlist").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Map<String, Object> map = (Map) snapshot.getValue();
                            boolean r = map.containsKey(productIDref.getKey());
                            if (r == true) {        //Already in Wishlist
                                Toast.makeText(ProductViewActivity.this, "Already added to Wishlist",
                                        Toast.LENGTH_SHORT).show();
                            } else {          //Not in wishlist,add to wishlist
                                Date currentTime = Calendar.getInstance().getTime();
                                mAuth = FirebaseAuth.getInstance();
                                //CartItem cartItem = new CartItem(currentTime, productIDref.getKey(), mAuth.getUid());

                                mRef.child("Wishlist").child(FirebaseAuth.getInstance().getUid())
                                        .child(productIDref.getKey()).setValue(productName.getText()).
                                        // .push().setValue(productIDref.getKey()).
                                                addOnSuccessListener(new OnSuccessListener<Void>() {

                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProductViewActivity.this, "Added To Wishlist",
                                                        Toast.LENGTH_SHORT).show();
                                                //Intent intent = new Intent();
                                                //intent.setClass(ProductViewActivity.this, ProductListActivity.class);
                                                //startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProductViewActivity.this, "Cannot add to Wishlist",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            Log.d("Wishlist", map.containsKey(productIDref.getKey()) + " ");
                        } else {

                            Log.e("Wishist", " it's null.");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("onCancelled", " cancelled");
                    }
                });

            }
        });

        buttonAddtoCart = findViewById(R.id.buttonAddtoCart);
        buttonAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child("Cart").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Map<String, Object> map = (Map) snapshot.getValue();
                            boolean r = map.containsKey(productIDref.getKey());
                            if (r == true) {        //Already in Cart
                                Toast.makeText(ProductViewActivity.this, "Already added to Cart",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProductViewActivity.this, "Cart Full",
                                        Toast.LENGTH_SHORT).show();
                            }

                            Log.d("cart", map.containsKey(productIDref.getKey()) + " ");
                        } else {    //No item in cart,add one
                            Log.d("cart", " it's null.");
                            mRef.child("Cart").child(FirebaseAuth.getInstance().getUid())
                                    .child(productIDref.getKey()).setValue(productName.getText()).
                                    // .push().setValue(productIDref.getKey()).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProductViewActivity.this, "Added To Cart",
                                                    Toast.LENGTH_SHORT).show();
                                            //Intent intent = new Intent();
                                            //intent.setClass(ProductViewActivity.this, ProductListActivity.class);
                                            //startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProductViewActivity.this, "Cannot add to Cart",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.e("onCancelled", " cancelled");
                    }
                });
            }
        });


        carticon = (ImageView) findViewById(R.id.cartIcon);
        carticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("carticon", "carticon");
                Intent intent = new Intent();
                intent.setClass(ProductViewActivity.this, CartListActivity.class);
                startActivity(intent);
            }
        });

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                imagePopup.viewPopup();

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
    public void onBackPressed() {
        //Intent intent = getIntent();
        //intent.setClass(ProductViewActivity.this, getIntent().getClass());
        Intent intent = new Intent();
        intent.setClass(ProductViewActivity.this, ProductListActivity.class);

        startActivity(intent);
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
