package com.milesmagusruber.simpleasynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class SimpleAsyncTask extends AsyncTask<Void,Integer,String> {
    private WeakReference<TextView> mTextView;
    private WeakReference<ProgressBar> progressBar;

    SimpleAsyncTask(TextView tv,ProgressBar pb)
    {
        mTextView = new WeakReference<>(tv);
        progressBar = new WeakReference<>(pb);
    }

    @Override
    protected void onPreExecute() {
        progressBar.get().setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected String doInBackground(Void... voids) {
        // Generate a random number between 0 and 10
        Random r = new Random();
        int n = r.nextInt(11);

        // Make the task take long enough that we have
        // time to rotate the phone while it is running
        int s = n * 200;
        int count = 0;
        // Sleep for the random amount of time
        try {
            while(count < 10) {
                Thread.sleep(s / 10);
                count++;
                publishProgress(count*10);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return a String result
        return "Awake at last after sleeping for " + s + " milliseconds!";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.get().setProgress(values[0]);

    }

    protected void onPostExecute(String result)
    {
        mTextView.get().setText(result);
        progressBar.get().setVisibility(ProgressBar.INVISIBLE);
    }
}
