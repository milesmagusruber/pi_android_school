package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.User;


//importing rx libraries
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;


public class LoginFragment extends Fragment{

    private MaterialButton buttonEnter;
    private TextInputEditText editTextLogin;
    private MaterialTextView authInfoTextView;
    private String login;
    private SSFSDatabase db;

    //Controlling AsyncTask
    private AsyncTask<String,Void,Boolean> loginTask;

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
        void onLogOut();
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
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.title_authorization);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listener.onLogOut();
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        buttonEnter = view.findViewById(R.id.button_enter);
        editTextLogin = view.findViewById(R.id.edittext_login);
        authInfoTextView = view.findViewById(R.id.auth_info);

        //Our Observable EditText field for editTextLogin
        Observable<String> rxEditTextLoginObservable = RxTextView.textChanges(editTextLogin)
                .debounce(250, TimeUnit.MILLISECONDS).skip(1)
                .observeOn(AndroidSchedulers.mainThread()).map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
                });

        //Subscribing an Observer that will process input of characters
        rxEditTextLoginObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String loginInput)
            {
                //do some action
                checkLogin(loginInput);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        //Going to main flickr search functionality
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking login in database
                login = editTextLogin.getText().toString();

                //checking login
                if(login.matches("[a-z0-9_]{3,30}")){
                //using Asynctask to work with database

                if (loginTask==null || loginTask.getStatus() != AsyncTask.Status.RUNNING) {
                    loginTask = new LoginTask();
                    loginTask.execute(login);
                }
                }else{
                    editTextLogin.setText("");
                    editTextLogin.setHint(R.string.incorrect_login);
                }
            }
        });
        return view;
    }

    private void checkLogin(String loginInput) {

        if(loginInput.equals("")){
            authInfoTextView.setText("");
        }else if(!loginInput.matches("[a-z0-9_]{3,30}")){
            authInfoTextView.setText(R.string.incorrect_login);
        }else{
            if(loginTask==null || loginTask.getStatus()!= AsyncTask.Status.RUNNING){
            loginTask = new CheckLoginTask();
            loginTask.execute(loginInput);
            }
        }
    }

    //AsyncTask for logging process
    private class LoginTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            buttonEnter.setClickable(false);
        }

        @Override
        protected Boolean doInBackground(String... data) {
            String login=data[0];
            db = db.getInstance(getActivity());
            User user = db.userDao().getUser(login);
            if (user == null) {
                db.userDao().insert(new User(login));
                user = db.userDao().getUser(login);

            }
            CurrentUser currentUser = CurrentUser.getInstance();
            currentUser.setUser(user);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            buttonEnter.setClickable(true);
            listener.onLoginButtonEnter();
        }
    }

    //AsyncTask to check is user's login exists in database
    private class CheckLoginTask extends AsyncTask<String,Void,Boolean>{

        String loginInput="";

        @Override
        protected Boolean doInBackground(String... data){
            loginInput=data[0];
            db = db.getInstance(getActivity());
            User user = db.userDao().getUser(loginInput);
            if (user == null) {
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean loginExists){
            if(loginExists){
                authInfoTextView.setText(getActivity().getString(R.string.user_exists,loginInput));
            }else{
                authInfoTextView.setText(getActivity().getString(R.string.user_doesnt_exist,loginInput));
            }
        }
    }

}
