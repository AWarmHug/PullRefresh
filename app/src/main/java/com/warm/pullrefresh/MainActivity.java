package com.warm.pullrefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.warm.library.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements PullToRefreshLayout.OnRefreshListener{

    private PullToRefreshLayout pull;
    private RecyclerView rv;
    private List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pull= this.findViewById(R.id.pull);
        pull.setOnRefreshListener(this);
        rv= this.findViewById(R.id.rv);
        list=new ArrayList<>();
        for (int i=0;i<20;i++) {
            list.add("我是"+i);
        }
        Adapter adapter = new Adapter(list);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pull.refreshEnd();
            }
        },2000);

    }
}