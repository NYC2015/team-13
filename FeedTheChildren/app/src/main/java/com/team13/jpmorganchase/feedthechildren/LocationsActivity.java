package com.team13.jpmorganchase.feedthechildren;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import static com.team13.jpmorganchase.feedthechildren.Utility.getLocationList;

/**
 *Created by HuMengpei on 9/30/2015.
 */

public class LocationsActivity extends AppCompatActivity{
    LocationsActivity activityReference = this;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationAdaptor adapter;
    private String[] categories;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        buildGoogleApiClient(this);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setElevation(0);
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
    }
    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        categories = getIntent().getStringArrayExtra("categories");
                        ArrayList<ParseObject> list;
                        if(mLastLocation!=null) {
                            System.out.println("mLastLocation not null");
                            list = getLocationList(categories, mLastLocation.getLatitude(), mLastLocation.getLongitude(), 5);
                        }else{
                            System.out.println("mLastLocation null");
                            list = getLocationList(categories, 42.85, 78.77, 5);
                        }


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
            name.setText(currentItem.name);
            TextView address = (TextView) convertView.findViewById(R.id.location_address);
            address.setText(currentItem.address);
            Button view = (Button) convertView.findViewById(R.id.button_view);

            view.setTag(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    Utility.Location currentItem = mObject.get(position);
                    Intent intent = new Intent(LocationsActivity.this, ItemsInLocationActivity.class);
                    intent.putExtra("locationID",currentItem.getLocationID());
                }
            });
            return convertView;
        }
    }
}
