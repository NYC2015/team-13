package com.team13.jpmorganchase.feedthechildren;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Issac on 10/17/2015.
 */
public class MyLocationActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Location finder");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
