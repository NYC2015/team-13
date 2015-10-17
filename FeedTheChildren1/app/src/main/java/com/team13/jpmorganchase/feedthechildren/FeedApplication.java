package com.team13.jpmorganchase.feedthechildren;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseCrashReporting;


public class FeedApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_id));


    }
}
