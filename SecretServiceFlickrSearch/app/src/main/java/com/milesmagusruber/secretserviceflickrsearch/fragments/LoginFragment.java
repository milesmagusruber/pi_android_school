package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.milesmagusruber.secretserviceflickrsearch.activities.MainActivity;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.User;

public class LoginFragment extends Fragment{

    private MaterialButton buttonEnter;
    private TextInputEditText editTextlogin;
    private String login;
    DatabaseHelper db;

    //Controlling AsyncTask
    LoginTask loginTask;




    //empty LoginFragment constructor
    public LoginFragment(){

    }

    //getting instance of LoginFragment
    public static LoginFragment newInstance(){
        LoginFragment fragment=new LoginFragment();
        return fragment;
    }


    private LoginFragmentListener listener;

    public interface LoginFragmentListener{
        void onLoginButtonEnter();
        void getRidOfNavigationDrawer();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            listener = (LoginFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement methods of LoginFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        listener=null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listener.getRidOfNavigationDrawer();
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        buttonEnter = view.findViewById(R.id.button_enter);
        editTextlogin = view.findViewById(R.id.edittext_login);

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
        return view;
    }

    //AsyncTask for logging process
    private class LoginTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            buttonEnter.setClickable(false);
        }

        @Override
        protected Integer doInBackground(Void... data) {
            db = DatabaseHelper.getInstance(getActivity());
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
            listener.onLoginButtonEnter();
            //Intent intent = new Intent(LoginFragment.this, FlickrSearchActivity.class);
            //startActivity(intent);
        }
    }

}
