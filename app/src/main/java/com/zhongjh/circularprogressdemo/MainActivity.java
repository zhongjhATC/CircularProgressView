package com.zhongjh.circularprogressdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhongjh.circularprogressview.CircularProgress;
import com.zhongjh.circularprogressview.CircularProgressListener;

public class MainActivity extends AppCompatActivity {

    static CircularProgress mCircularProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mCircularProgress = (CircularProgress) findViewById(R.id.circularProgress);
        mCircularProgress.setCircularProgressListener(() -> {
            runOnUiThread(() -> {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this,
                        "Starting download", Toast.LENGTH_SHORT)
                        .show();
            });
            new DownLoadSigTask().execute();
        });
    }


    static class DownLoadSigTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected String doInBackground(final String... args) {

            //Creating dummy task and updating progress

            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(50);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                publishProgress(i);

            }


            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {

            // publishing progress to progress arc

            mCircularProgress.setProgress(progress[0]);
        }



    }
}