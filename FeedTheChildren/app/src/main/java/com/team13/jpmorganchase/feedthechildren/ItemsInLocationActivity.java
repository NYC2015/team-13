package com.team13.jpmorganchase.feedthechildren;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *Created by HuMengpei on 9/30/2015.
 */

public class ItemsInLocationActivity extends AppCompatActivity{
    ItemsInLocationActivity activityReference = this;
    private LocationAdaptor adapter;
    private String[] categories;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        categories = getIntent().getStringArrayExtra("categories");
        //ArrayList<ParseObject> list = getLocationList(categories, mLastLocation.getLatitude(),mLastLocation.getLongitude(),5);
        //List<Utility.Location> locationList = Utility.convertToLocation(list);
        //List<Utility.Location> locationList = Utility.convertToLocation(list);
        List<Utility.Location> locationList = new ArrayList<Utility.Location>();
        try {
            locationList.add(new Utility.Location("a",new JSONObject("{a:b}"),"a","a"));
            locationList.add(new Utility.Location("a",new JSONObject("{a:b}"),"a","a"));
            locationList.add(new Utility.Location("a",new JSONObject("{a:b}"),"a","a"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListView view = (ListView) findViewById(R.id.location_list_view);
        adapter = new LocationAdaptor(activityReference, R.layout.location_item, locationList);
        view.setAdapter(adapter);

        LinearLayout layout = (LinearLayout)findViewById(R.id.EmptyListView);
        TextView text = (TextView)layout.findViewById(R.id.EmptyListViewText);
        text.setText("No Location");
        view.setEmptyView(layout);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.finish();
    }/*
    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);

                        categories = getIntent().getStringArrayExtra("categories");
                        ArrayList<ParseObject> list = getLocationList(categories, mLastLocation.getLatitude(),mLastLocation.getLongitude(),5);
                        List<Utility.Location> locationList = Utility.convertToLocation(list);
                        ListView view = (ListView) findViewById(R.id.location_list_view);
                        adapter = new LocationAdaptor(activityReference, R.layout.location_item, locationList);
                        view.setAdapter(adapter);

                        LinearLayout layout = (LinearLayout)findViewById(R.id.EmptyListView);
                        TextView text = (TextView)layout.findViewById(R.id.EmptyListViewText);
                        text.setText("No Location");
                        view.setEmptyView(layout);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
                        // onConnectionFailed.
                        Log.i("Map", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }
*/



    public class LocationAdaptor extends ArrayAdapter<Utility.Location>{

        Context mContext;
        int mResource;
        List<Utility.Location> mObject;
        private TextView[] textCollection;

        public LocationAdaptor(Context context, int resource, List<Utility.Location> objects){
            super(context, resource, objects);
            mContext = context;
            mResource = resource;
            mObject = objects;
            textCollection = new TextView[objects.size() + 1];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Utility.Location currentItem = mObject.get(position);
            if(convertView == null){
                convertView = getLayoutInflater().inflate(mResource, parent, false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.location_name);
            if(position==0){
                name.setText("Apple");
            }else if(position==1){
                name.setText("Orange");
            }else if(position==2){
                name.setText("Pork");
            }/*
            TextView address = (TextView) convertView.findViewById(R.id.location_address);
            if(position==0){
                address.setText("Address1");
            }else if(position==1){
                address.setText("Address2");
            }else if(position==2){
                address.setText("Address3");
            }*/
            Button view = (Button) convertView.findViewById(R.id.button_view);

            view.setTag(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    Utility.Location currentItem = mObject.get(position);
                    Intent intent = new Intent(ItemsInLocationActivity.this, ItemDetailActivity.class);
                    intent.putExtra("locationID",currentItem.getLocationID());
                }
            });
            return convertView;
        }
    }
}
