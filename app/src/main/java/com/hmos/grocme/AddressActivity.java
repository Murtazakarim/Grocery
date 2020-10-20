package com.hmos.grocme;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.TextView;

import com.Adapter.AddressFragment;

public class AddressActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);



        TextView textView = (TextView)toolbar.findViewById(R.id.text);
        textView.setText("My Addresses");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Fragment fm = new AddressFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    /**
     * Method to make json object request where json response starts wtih
     */

}
