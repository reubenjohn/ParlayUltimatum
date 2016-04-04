package com.aspirephile.parlayultimatum.splash;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.aspirephile.parlayultimatum.Constants;
import com.aspirephile.parlayultimatum.R;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;


public class SplashActivity extends AppCompatActivity {

    private final Logger l = new Logger(SplashActivity.class);
    private final NullPointerAsserter asserter = new NullPointerAsserter(l);
    private SplashFragment splashF;

    @Override
    protected void onStop() {
        l.onStop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        l.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        l.onResume();
    }

    @Override
    protected void onStart() {
        l.onStart();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l.onCreate();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeSplashFragment();
    }

    private void initializeSplashFragment() {
        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        splashF = (SplashFragment) fm.findFragmentByTag(Constants.tags.splashFragment);

        if (!asserter.assertPointerQuietly(splashF)) {
            l.i("Creating new " + SplashFragment.class.getSimpleName() + " fragment");
            splashF = new SplashFragment();
            fm.beginTransaction()
                    .add(R.id.container_splash, splashF, Constants.tags.splashFragment)
                    .commit();
            // load the data from the web
            //homeF.setNotificationIntent(loadMyData());
        }
        asserter.assertPointer(splashF);
    }

    @Override
    public void onDestroy() {
        l.onDestroy();
        super.onDestroy();
    }

}
