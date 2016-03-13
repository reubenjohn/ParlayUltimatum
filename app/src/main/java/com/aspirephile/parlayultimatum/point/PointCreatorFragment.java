package com.aspirephile.parlayultimatum.point;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.aspirephile.parlayultimatum.Constants;
import com.aspirephile.parlayultimatum.R;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

import org.kawanfw.sql.api.client.android.AceQLDBManager;
import org.kawanfw.sql.api.client.android.execute.update.OnUpdateCompleteListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PointCreatorFragment extends Fragment implements View.OnClickListener {

    PointCreatorListener pointCreatorListener;
    private Logger l = new Logger(PointCreatorFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageButton done;
    private TextInputLayout titleContainer, descriptionContainer;
    private Snackbar invalidFieldsSnackbar;

    public PointCreatorFragment() {
        l.onConstructor();
    }

    @Override
    public void onAttach(Context context) {
        l.onAttach();
        super.onAttach(context);
        try {
            pointCreatorListener = (PointCreatorListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        l.onCreate();
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        l.onCreateView();
        View v = inflater.inflate(R.layout.fragment_point_creator, container, false);
        if (asserter.assertPointer(v))
            bridgeXML(v);
        initializeFields();
        return v;
    }

    @Override
    public void onStart() {
        l.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        l.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        l.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        l.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        l.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        l.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        l.onDetach();
        super.onDetach();
        pointCreatorListener = null;
    }

    private void bridgeXML(View v) {
        l.bridgeXML();
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.cl_point_creator);
        collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.ctl_point_creator);

        titleContainer = (TextInputLayout) v.findViewById(R.id.til_point_creator_title);
        descriptionContainer = (TextInputLayout) v.findViewById(R.id.til_point_creator_description);

        done = (FloatingActionButton) v.findViewById(R.id.fab_point_creator);

        l.bridgeXML(asserter.assertPointer(coordinatorLayout, collapsingToolbarLayout, descriptionContainer,
                titleContainer));
    }

    private void initializeFields() {
        l.initializeFields();

        invalidFieldsSnackbar = Snackbar.make(coordinatorLayout, R.string.point_creator_error_invalid_fields, Snackbar.LENGTH_SHORT);

        if (isContainerEditTextAvailable()) {
            //noinspection ConstantConditions
            titleContainer.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence text, int start, int before, int count) {
                    handleTitleValidity(text);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            //noinspection ConstantConditions
            descriptionContainer.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence text, int start, int before, int count) {
                    handleDescriptionValidity(text);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        done.setOnClickListener(this);
    }

    private boolean handleTitleValidity(CharSequence text) {
        if (text.length() < Constants.preferences.pointCreator.titleMin) {
            titleContainer.setError(getString(R.string.point_creator_error_title_too_short)
                    + "(" + text.length() + "/" + Constants.preferences.pointCreator.titleMin + ")");
        } else if (text.length() > Constants.preferences.pointCreator.titleMax) {
            titleContainer.setError(getString(R.string.point_creator_error_title_too_long)
                    + "(" + text.length() + "/" + Constants.preferences.pointCreator.titleMax + ")");
        } else {
            titleContainer.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    private boolean handleDescriptionValidity(CharSequence text) {
        if (text.length() < Constants.preferences.pointCreator.descriptionMin) {
            descriptionContainer.setError(getString(R.string.point_creator_error_description_too_short)
                    + "(" + text.length() + "/" + Constants.preferences.pointCreator.descriptionMin + ")");
        } else if (text.length() > Constants.preferences.pointCreator.descriptionMax) {
            descriptionContainer.setError(getString(R.string.point_creator_error_description_too_long)
                    + "(" + text.length() + "/" + Constants.preferences.pointCreator.descriptionMax + ")");
        } else {
            descriptionContainer.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fab_point_creator) {
            createPoint();
        } else {
            l.w("Unhandled view clicked with ID: " + v.getId());
        }
    }

    private void createPoint() {
        if (isFieldsValid()) {
            l.d("Creating point");
            final PointCreatorItem pointCreatorItem = new PointCreatorItem();
            if (isContainerEditTextAvailable()) {
                Snackbar.make(coordinatorLayout, R.string.point_creator_creating, Snackbar.LENGTH_INDEFINITE)
                        .show();
                //noinspection ConstantConditions
                pointCreatorItem.setTitle(titleContainer.getEditText().getText().toString());
                //noinspection ConstantConditions
                pointCreatorItem.setDescription(descriptionContainer.getEditText().getText().toString());

                List<PointCreatorItem> list = new ArrayList<>();
                list.add(pointCreatorItem);
                AceQLDBManager.insertSQLEntityList(list, new OnUpdateCompleteListener() {
                    @Override
                    public void onUpdateComplete(int result, SQLException e) {
                        if (e != null) {
                            pointCreatorListener.onCreationFailed();
                            e.printStackTrace();
                        } else {
                            l.i("No of rows inserted: " + result);
                            if (result == 0) {
                                pointCreatorListener.onCreationFailed();
                            } else {
                                pointCreatorListener.onCreationSuccess();
                            }
                        }
                    }
                });
            } else {
                Snackbar.make(coordinatorLayout, R.string.point_error_ui, Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            l.e("Cannot create point. Invalid fields detected!");
            invalidFieldsSnackbar.show();
        }
    }

    private boolean isContainerEditTextAvailable() {
        return asserter.assertPointer(titleContainer.getEditText(), descriptionContainer.getEditText());
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isFieldsValid() {
        return isContainerEditTextAvailable()
                && handleTitleValidity(titleContainer.getEditText().getText().toString())
                && handleDescriptionValidity(descriptionContainer.getEditText().getText().toString());
    }

    private void cancelCreation() {
        pointCreatorListener.onCreationFailed();
    }

    public interface PointCreatorListener {
        void onCreationSuccess();

        void onCreationFailed();
    }
}
