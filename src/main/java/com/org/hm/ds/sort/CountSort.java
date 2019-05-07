package com.org.hm.ds.sort;

public class CountSort extends AbstractSort {

    /**
     * 基本思想
     * 当数据表长度为n
     *
     * 基本操作
     * 根据获得的数据表的范围，分割成不同的buckets，然后直接统计数据在buckets上的频次，然后顺序遍历buckets就可以得到已经排好序的数据表
     *
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        Integer b[] = new Integer[a.length];

        int max = a[0];
        int min = a[0];
        for(int i = 1; i < a.length; i++){
            if(a[i] > max){
                max = a[i];
            }
            if(a[i] < min){
                min = a[i];
            }
        }

        //数值区间大小
        int d = max - min + 1;
        int[] c = new int[d];
        //计数
        for(int i = 0; i < a.length; i++){
            c[a[i] - min]++;
        }
        //累加次数，后面的元素等于前面的元素之和
        for(int i = 1; i < c.length; i++){
            c[i] = c[i] + c[i-1];
        }
        //取值，统计次数减1
        for(int i = a.length -1; i >= 0; i--){
            b[--c[a[i] - min]] = a[i];
        }
        return b;
    }

    protected String getName() {
        return "[count sort] stable sort";
    }
}
