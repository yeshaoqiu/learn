package com.org.hm.ds.sort;

public class RadixSort extends AbstractSort {

    /**
     * 基数排序(Radix Sort)是桶排序的扩展，
     * 基本思想是：将整数按位数切割成不同的数字，然后按每个位数分别比较
     *
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        Integer max = a[0];
        for(int i = 1; i < a.length; i++){
            if(a[i] > max){
                max = a[i];
            }
        }

        int digit = 1;
        int[] bucket = new int[a.length];
        while (max/digit > 0){
            /* reset counter */
            int[] digitCount= new int[10];

            /* count pos-th digits (keys) */
            for(int i = 0; i < a.length; i++){
                digitCount[a[i]/digit%10]++;
            }

            /* accumulated count */
            for(int i = 1; i < digitCount.length; i++){
                digitCount[i]+=digitCount[i-1];
            }

            /* To keep the order, start from back side */
            for(int i = a.length - 1; i >= 0; i--){
                bucket[--digitCount[a[i]/digit%10]] = a[i];
            }

            /* rearrange the original array using elements in the bucket */
            for(int i = 0; i < a.length; i++){
                a[i] = bucket[i];
            }

            /* move up the digit position */
            digit *= 10;
        }
        return a;
    }

    protected String getName() {
        return "[radix sort] stable sort";
    }
}
