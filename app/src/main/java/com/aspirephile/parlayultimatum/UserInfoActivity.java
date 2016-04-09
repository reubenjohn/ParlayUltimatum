package com.aspirephile.parlayultimatum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.kawanfw.sql.api.client.android.AceQLDBManager;
import org.kawanfw.sql.api.client.android.BackendConnection;
import org.kawanfw.sql.api.client.android.execute.OnGetPrepareStatement;
import org.kawanfw.sql.api.client.android.execute.update.OnUpdateCompleteListener;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView contactImgImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }
//Get username from backend and put it on the screen

    public void onActivityResult(int resCode, int reqCode, Intent data) {
        if (resCode == RESULT_OK)        //If user presses back button
            if (reqCode == 1)
                contactImgImgView.setImageURI(data.getData());
    }

    @Override
    public void onClick(View v) {

        EditText editTextFirst = (EditText) findViewById(R.id.editTextFname);
        EditText editTextLast = (EditText) findViewById(R.id.editTextLname);
        final String fname = editTextFirst.getText().toString();
        final String lname = editTextLast.getText().toString();
        OnGetPrepareStatement preparedStatementListener = new OnGetPrepareStatement() {
            @Override
            public PreparedStatement onGetPreparedStatement(BackendConnection remoteConnection) {
                String sql = "update table_name set fname=?,lname=?";
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = remoteConnection.prepareStatement(sql);
                    preparedStatement.setString(0, fname);
                    preparedStatement.setString(1, lname);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return preparedStatement;
            }
        };
        OnUpdateCompleteListener onUpdateCompleteListener = new OnUpdateCompleteListener() {
            @Override
            public void onUpdateComplete(int result, SQLException e) {

            }
        };
        AceQLDBManager.executeUpdate(preparedStatementListener, onUpdateCompleteListener);
    }
}
