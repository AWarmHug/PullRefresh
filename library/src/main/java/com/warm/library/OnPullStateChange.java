package com.warm.library;

/**
 * 作者：warm
 * 时间：2017-11-10 13:22
 * 描述：下拉状态改变，{@link State}
 */

public interface OnPullStateChange {
    void pullState(int state);

    void pulling(float y);
}
