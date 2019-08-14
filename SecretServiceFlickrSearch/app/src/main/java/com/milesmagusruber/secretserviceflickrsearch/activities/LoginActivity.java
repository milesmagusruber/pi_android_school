package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.db.model.User;

public class LoginActivity extends AppCompatActivity {


    private Button buttonEnter;
    private EditText editTextlogin;
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
                        db = new DatabaseHelper(LoginActivity.this);
                        db.addUser(new User(editTextlogin.getText().toString()));
                        db.close();
                    }
                }).start();


                Intent intent=new Intent(LoginActivity.this,FlickrSearchActivity.class);
                startActivity(intent);
            }
        });
    }

}
