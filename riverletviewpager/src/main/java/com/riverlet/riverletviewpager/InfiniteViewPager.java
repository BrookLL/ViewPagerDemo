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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InfiniteViewPager extends ViewPager {
    private static final String TAG = "InfiniteViewPager";
    private static final int MAX_VIEW_LIMIT = 2;
    private static final int MID_PAGES_INDEX = Integer.MAX_VALUE / 2;
    private ViewHolderCreator viewHolderCreator;
    private Map<Integer, Object> dataMap = new HashMap<>();
    private OnNeedAddDataCallback onNeedAddDataCallback;
    private OnCurrentPageChangeListener onCurrentPageChangeListener;

    public InfiniteViewPager(@NonNull Context context) {
        this(context, null);
    }

    public InfiniteViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOffscreenPageLimit(MAX_VIEW_LIMIT);
        addOnPageChangeListener(onPageChangeListener);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            if (onCurrentPageChangeListener != null) {
                Object currentData = getCurrentItemViewHolder().data;
                onCurrentPageChangeListener.onCurrentPageChange(currentData);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 设置新增数据的回调
     * @param onNeedAddDataCallback
     */
    public void setOnNeedAddDataCallback(OnNeedAddDataCallback onNeedAddDataCallback) {
        this.onNeedAddDataCallback = onNeedAddDataCallback;
    }

    private class LoopPagerAdapter extends PagerAdapter {

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
        }

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
            Log.d(TAG, "instantiateItem.position:" + position);
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
            container.removeView(holder.view);
            viewHolderCreator.freeViewHolders.addLast(holder);
            Log.d(TAG, "destroyItem.position:" + position);
        }

        private Object getDataOfItem(int position) {
            Log.d(TAG, "position:" + position);
            if (dataMap.containsKey(position)) {
                return dataMap.get(position);
            } else {
                if (onNeedAddDataCallback != null) {
                    Log.d(TAG, ".getDataOfItem.dataMap:" + dataMap.toString());
                    if (position > MID_PAGES_INDEX) {
                        Object currentLastData = dataMap.get(position - 1);
                        if (currentLastData != null) {
                            Object object = onNeedAddDataCallback.addLast(position, currentLastData);
                            dataMap.put(position, object);
                            return object;
                        }
                    }
                    if (position < MID_PAGES_INDEX) {
                        Object currentFirstData = dataMap.get(position + 1);
                        if (currentFirstData != null) {
                            Object object = onNeedAddDataCallback.addFirst(position, currentFirstData);
                            dataMap.put(position, object);
                            return object;
                        }
                    }
                    if (position == MID_PAGES_INDEX) {
                        throw new NullPointerException("没有设置第一个数据的值");
                    }
                }

            }
            return null;
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

        /**
         * 获取自由ViewHolder
         *
         * @return
         */
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
     * 需要新增数据的回调
     *
     * @param <T>
     */
    public interface OnNeedAddDataCallback<T> {
        /**
         * 新增数据在前（左）
         *
         * @param position 触发回调的位置
         * @param t        当前最靠前（左）的数据
         * @return 新增数据
         */
        T addFirst(int position, T t);

        /**
         * 新增数据在后（右）
         *
         * @param position 触发回调的位置
         * @param t        当前最靠前后（右）的数据
         * @return 新增数据
         */
        T addLast(int position, T t);
    }

    /**
     * 翻页后当前数据在列表中的位置回调
     */
    public interface OnCurrentPageChangeListener {
        void onCurrentPageChange(Object onject);
    }


    /**
     * 设置基础数据和ViewHolderCreator
     *
     * @param dataList
     * @param viewHolderCreator
     */
    public void setData(List dataList, @NonNull ViewHolderCreator viewHolderCreator) {
        dataMap.clear();
        this.viewHolderCreator = viewHolderCreator;
        for (int i = 0; i < dataList.size(); i++) {
            dataMap.put(MID_PAGES_INDEX + (i - dataList.size() / 2), dataList.get(i));
        }
        setAdapter(new LoopPagerAdapter());
        super.setCurrentItem(MID_PAGES_INDEX, false);
    }

    /**
     * 获取当前显示Item的ViewHolder
     *
     * @return
     */
    public ViewHolder getCurrentItemViewHolder() {
        return viewHolderCreator.usedViewHolders.get(getCurrentItem());
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

    //原来跳转界面要阻断
    @Override
    public void setCurrentItem(int item) {
//        super.setCurrentItem(item);
    }

    //原来跳转界面要阻断
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
//        super.setCurrentItem(item, smoothScroll);
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
     */
    public void setCurrentItemOfData(Object data, boolean smoothScroll) {
        for (Integer key : dataMap.keySet()) {
            if (data.equals(dataMap.get(key))) {
                scrollToItem(key, smoothScroll);
                break;
            }
        }
    }

    private void scrollToItem(final Integer position, final boolean smoothScroll) {
        int currentPosition = getCurrentItem();
        if (position > currentPosition) {
            while (++currentPosition <= position) {
                super.setCurrentItem(currentPosition, smoothScroll);
            }

        } else {
            while (--currentPosition >= position) {
                super.setCurrentItem(currentPosition, smoothScroll);
            }
        }
    }

    /**
     * 翻到下一页
     */
    public void nextPage() {
        if (getCurrentItem() + 1 > Integer.MAX_VALUE) {
            Log.e(TAG, "没有下一页了");
            return;
        }
        super.setCurrentItem(getCurrentItem() + 1);
    }

    /**
     * 翻到上一页
     */
    public void lastPage() {
        if (getCurrentItem() <= 0) {
            Log.e(TAG, "没有上一页了");
            return;
        }
        super.setCurrentItem(getCurrentItem() - 1);
    }
    /**
     * 设置当前页变化的监听
     * @param onCurrentPageChangeListener
     */
    public void setOnCurrentPageChangeListener(OnCurrentPageChangeListener onCurrentPageChangeListener) {
        this.onCurrentPageChangeListener = onCurrentPageChangeListener;
    }

    /**
     * 获取数据Map
     * @return
     */
    public Map<Integer, Object> getDataMap() {
        return dataMap;
    }
}
