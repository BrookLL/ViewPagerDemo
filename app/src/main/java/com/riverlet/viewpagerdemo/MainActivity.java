package com.riverlet.viewpagerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.riverlet.riverletviewpager.InfiniteViewPager;

public class MainActivity extends AppCompatActivity {
    private InfiniteViewPagerFragment infiniteViewPagerFragment;
    private LoopViewPagerFragment loopViewPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infiniteViewPagerFragment = InfiniteViewPagerFragment.newInstance();
        loopViewPagerFragment = LoopViewPagerFragment.newInstance();

    }

    public void showInfiniteViewPager(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, infiniteViewPagerFragment).commit();
    }

    public void showLoopViewPager(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, loopViewPagerFragment).commit();
    }
}
