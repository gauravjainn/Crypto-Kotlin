package com.siltech.cryptochat.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.siltech.cryptochat.R;

public class Picker extends AppCompatActivity {

    Button one, two;

    public boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNW = cm.getActiveNetworkInfo();
        if (activeNW != null && activeNW.isConnected()) {
            return true;
        }
        return false;
    }


    @Override
    protected void onStart() {


        if(hasConnection(this)) {
            Toast.makeText(this, "Active networks OK ", Toast.LENGTH_LONG).show();
        }
        else  Toast.makeText(this, "No active networks... ", Toast.LENGTH_LONG).show();
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);


        one = findViewById(R.id.btn_add_group);
        two = findViewById(R.id.add_contact);


        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add group
            }
        });


        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add contact

                Intent intent = new Intent(getApplicationContext(), Add.class);
                startActivity(intent);


            }
        });
    }
}