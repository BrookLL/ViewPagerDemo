package com.riverlet.viewpagerdemo;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.riverlet.riverletviewpager.InfiniteViewPager;
import com.riverlet.riverletviewpager.LoopViewPager;

import java.util.ArrayList;
import java.util.List;


public class LoopViewPagerFragment extends Fragment {


    public LoopViewPagerFragment() {
    }


    public static LoopViewPagerFragment newInstance() {
        LoopViewPagerFragment fragment = new LoopViewPagerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loopviewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final LoopViewPager viewpager = view.findViewById(R.id.viewpager);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItemOfData("A", false);
            }
        });
        List<String> dataList = new ArrayList<>();
        dataList.add("A");
        dataList.add("B");
        dataList.add("C");
        viewpager.setData(dataList, new LoopViewPager.ViewHolderCreator() {
            @Override
            public LoopViewPager.ViewHolder create() {
                TextView textView = new TextView(getContext());
                textView.setBackgroundColor(0xff43CD80);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(50);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return new LoopViewHolder(textView);
            }
        });
    }

    class LoopViewHolder extends LoopViewPager.ViewHolder<View, String> {
        TextView text;

        public LoopViewHolder(TextView view) {
            super(view);
            text = view;
        }

        @Override
        public void update(LoopViewPager.ViewHolder<View, String> holder, String str) {
            text.setText(str);
        }
    }
}
