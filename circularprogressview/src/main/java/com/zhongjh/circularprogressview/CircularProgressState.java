package com.zhongjh.circularprogressview;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zhongjh.circularprogressview.CircularProgressState.PLAY;
import static com.zhongjh.circularprogressview.CircularProgressState.STOP;

@IntDef({STOP, PLAY})
@Retention(RetentionPolicy.SOURCE)
public @interface CircularProgressState {

    int STOP = 0;
    int PLAY = 1;

}