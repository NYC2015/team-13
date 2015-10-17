package com.team13.jpmorganchase.feedthechildren;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * Created by Issac on 10/17/2015.
 */
public class MyLocationActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locations);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Location finder");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView listview = (ListView) findViewById(R.id.lv);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_setting:

                Intent intent = new Intent(MyLocationActivity.this, AddLocationActivity.class);
                startActivity(intent);


                break;

        }


        return super.onOptionsItemSelected(item);
    }
}
