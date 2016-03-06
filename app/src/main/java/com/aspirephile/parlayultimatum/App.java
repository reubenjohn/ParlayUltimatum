package com.aspirephile.parlayultimatum;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

import com.aspirephile.parlayultimatum.db.ParlayUltimatumConnection;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

import java.sql.Connection;
import java.sql.SQLException;

public class App extends MultiDexApplication {
    private static final String username = "reuben";
    private static final String password = "pass";
    private static ConnectivityManager connectivityManager;
    public ParlayUltimatumConnection remoteConnection;
    Logger l = new Logger(App.class);
    Context context;
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    public static boolean isOnline(Logger l) {
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isOnline = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isOnline)
                l.d("Network connection found");
            else
                l.e("Network connection not detected");
            return isOnline;
        } else {
            l.e("Connectivity manager is null!");
            return true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        l.d(App.class.getSimpleName() + " being created");
        context = getApplicationContext();
    }

    public ParlayUltimatumConnection getDefaultRemoteConnectionIfExists(String url) throws SQLException, ClassNotFoundException {
        if (asserter.assertPointer(remoteConnection))
            return remoteConnection;
        else {
            Connection connection = ParlayUltimatumConnection
                    .remoteConnectionBuilder(url, username, password);
            remoteConnection = new ParlayUltimatumConnection(
                    connection);
            return remoteConnection;
        }
    }
}
