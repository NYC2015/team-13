package com.team13.jpmorganchase.feedthechildren;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Issac on 10/17/2015.
 */
public class CategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Categories");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button b = (Button) findViewById(R.id.searchButton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(CategoriesActivity.this, LocationsActivity.class);
                intent.putExtra("categories", new String[]{"fruit","vegetables","oils"});
                if(!((EditText)findViewById(R.id.radius)).getText().toString().isEmpty()){
                    intent.putExtra("radius", Integer.parseInt(((EditText)findViewById(R.id.radius)).getText().toString()));
                }
                else{
                    intent.putExtra("radius",5);
                }
                if(!((EditText)findViewById(R.id.keyword)).getText().toString().isEmpty()){
                    intent.putExtra("keyword",((EditText)findViewById(R.id.keyword)).getText().toString());}
                else{
                    intent.putExtra("keyword","");
                }
                startActivity(intent);




            }
        });
    }


}
