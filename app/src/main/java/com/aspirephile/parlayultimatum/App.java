package com.aspirephile.parlayultimatum;

import android.support.multidex.MultiDexApplication;

import com.aspirephile.shared.debug.Logger;

import org.kawanfw.sql.api.client.android.AceQLDBManager;
import org.kawanfw.sql.api.client.android.BackendConnection;
import org.kawanfw.sql.api.client.android.OnRemoteConnectionEstablishedListener;

import java.sql.SQLException;

public class App extends MultiDexApplication {
    Logger l = new Logger(App.class);

    @Override
    public void onCreate() {
        super.onCreate();
        l.d(App.class.getSimpleName() + " being created");
        AceQLDBManager.initialize(getString(R.string.pref_default_data_sync_backend_url),
                "reuben", "pass");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        l.w("onTerminate");
        AceQLDBManager.getDefaultRemoteConnectionIfExists(new OnRemoteConnectionEstablishedListener() {
            @Override
            public void onRemoteConnectionEstablishedListener(BackendConnection remoteConnection, SQLException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    try {
                        remoteConnection.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
}
