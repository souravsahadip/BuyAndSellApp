package com.example.buyandsellapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

public class ProductUploadActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private TextView productName;
    private TextView productPrice;
    private  Spinner spinner1,spinner2 ;
    private Button buttonLogout;
    private Button buttonProductUpload;
    String productSellerName;
    FirebaseAuth mAuth;
    String uid;
    private Button btnChoose;
    private ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    String productID;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productupload);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Product");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        productName = (TextView) findViewById(R.id.inputProductName);
        productPrice = (TextView) findViewById(R.id.inputProductPrice);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
       // productCategory = (TextView) findViewById(R.id.inputProducCategory);
        //productCondition = (TextView) findViewById(R.id.inputProductCondition);

        buttonProductUpload = findViewById(R.id.buttonUploadProduct);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        imageView = (ImageView) findViewById(R.id.imgView);

        buttonProductUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm() && filePath != null) {
                    Log.d("uploadingProduct", "uploadingProduct");
                    productID = UUID.randomUUID().toString();

                    final ProgressDialog progressDialog = new ProgressDialog(ProductUploadActivity.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    Log.d("filepath", filePath.toString());
                    StorageReference ref = storageReference.child("images/" + productID);
                    ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProductUploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Uri downloadUrl;
                            storageReference.child("images/" + productID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("uri", "onSuccess: uri= " + uri.toString());

                                    //create data
                                    Product product = new Product(productName.getText().toString(),
                                            Double.parseDouble(productPrice.getText().toString()),
                                            String.valueOf(spinner2.getSelectedItem()), uid,
                                            String.valueOf(spinner1.getSelectedItem()),uri.toString());
                                    mRef.child(productID).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mRef.child(productID).child(uid).setValue(true);
                                            Intent intent = new Intent();
                                            intent.setClass(ProductUploadActivity.this, ConfirmationActivity.class);
                                            intent.putExtra("message","Product Uploaded Successfully");
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProductUploadActivity.this, "Product Upload Failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProductUploadActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });


                } else {
                    Toast.makeText(ProductUploadActivity.this, "Please fill all required fields!",
                            Toast.LENGTH_SHORT).show();
                }
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

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);

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

    private boolean validateForm() {

        boolean result = true;
        if (TextUtils.isEmpty(productName.getText().toString())) {
            productName.setError("Required");
            result = false;
        } else {
            productName.setError(null);
        }

        if (TextUtils.isEmpty(String.valueOf(spinner1.getSelectedItem()))) {
            //spinner1.setError("Required");
            result = false;
        } else {
           // productCategory.setError(null);
        }

        if (TextUtils.isEmpty(String.valueOf(spinner2.getSelectedItem()))) {
            //spinner2.setError("Required");
            result = false;
        } else {
            // productCategory.setError(null);
        }


        if (TextUtils.isEmpty(productPrice.getText().toString())) {
            productPrice.setError("Required");
            result = false;
        } else {
            productPrice.setError(null);
        }

        if (TextUtils.isEmpty(filePath.toString())) {
            Toast.makeText(ProductUploadActivity.this, "Please Choose a photo", Toast.LENGTH_SHORT).show();
            result = false;
        } else {

        }

        return result;
    }

}
