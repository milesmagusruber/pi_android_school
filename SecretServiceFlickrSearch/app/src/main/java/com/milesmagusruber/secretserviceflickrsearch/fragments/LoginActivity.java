package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.User;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton buttonEnter;
    private TextInputEditText editTextlogin;
    private String login;
    DatabaseHelper db;

    //Controlling AsyncTask
    LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_SSFS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        buttonEnter = findViewById(R.id.button_enter);
        editTextlogin = findViewById(R.id.edittext_login);

        //Going to main flickr search functionality
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking login in database
                login = editTextlogin.getText().toString();

                //checking login
                if(login.matches("[a-z0-9_]{3,30}")){
                //using Asynctask to work with database
                loginTask = new LoginTask();
                if (loginTask.getStatus() != AsyncTask.Status.RUNNING) {
                    loginTask.execute();
                }
                }else{
                    editTextlogin.setText("");
                    editTextlogin.setHint(R.string.incorrect_login);
                }
            }
        });
    }

    //AsyncTask for logging process
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
