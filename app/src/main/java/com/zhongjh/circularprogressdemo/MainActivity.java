package com.zhongjh.circularprogressdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zhongjh.circularprogressview.CircularProgress;
import com.zhongjh.circularprogressview.CircularProgressListener;

public class MainActivity extends AppCompatActivity {

    CircularProgress mCircularProgress;
    DownLoadSigTask downLoadSigTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(v -> {
            downLoadSigTask.cancel(true);
            mCircularProgress.reset();
        });

        mCircularProgress = (CircularProgress) findViewById(R.id.circularProgress);
        mCircularProgress.setCircularProgressListener(new CircularProgressListener() {
            @Override
            public void onStart() {
                Log.d(CircularProgress.TAG, "onStart");
                downLoadSigTask = new DownLoadSigTask();
                downLoadSigTask.execute();
            }

            @Override
            public void onDone() {

            }

            @Override
            public void onStop() {
                // 这里写停止当前线程的操作
                downLoadSigTask.cancel(true);
                // 要自己告诉mCircularProgress重置，因为不知道自己停止当前线程要消耗多少时间
                mCircularProgress.reset();
            }

        });
    }


    /**
     * 模拟的一个消耗时间任务
     */
    class DownLoadSigTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected String doInBackground(final String... args) {

            //Creating dummy task and updating progress

            for (int i = 0; i <= 100; i++) {
                if (isCancelled()) {
                    break;
                }
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
            Log.d(CircularProgress.TAG, "progress" + progress[0]);
        }


    }
}