package com.zhongjh.circularprogressview;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zhongjh.circularprogressview.CircularProgressState.DONE;
import static com.zhongjh.circularprogressview.CircularProgressState.PLAY;
import static com.zhongjh.circularprogressview.CircularProgressState.PREPARE;
import static com.zhongjh.circularprogressview.CircularProgressState.STOP;
import static com.zhongjh.circularprogressview.CircularProgressState.STOPIN;

/**
 * @author zhongjh
 */
@IntDef({STOP, PLAY, DONE, STOPIN, PREPARE})
@Retention(RetentionPolicy.SOURCE)
public @interface CircularProgressState {

    /**
     * 默认状态，停止中
     */
    int STOP = 0;
    /**
     * 播放中
     */
    int PLAY = 1;
    /**
     * 完成
     */
    int DONE = 2;
    /**
     * 正在停止中
     */
    int STOPIN = 3;
    /**
     * 准备中
     */
    int PREPARE = 4;

}