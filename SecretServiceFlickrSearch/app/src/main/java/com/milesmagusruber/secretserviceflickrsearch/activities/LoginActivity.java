package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.SearchRequestsAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.db.model.User;

public class LoginActivity extends AppCompatActivity {
    public final static String EXTRA_CURRENT_USER= BuildConfig.APPLICATION_ID + ".extra.currentuser";

    //current user
    private int currentUser=1;

    private Button buttonEnter;
    private EditText editTextlogin;
    private String login;
    DatabaseHelper db;

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
                //adding login to database
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: wtf
                    }
                }).start();
                login=editTextlogin.getText().toString();

                // TODO: нужно хранить ссылку на этот AsyncTask и не запускать его если уже работает
                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... data) {
                        db = DatabaseHelper.getInstance(LoginActivity.this);
                        User user = db.getUser(login);
                        if (user == null) {
                            db.addUser(new User(login));
                            user = db.getUser(login);
                            currentUser=user.getId();
                        }else{
                            currentUser=user.getId();
                        }
                        db.close();
                        return 0;
                    }

                    @Override
                    protected void onPostExecute(Integer a) {
                        Intent intent=new Intent(LoginActivity.this,FlickrSearchActivity.class);
                        intent.putExtra(EXTRA_CURRENT_USER,currentUser);
                        startActivity(intent);
                    }
                }.execute();
            }
        });
    }

}
