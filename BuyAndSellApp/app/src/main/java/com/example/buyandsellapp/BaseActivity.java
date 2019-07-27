package com.example.buyandsellapp;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {

    // Activity code here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_uploaded_products){
            Intent intent = new Intent();
            intent.setClass(BaseActivity.this,UploadedProductListActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.menu_logout){
            // do something
            Log.d("loggingout","loggingout");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent();
            intent.setClass(BaseActivity.this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
