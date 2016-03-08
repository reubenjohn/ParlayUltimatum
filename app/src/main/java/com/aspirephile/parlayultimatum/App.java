package com.aspirephile.parlayultimatum;

import android.support.multidex.MultiDexApplication;

import org.kawanfw.sql.api.client.android.AceQLDBManager;
import com.aspirephile.shared.debug.Logger;

public class App extends MultiDexApplication {
    Logger l = new Logger(App.class);

    @Override
    public void onCreate() {
        super.onCreate();
        l.d(App.class.getSimpleName() + " being created");
        AceQLDBManager.initialize(getString(R.string.pref_default_data_sync_backend_url),
                "reuben", "pass");
    }
}
