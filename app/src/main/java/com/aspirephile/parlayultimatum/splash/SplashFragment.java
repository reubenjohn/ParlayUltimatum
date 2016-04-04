package com.aspirephile.parlayultimatum.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.aspirephile.parlayultimatum.Constants;
import com.aspirephile.parlayultimatum.HomeActivity;
import com.aspirephile.parlayultimatum.LoginActivity;
import com.aspirephile.parlayultimatum.R;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

public class SplashFragment extends Fragment {
    private final Logger l = new Logger(SplashFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        l.onCreateView();
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_splash, container, false);
        bridgeXML(v);
        initializeFields();
        return v;
    }

    private void bridgeXML(View v) {
        l.bridgeXML();
        textView = (TextView) v.findViewById(R.id.tv_splash_image);
        l.bridgeXML(asserter.assertPointer(textView));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        l.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getActivity(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
        }
        switch (requestCode) {
            case Constants.codes.request.authentication:
                launchHomeActivity();
                break;
            default:
                l.w("Unhandled request code!");
        }
    }

    private void launchHomeActivity() {
        Intent i = new Intent(getActivity(), HomeActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getActivity().finish();
    }

    private void initializeFields() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sp = getActivity().getSharedPreferences(Constants.files.authentication, Context.MODE_PRIVATE);
                        if (sp.contains(Constants.preferences.username)) {
                            launchHomeActivity();
                        } else {
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            startActivityForResult(i, Constants.codes.request.authentication);
                            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            getActivity().finish();
                        }
                    }
                });
            }
        }, Constants.properties.splashScreenDuration);

        ViewTreeObserver vto = textView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (asserter.assertPointer(textView)) {
                    AnimationSet animationSet = new AnimationSet(true);
                    Animation translateAnimation = new TranslateAnimation(-textView.getWidth() / 2, 0, 0, 0);
                    Animation alphaAnimation = new AlphaAnimation(0, 1);
                    animationSet.addAnimation(translateAnimation);
                    animationSet.addAnimation(alphaAnimation);
                    DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(3);
                    animationSet.setInterpolator(decelerateInterpolator);
                    animationSet.setDuration(Constants.properties.splashScreenDuration);
                    textView.setAnimation(animationSet);
                }
            }
        });
    }

    @Override
    public void onResume() {
        l.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        l.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        l.onDestroyView();
        super.onDestroyView();
    }

}
