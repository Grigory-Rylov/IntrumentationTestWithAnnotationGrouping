package com.github.grishberg.instrumentaltestwithtestgroupsordering;

import android.app.Application;
import android.util.Log;

/**
 * Created by grishberg on 07.03.18.
 */

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: cold start");
    }
}
