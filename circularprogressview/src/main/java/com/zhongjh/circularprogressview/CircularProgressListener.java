package com.zhongjh.circularprogressview;

public interface CircularProgressListener {

    /**
     * 开始进行消耗性的事件
     */
    void onStart();

    /**
     * 完成动作
     */
    void onDone();

    /**
     * 停止动作
     */
    void onStop();

    /**
     * 只有普通模式下才触发
     */
    void onClick();

}
