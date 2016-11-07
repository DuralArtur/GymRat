package com.example.android.gymrat;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Artur on 31-Jul-16.
 */
public class MyApp extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
