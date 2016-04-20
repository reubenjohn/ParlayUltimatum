
package com.aspirephile.parlayultimatum.point;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.aspirephile.parlayultimatum.Constants;
import com.aspirephile.parlayultimatum.R;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

import java.sql.SQLException;

public class CommentListActivity extends AppCompatActivity implements CommentListFragment.OnListFragmentInteractionListener {
    private Logger l = new Logger(CommentListActivity.class);

    private String PID;
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l.onCreate();
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i == null) {
            Intent data = new Intent();
            data.putExtra(Constants.extras.errorResult, Constants.errorResults.badIntent);
            setResult(RESULT_CANCELED, data);
            return;
        } else if ((PID = i.getStringExtra(Constants.extras.PID)) == null) {
            Intent data = new Intent();
            data.putExtra(Constants.extras.errorResult, Constants.errorResults.badPID);
            data.putExtra(Constants.extras.PID, PID);
            setResult(RESULT_CANCELED, data);
            return;
        }

        setContentView(R.layout.activity_comment_list);

        openCommentListFragment();
    }


    private void openCommentListFragment() {
        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        CommentListFragment commentListF = (CommentListFragment) fm.findFragmentByTag(Constants.tags.commentListFragment);

        if (!asserter.assertPointerQuietly(commentListF)) {
            l.i("Creating new " + CommentListFragment.class.getSimpleName() + " fragment");
            commentListF = CommentListFragment.newInstance(1);//TODO Set PID from constructor
            fm.beginTransaction()
                    .replace(R.id.container_comment_list, commentListF, Constants.tags.commentListFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCommentListItemSelected(CommentListItem.CommentItem item) {

    }

    @Override
    public void onCommentListLoadFailed(SQLException e) {

    }
}
