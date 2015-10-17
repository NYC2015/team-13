package com.team13.jpmorganchase.feedthechildren;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HuMengpei on 10/17/2015.
 */
public class AddLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_add_location_activity);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Add Location");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }
}