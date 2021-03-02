package com.zhongjh.circularprogressview;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zhongjh.circularprogressview.CircularProgressState.PLAY;
import static com.zhongjh.circularprogressview.CircularProgressState.STOP;

@IntDef({STOP, PLAY})
@Retention(RetentionPolicy.SOURCE)
public @interface CircularProgressState {

    int STOP = 0; // 默认状态，停止中
    int PLAY = 1; // 播放中
    int DONE = 2; // 完成
    int STOPIN = 3; // 正在停止中
    int PREPARE = 4; // 准备中

}