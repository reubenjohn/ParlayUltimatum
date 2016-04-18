package com.aspirephile.parlayultimatum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspirephile.parlayultimatum.db.tables.ParlayUser;

import org.kawanfw.sql.api.client.android.AceQLDBManager;
import org.kawanfw.sql.api.client.android.BackendConnection;
import org.kawanfw.sql.api.client.android.execute.OnGetPrepareStatement;
import org.kawanfw.sql.api.client.android.execute.query.OnGetResultSetListener;
import org.kawanfw.sql.api.client.android.execute.update.OnUpdateCompleteListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView contactImgImgView;
    private CoordinatorLayout coordinatorLayout;
    private EditText editTextFirst;
    private EditText editTextLast;
    private String username;
    private TextView tUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getSharedPreferences(Constants.files.authentication, Context.MODE_PRIVATE)
                .getString(Constants.preferences.username, null);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_user_info);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);

        Button saveChangesButton = (Button) findViewById(R.id.buttonSaveChanges);
        saveChangesButton.setOnClickListener(this);

        //Intent for choosing image and bringing it to this activity
        contactImgImgView = (ImageView) findViewById(R.id.imageViewUserImage);
        contactImgImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Set Profile Picture"), 1);
            }
        });

        editTextFirst = (EditText) findViewById(R.id.editTextFname);
        editTextLast = (EditText) findViewById(R.id.editTextLname);
        tUsername = (TextView) findViewById(R.id.textViewUsername);

        AceQLDBManager.executeQuery(new OnGetPrepareStatement() {
            @Override
            public PreparedStatement onGetPreparedStatement(BackendConnection remoteConnection) {
                String sql = "select * from ParlayUser where username=?";
                try {
                    PreparedStatement preparedStatement = remoteConnection.prepareStatement(sql);

                    if (username != null) {
                        preparedStatement.setString(1, username);
                    } else {
                        Snackbar.make(coordinatorLayout, R.string.error_incorrect_username, Snackbar.LENGTH_LONG)
                                .show();
                        return null;
                    }
                    return preparedStatement;
                } catch (SQLException e) {
                    e.printStackTrace();
                    Snackbar.make(coordinatorLayout, e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                            .show();
                    return null;
                }
            }
        }, new OnGetResultSetListener() {
            @Override
            public void onGetResultSet(ResultSet rs, SQLException e) {
                if (e != null) {
                    e.printStackTrace();
                    Snackbar.make(coordinatorLayout, e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    try {
                        rs.next();
                        ParlayUser user = new ParlayUser(rs);
                        updateProfile(user);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        Snackbar.make(coordinatorLayout, e1.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
    }

    private void updateProfile(ParlayUser user) {
        Snackbar.make(coordinatorLayout, user.toString(), Snackbar.LENGTH_LONG)
                .show();
        tUsername.setText(user.username);
        editTextFirst.setText(user.fName);
        editTextLast.setText(user.lName);

    }

    public void onActivityResult(int resCode, int reqCode, Intent data) {
        if (resCode == RESULT_OK)        //If user presses back button
            if (reqCode == 1)
                contactImgImgView.setImageURI(data.getData());
    }

    @Override
    public void onClick(View v) {
        final String fname = editTextFirst.getText().toString();
        final String lname = editTextLast.getText().toString();
        OnGetPrepareStatement preparedStatementListener = new OnGetPrepareStatement() {
            @Override
            public PreparedStatement onGetPreparedStatement(BackendConnection remoteConnection) {
                String sql = "update ParlayUser set fname=?,lname=? where username=?";
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = remoteConnection.prepareStatement(sql);
                    preparedStatement.setString(1, fname);
                    preparedStatement.setString(2, lname);
                    preparedStatement.setString(3, username);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return preparedStatement;
            }
        };
        OnUpdateCompleteListener onUpdateCompleteListener = new OnUpdateCompleteListener() {
            @Override
            public void onUpdateComplete(int result, SQLException e) {
                if (e != null) {
                    e.printStackTrace();
                    Snackbar.make(coordinatorLayout, e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    Snackbar.make(coordinatorLayout, R.string.user_info_update_success, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        };
        AceQLDBManager.executeUpdate(preparedStatementListener, onUpdateCompleteListener);
    }
}
