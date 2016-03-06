package com.aspirephile.parlayultimatum;

import android.support.multidex.MultiDexApplication;

import com.aspirephile.parlayultimatum.db.ParlayUltimatumDb;
import com.aspirephile.shared.debug.Logger;

public class App extends MultiDexApplication {
    Logger l = new Logger(App.class);

    @Override
    public void onCreate() {
        super.onCreate();
        l.d(App.class.getSimpleName() + " being created");
        ParlayUltimatumDb.initialize(getApplicationContext());
    }
}
