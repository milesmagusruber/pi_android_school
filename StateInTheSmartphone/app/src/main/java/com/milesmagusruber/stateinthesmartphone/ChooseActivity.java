package com.milesmagusruber.stateinthesmartphone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChooseActivity extends AppCompatActivity {

    // Unique tag for the intent reply.
    public static final String EXTRA_REPLY =
            "com.milesmagusruber.stateinthesmartphone.extra.REPLY";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
    }

    /**
     * Handles the onClick for different TextView objects. Gets the text value (action1-action7)
     * from the TextView, creates an intent, and returns the action back to
     * the main activity.
     *
     * @param view The view (TextView) that was clicked.
     */
    public void returnAction(View view) {
        TextView mReply = (TextView) view;
        // Get the action from the edit text.
        String reply = mReply.getText().toString();
        // Create a new intent for the reply, add the action message to it
        // as an extra, set the intent result, and close the activity.
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, reply);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
