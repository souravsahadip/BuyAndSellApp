package com.example.buyandsellapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mDistrict;
    private EditText mConfPasswordField;
    private EditText mDateofbirth;
    private Button mSignUpButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        mAuth = FirebaseAuth.getInstance();
       // Log.d("mauth:",mAuth.getUid().toString());

        mSignUpButton=findViewById(R.id.buttonSignUp);
        mEmailField = (EditText) findViewById(R.id.fieldEmail);
        mPasswordField = (EditText) findViewById(R.id.fieldPassword);
        mConfPasswordField = (EditText) findViewById(R.id.fieldConfirmpwd);
        mFirstName = (EditText) findViewById(R.id.firstName);
        mLastName = (EditText) findViewById(R.id.LastName);
        mDistrict = (EditText) findViewById(R.id.district);
        mDateofbirth = (EditText) findViewById(R.id.dateofbirth);

        mSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Log.d("clicked","clicked");
        signUp();
    }

    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            try {
                onAuthSuccess(mAuth.getCurrentUser());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("Start","started");
        }
    }
    private void signUp() {
        if (!validateForm()) {
            Log.d("Invalid","hhhhhhhhhh");
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        Log.d("emailPwd",email+password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            try {
                                onAuthSuccess(task.getResult().getUser());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {

        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(mFirstName.getText().toString())) {
            mFirstName.setError("Required");
            result = false;
        } else {
            mFirstName.setError(null);
        }

        if (TextUtils.isEmpty(mLastName.getText().toString())) {
            mLastName.setError("Required");
            result = false;
        } else {
            mLastName.setError(null);
        }

       if (TextUtils.isEmpty(mConfPasswordField.getText().toString())) {
            mConfPasswordField.setError("Required");
            result = false;
        } else {
            mConfPasswordField.setError(null);
        }

        if (!mConfPasswordField.getText().toString().equals(mConfPasswordField.getText().toString())) {
            mConfPasswordField.setError("Password not matched");
            result = false;
        } else {
            mConfPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(mDistrict.getText().toString())) {
            mDistrict.setError("Required");
            result = false;
        } else {
            mDistrict.setError(null);
        }

        if (TextUtils.isEmpty(mDateofbirth.getText().toString())) {
            mDateofbirth.setError("Required");
            result = false;
        } else {
            mDateofbirth.setError(null);
        }
        return result;
    }

    private void onAuthSuccess(FirebaseUser user) throws ParseException {
        Log.d("OnAuth",user.getEmail());

        String username = usernameFromEmail(user.getEmail());
        String fullName= mFirstName.getText()+" "+mLastName.getText();
        String district=mDistrict.getText().toString();
        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");

       String dateString = format.format(new Date());

       try{
           Date dateOfBirth = format.parse(mDateofbirth.getText().toString());
           // Write new user
           Log.d("creating","User Created");
           writeNewUser(user.getUid(), username, user.getEmail(),fullName,district,dateOfBirth);
           // Go to MainActivity
           Log.d("userCreated","User Created");
           Intent intent = new Intent();
           intent.setClass(ProfileActivity.this, WelcomeScreenActivity.class);
           startActivity(intent);
       }
       catch (ParseException e){
           e.printStackTrace();
       }

        //finish();
    }

    private String usernameFromEmail(String email) {

        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(String userId, String name, String email, String fullName, String district, Date dateOfBirth) {
        User user = new User(name, email,fullName,district,dateOfBirth);
        mDatabase.child("User").child(userId).setValue(user);
    }


}
