package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.User;

public class LoginActivity extends AppCompatActivity {

    private Button buttonEnter;
    private EditText editTextlogin;
    private String login;
    DatabaseHelper db;

    //Controlling AsyncTask
    LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonEnter = (Button) findViewById(R.id.button_enter);
        editTextlogin = (EditText) findViewById(R.id.edittext_login);

        //Going to main flickr search functionality
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking login in database
                login = editTextlogin.getText().toString();

                //using Asynctask to work with database
                loginTask = new LoginTask();
                if (loginTask.getStatus() != AsyncTask.Status.RUNNING) {
                    loginTask.execute();
                }
            }
        });
    }

    private class LoginTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            buttonEnter.setClickable(false);
        }

        @Override
        protected Integer doInBackground(Void... data) {
            db = DatabaseHelper.getInstance(LoginActivity.this);
            User user = db.getUser(login);
            if (user == null) {
                db.addUser(new User(login));
                user = db.getUser(login);
            }
            CurrentUser currentUser = CurrentUser.getInstance();
            currentUser.setUser(user);
            db.close();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer a) {
            buttonEnter.setClickable(true);
            Intent intent = new Intent(LoginActivity.this, FlickrSearchActivity.class);
            startActivity(intent);
        }
    }

}
