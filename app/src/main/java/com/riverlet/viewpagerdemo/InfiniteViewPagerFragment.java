package com.riverlet.viewpagerdemo;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.riverlet.riverletviewpager.InfiniteViewPager;
import com.riverlet.riverletviewpager.LoopViewPager;

import java.util.ArrayList;
import java.util.List;


public class InfiniteViewPagerFragment extends Fragment {


    public InfiniteViewPagerFragment() {
    }


    public static InfiniteViewPagerFragment newInstance() {
        InfiniteViewPagerFragment fragment = new InfiniteViewPagerFragment();
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
        return inflater.inflate(R.layout.fragment_infiniteviewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("InfiniteViewPager", "onViewCreated");
        final InfiniteViewPager viewpager = view.findViewById(R.id.viewpager);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("InfiniteViewPager",viewpager.getDataList().toString());
                viewpager.setCurrentItemOfData(0, false);
            }
        });


        List<Integer> dataList = new ArrayList<>();
        dataList.add(0);

        viewpager.setData(dataList, new InfiniteViewPager.ViewHolderCreator() {
            @Override
            public InfiniteViewPager.ViewHolder create() {
                TextView textView = new TextView(getContext());
                textView.setBackgroundColor(0xff199dff);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(50);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return new InfiniteViewHolder(textView);
            }
        });

        viewpager.setOnNeedAddDataCallback(new InfiniteViewPager.OnNeedAddDataCallback<Integer>() {
            @Override
            public Integer addFirst(int position, Integer integer) {
                return integer - 1;
            }

            @Override
            public Integer addLast(int position, Integer integer) {
                return integer + 1;
            }
        });
    }

    class InfiniteViewHolder extends InfiniteViewPager.ViewHolder<View, Integer> {
        TextView text;

        public InfiniteViewHolder(TextView view) {
            super(view);
            text = view;
        }

        @Override
        public void update(InfiniteViewPager.ViewHolder<View, Integer> holder, Integer integer) {
            text.setText(integer + "");
        }
    }
}
