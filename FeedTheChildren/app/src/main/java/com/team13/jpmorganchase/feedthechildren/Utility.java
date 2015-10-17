package com.team13.jpmorganchase.feedthechildren;

import android.content.Context;
import android.util.Log;

import com.beust.jcommander.Parameter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

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
        private JSONObject location;
        String name;
        String address;

        Location(String pParseObjectID, JSONObject pLocation, String pName, String pAddress){
            locationID = pParseObjectID;
            location = pLocation;
            name = pName;
            address = pAddress;
        }
        public String getLocationID(){
            return locationID;
        }
        public JSONObject getLocationObject(){
            return location;
        }

    }
    public static class YelpAPI {

        private static final String API_HOST = "api.yelp.com";
        private static final String DEFAULT_TERM = "dinner";
        private static final String DEFAULT_LOCATION = "San Francisco, CA";
        private static final int SEARCH_LIMIT = 3;
        private static final String SEARCH_PATH = "/v2/search";
        private static final String BUSINESS_PATH = "/v2/business";

        /*
         * Update OAuth credentials below from the Yelp Developers API site:
         * http://www.yelp.com/developers/getting_started/api_access
         */
        private static final String CONSUMER_KEY = "uYYfdg3TbkgG_rmq8KmzlQ";
        private static final String CONSUMER_SECRET = "Jq1Lmu8p0fYzBVYkf7b8YimwQZU";
        private static final String TOKEN = "XDs90zWCXNSTzYDfVLAKZ515yFpQ5cz3";
        private static final String TOKEN_SECRET = "CZy5rsmLV88PZnoRa3Tn3y_mqZE";

        OAuthService service;
        Token accessToken;

        /**
         * Setup the Yelp API OAuth credentials.
         *
         * @param consumerKey Consumer key
         * @param consumerSecret Consumer secret
         * @param token Token
         * @param tokenSecret Token secret
         */
        public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
            this.service =
                    new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
                            .apiSecret(consumerSecret).build();
            this.accessToken = new Token(token, tokenSecret);
        }

        /**
         * Creates and sends a request to the Search API by term and location.
         * <p>
         * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
         * for more info.
         *
         * @param term <tt>String</tt> of the search term to be queried
         * @param location <tt>String</tt> of the location
         * @return <tt>String</tt> JSON Response
         */
        public String searchForBusinessesByLocation(String term, String location) {
            OAuthRequest request = createOAuthRequest(SEARCH_PATH);
            request.addQuerystringParameter("term", term);
            request.addQuerystringParameter("location", location);
            request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
            return sendRequestAndGetResponse(request);
        }

        /**
         * Creates and sends a request to the Business API by business ID.
         * <p>
         * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
         * for more info.
         *
         * @param businessID <tt>String</tt> business ID of the requested business
         * @return <tt>String</tt> JSON Response
         */
        public String searchByBusinessId(String businessID) {
            OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
            return sendRequestAndGetResponse(request);
        }

        /**
         * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
         *
         * @param path API endpoint to be queried
         * @return <tt>OAuthRequest</tt>
         */
        private OAuthRequest createOAuthRequest(String path) {
            OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
            return request;
        }

        /**
         * Sends an {@link OAuthRequest} and returns the {@link Response} body.
         *
         * @param request {@link OAuthRequest} corresponding to the API request
         * @return <tt>String</tt> body of API response
         */
        private String sendRequestAndGetResponse(OAuthRequest request) {
            System.out.println("Querying " + request.getCompleteUrl() + " ...");
            this.service.signRequest(this.accessToken, request);
            Response response = request.send();
            return response.getBody();
        }

        /**
         * Queries the Search API based on the command line arguments and takes the first result to query
         * the Business API.
         *
         * @param yelpApi <tt>YelpAPI</tt> service instance
         * @param yelpApiCli <tt>YelpAPICLI</tt> command line arguments
         */
        private static void queryAPI(YelpAPI yelpApi, YelpAPICLI yelpApiCli) {
            String searchResponseJSON =
                    yelpApi.searchForBusinessesByLocation(yelpApiCli.term, yelpApiCli.location);

            JSONParser parser = new JSONParser();
            JSONObject response = null;
            try {
                response = (JSONObject) parser.parse(searchResponseJSON);
            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }

            JSONArray businesses = null;
            try {
                businesses = (JSONArray) response.get("businesses");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject firstBusiness = (JSONObject) businesses.get(0);
            String firstBusinessID = null;
            try {
                firstBusinessID = firstBusiness.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(String.format(
                    "%s businesses found, querying business info for the top result \"%s\" ...",
                    businesses.size(), firstBusinessID));

            // Select the first business and display business details
            String businessResponseJSON = yelpApi.searchByBusinessId(firstBusinessID.toString());
            System.out.println(String.format("Result for business \"%s\" found:", firstBusinessID));
            System.out.println(businessResponseJSON);
        }

        /**
         * Command-line interface for the sample Yelp API runner.
         */
        private static class YelpAPICLI {
            @Parameter(names = {"-q", "--term"}, description = "Search Query Term")
            public String term = DEFAULT_TERM;

            @Parameter(names = {"-l", "--location"}, description = "Location to be Queried")
            public String location = DEFAULT_LOCATION;
        }
        public static void getLocationList(final ArrayList<JSONObject> results, Context ctx, String[] array, double lat, double lng, double radius) {
            /*YelpAPICLI yelpApiCli = new YelpAPICLI();
            new JCommander(yelpApiCli);

            YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
            queryAPI(yelpApi, yelpApiCli);*/

        }
    }
    /*public static void getLocationList(final ArrayList<JSONObject> results, Context ctx, String[] array, double lat, double lng, double radius){
        YelpAPICLI yelpApiCli = new YelpAPICLI();
        new JCommander(yelpApiCli, args);

        YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        queryAPI(yelpApi, yelpApiCli);
        /*
        String url = "https://api.yelp.com/v2/search";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //params.put("radius_filter", radius);
        params.put("limit",20);

        client.get(ctx, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                results.add(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                for (int i = 0; i < timeline.length(); i++) {
                    try {
                        results.add(timeline.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        */
        /*client.get(ctx, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jo = new JSONObject(new String(responseBody));
                    results.add(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("onFailure");
            }
        });
    }*/
    /*
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


    }*/

    private static List<Location> pLocationList = null;
    private static boolean changedRecord = true;

    public static List<Location> convertToLocation (List<JSONObject> rawList){
        if(pLocationList == null || changedRecord) {                                                     //need to test the case if the list in userA change if
            List<Utility.Location> locationList = new ArrayList<>();
            if(rawList==null){
                return new ArrayList<>();
            }
            for (int i = 0; i < rawList.size(); i++) {
                try {
                    JSONObject locationObject = rawList.get(i);
                    locationList.add(new Location(locationObject.getString("id"), locationObject,
                            locationObject.getString("name"), locationObject.getString("address")));
                } catch (JSONException e) {
                    e.printStackTrace();
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
