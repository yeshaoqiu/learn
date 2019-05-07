package com.org.hm.ds.sort.compare;

import com.org.hm.ds.sort.AbstractSort;

public class InsertSort extends AbstractSort {

    /**
     * 原理：插入排序的基本操作就是将一个数据插入到已经排好序的有序数据中，从而得到一个新的、个数加一的有序数据，算法适用于少量数据的排序，时间复杂度为O(n^2)。是稳定的排序方法。
     *
     * 思路：将第一待排序序列第一个元素看做一个有序序列，把第二个元素到最后一个元素当成是未排序序列。
     * 从头到尾依次扫描未排序序列，将扫描到的每个元素插入有序序列的适当位置。（如果待插入的元素与有序序列中的某个元素相等，则将待插入元素插入到相等元素的后面。）
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        int i,j,temp;
        int swap = 0;
        //<i为有序数组，从i开始往后遍历
        for(i = 1; i < a.length; i++){
            //遍历有序数组，并与当前元素进行比较
//            for(j = 0; j < i; j++){
//                if(a[j] > a[i]){
//                    swap(a, i, j);
//                    swap++;
//                }
//            }

            //折半插入
            temp = a[i];
            int low = 0;
            int high = i - 1;
            while(low <= high){
                int mid = (high + low)/2;
                if(a[mid] > temp){
                    high = mid - 1;
                }else {
                    low = mid + 1;
                }
            }

            for(j = i-1; j >= low; j--){
                a[j+1] = a[j];
                swap++;
            }
            a[low] = temp;
        }
        System.out.println("swap " + swap + " times");
        return a;
    }

    protected String getName() {
        return "[insert sort] stable sort";
    }
}
