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
        mCircularProgress = findViewById(R.id.circularProgress);
        findViewById(R.id.button).setOnClickListener(v -> {
            if (downLoadSigTask != null) {
                downLoadSigTask.cancel(true);
            }
            mCircularProgress.reset();
        });


        // 修改主色调
        findViewById(R.id.button2).setOnClickListener(v -> mCircularProgress.setPrimaryColor(R.color.purple_200));

        // 修改副色调
        findViewById(R.id.button3).setOnClickListener(v -> mCircularProgress.setPrimaryVariantColor(R.color.purple_700));

        // 修改成铺满样式
        findViewById(R.id.button4).setOnClickListener(v -> mCircularProgress.setFullStyle(true));

        // 修改图标
        findViewById(R.id.button5).setOnClickListener(v -> mCircularProgress.setFunctionImage(R.drawable.ic_baseline_done, R.drawable.avd_done_to_stop, R.drawable.avd_stop_to_done));

        // 修改铺满模式下的进度颜色
        findViewById(R.id.button6).setOnClickListener(v -> mCircularProgress.setFullProgressColor(R.color.red));

        // 修改成普通的按钮
        findViewById(R.id.button7).setOnClickListener(v -> mCircularProgress.setProgressMode(false));

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
                stop();
            }

            @Override
            public void onClickByGeneralMode() {
                Toast.makeText(MainActivity.this, "普通模式下触发", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickByProgressMode() {

            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    private void stop() {
        // 这里写停止当前线程的操作
        if (downLoadSigTask != null) {
            downLoadSigTask.cancel(true);
        }
        // 要自己告诉mCircularProgress重置，因为不知道自己停止当前线程要消耗多少时间
        mCircularProgress.reset();
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