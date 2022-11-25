package com.zhongjh.circularprogressview;

/**
 * @author zhongjh
 */
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
     * 普通按钮模式下的按钮点击事件，非进度模式才会触发
     */
    void onClickByGeneralMode();

    /**
     * 进度模式下的按钮点击事件，比onStart事件还要早
     */
    void onClickByProgressMode();

}
