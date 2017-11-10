package com.warm.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warm.library.OnPullStateChange;
import com.warm.library.State;


/**
 * Created by warm on 17/6/9.
 */

public class Header extends RelativeLayout implements OnPullStateChange {
    private static final String TAG = "Header";
    private TextView tv_state;
    private ProgressBar pb;

    public Header(Context context) {
        this(context, null);
    }

    public Header(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Header(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.header, this);
        tv_state = findViewById(R.id.tv_state);
        pb = findViewById(R.id.pb);
    }

    @Override
    public void pullState(int state) {
        switch (state) {

            case State.PUSH_NO_OK:
                pb.setVisibility(GONE);
                tv_state.setText("下拉刷新");

                break;
            case State.PUSH_OK:
                tv_state.setText("松开刷新");
                break;
            case State.REFRESHING:
                pb.setVisibility(VISIBLE);
                tv_state.setText("正在加载...");

                break;
            case State.END:
                pb.setVisibility(GONE);
                tv_state.setText("下拉刷新");
                break;
        }
    }

    @Override
    public void pulling(float y) {


    }




}
