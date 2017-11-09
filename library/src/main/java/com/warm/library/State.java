package com.warm.library;

/**
 * Created by warm on 17/6/9.
 */

public class State {

    /**
     * 往下拉，但是没有到可以加载的距离
     */
    public static final int PUSH_NO_OK=10;
    /**
     * 往下拉，可以达到可以加载的距离
     */
    public static final int PUSH_OK=11;

    /**
     * 刷新中
     */
    public static final int REFRESHING=12;

    /**
     * 刷新结束
     */
    public static final int END=13;







}
