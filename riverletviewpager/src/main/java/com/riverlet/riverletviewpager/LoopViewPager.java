package com.riverlet.riverletviewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LoopViewPager extends RelativeLayout {
    private static final String TAG = "LoopViewPager";
    private static final int MAX_VIEW_LIMIT = 2;
    private static final int MID_PAGES_INDEX = Integer.MAX_VALUE / 2;
    private OnCurrentPageChangeListener onCurrentPageChangeListener;
    private ViewHolderCreator viewHolderCreator;
    private List dataList = new ArrayList();
    private ViewPager viewPager;

    public LoopViewPager(@NonNull Context context) {
        this(context, null);
    }

    public LoopViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if (onCurrentPageChangeListener != null) {
                Object currentData = getCurrentItemViewHolder().data;
                onCurrentPageChangeListener.onCurrentPageChange(dataList.indexOf(currentData), currentData);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private class LoopPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewHolder holder = viewHolderCreator.getFreeViewHolder();
            Object object = getDataOfItem(position);
            holder.setData(object);
            container.addView(holder.view);
            viewHolderCreator.usedViewHolders.put(position, holder);
            return holder.view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ViewHolder holder = viewHolderCreator.usedViewHolders.remove(position);
            if (holder != null) {
                container.removeView(holder.view);
                holder.setData(null);
                viewHolderCreator.freeViewHolders.addLast(holder);
            }
        }

        /**
         * 获取当前postion对用的数据
         *
         * @param position
         * @return
         */
        private Object getDataOfItem(int position) {
            int size = dataList.size();
            int i = ((position - MID_PAGES_INDEX) % size + size) % size;
            return dataList.get(i);
        }
    }


    /**
     * ViewHolder
     *
     * @param <V> View类型
     * @param <T> 数据类型
     */
    public static abstract class ViewHolder<V extends View, T> {
        V view;
        T data;

        public ViewHolder(V view) {
            this.view = view;
        }

        public T getData() {
            return data;
        }

        public V getView() {
            return view;
        }

        private void setData(T data) {
            this.data = data;
            update(this, data);
        }

        public abstract void update(ViewHolder<V, T> holder, T t);
    }

    /**
     * 创建ViewHolder的工具子类
     */
    public static abstract class ViewHolderCreator {
        LinkedList<ViewHolder> freeViewHolders = new LinkedList<ViewHolder>();
        Map<Integer, ViewHolder> usedViewHolders = new HashMap<>();

        private ViewHolder getFreeViewHolder() {
            ViewHolder viewHolder = null;
            if (!freeViewHolders.isEmpty()) {
                viewHolder = freeViewHolders.removeFirst();
            }
            if (viewHolder == null) {
                viewHolder = create();
            }
            return viewHolder;
        }

        public abstract ViewHolder create();
    }

    /**
     * 翻页后当前数据在列表中的位置回调
     */
    public interface OnCurrentPageChangeListener {
        void onCurrentPageChange(int dataIndexOfList, Object onject);
    }

    /**
     * 设置基础数据和ViewHolderCreator
     *
     * @param dataList          数据列表
     * @param viewHolderCreator
     */
    public void setData(List dataList, @NonNull ViewHolderCreator viewHolderCreator) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        this.viewHolderCreator = viewHolderCreator;
        createViewPager();
    }

    private void createViewPager() {
        viewPager = new ViewPager(getContext());
        viewPager.setAdapter(new LoopPagerAdapter());
        viewPager.setCurrentItem(MID_PAGES_INDEX,false);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        removeAllViews();
        addView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 获取当前显示Item的ViewHolder，即可获取当前显示的数据和View
     *
     * @return
     */
    public ViewHolder getCurrentItemViewHolder() {
        if (viewPager != null) {
            return viewHolderCreator.usedViewHolders.get(viewPager.getCurrentItem());
        }
        return null;
    }

    /**
     * 根据Item位置获取它的ViewHolder
     *
     * @param position
     * @return
     */
    public ViewHolder getViewHolderOfPosition(int position) {
        return viewHolderCreator.usedViewHolders.get(position);
    }

    /**
     * 跳转到指定Data的位置
     *
     * @param data
     */
    public void setCurrentItemOfData(Object data) {
        setCurrentItemOfData(data, true);
    }

    /**
     * 跳转到指定Data的位置
     *
     * @param data
     * @param smoothScroll 是否带滚动动画
     */
    public void setCurrentItemOfData(Object data, boolean smoothScroll) {
        if (viewPager == null) {
            return;
        }
        for (Integer key : viewHolderCreator.usedViewHolders.keySet()) {
            if (data.equals(viewHolderCreator.usedViewHolders.get(key).data)) {
                viewPager.setCurrentItem(key, smoothScroll);
                break;
            }
        }
    }

    /**
     * 翻到下一页
     */
    public void nextPage() {
        if (viewPager == null) {
            return;
        }
        if (viewPager.getCurrentItem() + 1 > Integer.MAX_VALUE) {
            Log.e(TAG, "没有下一页了");
            return;
        }
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    /**
     * 翻到上一页
     */
    public void lastPage() {
        if (viewPager == null) {
            return;
        }
        if (viewPager.getCurrentItem() <= 0) {
            Log.e(TAG, "没有上一页了");
            return;
        }
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    /**
     * 设置当前页变化的监听
     *
     * @param onCurrentPageChangeListener
     */
    public void setOnCurrentPageChangeListener(OnCurrentPageChangeListener onCurrentPageChangeListener) {
        this.onCurrentPageChangeListener = onCurrentPageChangeListener;
    }
}
