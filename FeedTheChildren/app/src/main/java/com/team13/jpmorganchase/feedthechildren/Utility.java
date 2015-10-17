package com.team13.jpmorganchase.feedthechildren;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 *Created by Issac on 9/23/2015.
 */
public class Utility {



    public static String getName(ParseUser user){
        return (user.getString("First_Name") + " " + user.getString("Last_Name"));
    }
    public static class Location {

        private String locationID;
        private ParseObject location;
        String name;
        String address;

        Location(String pParseObjectID, ParseObject pLocation, String pName, String pAddress){
            locationID = pParseObjectID;
            location = pLocation;
            name = pName;
            address = pAddress;
        }
        public String getLocationID(){
            return locationID;
        }
        public ParseObject getLocationObject(){
            return location;
        }

    }

    public static ArrayList<ParseObject> getLocationList(String[] array, double lat, double lng, double radius){
        final ArrayList<ArrayList<ParseObject>> results = new ArrayList<>(1);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Locations");
        for(int i = 0;i<array.length;i++){
                query.whereEqualTo("categories",array[i]);
        }
        query.whereWithinRadians("latLng", new ParseGeoPoint(lat, lng), radius);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for( ParseObject po : list){
                        results.get(0).add(po);
                    }
                }
            }
        });
        return results.get(0);
    }

    private static List<Location> pLocationList = null;
    private static boolean changedRecord = true;

    public static List<Location> convertToLocation (List<ParseObject> rawList){
        if(pLocationList == null || changedRecord) {                                                     //need to test the case if the list in userA change if
            List<Utility.Location> locationList = new ArrayList<>();

            if(rawList==null){
                return new ArrayList<>();
            }
            for (int i = 0; i < rawList.size(); i++) {
                try {
                    ParseObject locationObject = rawList.get(i).fetch();
                    locationList.add(new Location(locationObject.getObjectId(), locationObject,
                            locationObject.getString("name"), locationObject.getString("address")));
                } catch (ParseException e) {
                    Log.d("Fetch", e.getMessage());
                }
            }
            pLocationList = new ArrayList<>(locationList);
            changedRecord = false;
            return locationList;
        }
        return pLocationList;
    }


    public static void resetExistingLocationList(){ pLocationList = null; }
    public static void setChangedRecord(){ changedRecord = true; }

    public static boolean checkNewEntry(){
        try {
            return ParseUser.getCurrentUser().getParseObject("newEntry").fetch().getBoolean("newEntry");
        }catch (ParseException e) {
            Log.d("checkNewEntry", e.getMessage());
        }
        return true;
    }

    public static void editNewEntry(ParseUser user, final boolean newResult){
        user.getParseObject("newEntry").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                parseObject.put("newEntry", newResult);
                parseObject.saveInBackground();
            }
        });
    }

    public static ParseObject getListLocation(){
        try {
            return ParseUser.getCurrentUser().getParseObject("newEntry").fetchIfNeeded();
        }catch (ParseException e) {
            Log.d("checkNewEntry", e.getMessage());
        }
        return new ParseObject("Location_update");
    }


}
