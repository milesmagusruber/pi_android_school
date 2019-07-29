package com.milesmagusruber.stateinthesmartphone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // Unique tag for the intent reply
    public static final int TEXT_REQUEST = 1;

    //Private TextView vars
    private TextView mChosenActionTextView;
    private TextView mIncomeMessageTextView;
    private TextView mAppUsedCounter;

    //Static values to make an app count number of times it is used
    private static boolean valueOfLaunchCountModified = false;
    private static int appUsedCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Using static values and SharedPreferences to count number of times we've used our app
        // Из-за этой конструкции не всегда при выходе-входе в приложение увеличивается счётчик
        // Флаг valueOfLaunchCountModified тут лишний. Метод onCreate() твоей основной активити
        // и так служит точкой входа в приложение
        if(!valueOfLaunchCountModified){
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            appUsedCount= preferences.getInt("appUsedCount", 0);
            if(preferences.edit().putInt("appUsedCount", ++appUsedCount).commit()){
                valueOfLaunchCountModified = true;
            }
        }

        setContentView(R.layout.activity_main);
        mChosenActionTextView = findViewById(R.id.chosen_action);
        mIncomeMessageTextView = findViewById(R.id.income_message);
        mAppUsedCounter = findViewById(R.id.app_used_counter);
        mAppUsedCounter.setText(Integer.toString(appUsedCount));

        // Restore the saved state.
        // See onSaveInstanceState() for what gets saved.
        if (savedInstanceState != null) {
            //chosen_action лучше вынести в константу - с ней будет проще работать и нет шанса на опечатку
            mChosenActionTextView.setText(savedInstanceState.getString("chosen_action"));
        }

        //Receiving an implicit Intent
        Intent intent = getIntent();
        String income_message=intent.getStringExtra(Intent.EXTRA_TEXT);
        if (income_message != null) {
            mIncomeMessageTextView.setVisibility(View.VISIBLE);
            mIncomeMessageTextView.setText(income_message);
        }
    }


    /**
     * @param view
     * This method is used to start ChooseActivity which
     * returns us a text value for TextView with id=chosen_action
     */
    public void chooseAction(View view){
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * This method helps us to process result that we got from ChooseActivity
     * If reply result is good we get a text value which we place in the
     * third TextView (id=chosen_action) in our layout
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Test for the right intent reply.
        if (requestCode == TEXT_REQUEST) {
            // Test to make sure the intent reply result was good.
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(ChooseActivity.EXTRA_REPLY);
                // Set the reply
                mChosenActionTextView.setText(reply);
            }
        }
    }

    /**
     * @param view
     * This function implements "Share with people" button functionality
     * It sends text from TextView with id="chosen_action" to another applications
     */
    public void shareWithPeople(View view) {
        String txt = mChosenActionTextView.getText().toString();
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle(R.string.button_share_with_people)
                .setText(txt)
                .startChooser();
    }

    //Saving state of our activity
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("chosen_action", mChosenActionTextView.getText().toString());
    }


}
