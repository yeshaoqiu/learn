package com.org.hm.ds.sort.compare;

import com.org.hm.ds.sort.AbstractSort;

public class MergeSort extends AbstractSort {

    /**
     * 归并排序（MERGE-SORT）是建立在归并操作上的一种有效的排序算法,该算法是采用分治法（Divide and Conquer）的一个非常典型的应用。
     * 原理：将已有序的子序列合并，得到完全有序的序列；即先使每个子序列有序，再使子序列段间有序。若将两个有序表合并成一个有序表，称为二路归并
     * 思路：
     * 从上往下的归并排序：它与"从下往上"在排序上是反方向的。它基本包括3步：
     * ① 分解 -- 将当前区间一分为二，即求分裂点 mid = (low + high)/2;
     * ② 求解 -- 递归地对两个子区间a[low...mid] 和 a[mid+1...high]进行归并排序。递归的终结条件是子区间长度为1。
     * ③ 合并 -- 将已排序的两个子区间a[low...mid]和 a[mid+1...high]归并为一个有序的区间a[low...high]。
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        mergeSort(a, 0, a.length-1);

        return a;
    }

    private void mergeSort(Integer[] a, int left, int right) {
        if(a == null || a.length <= 0 || left >= right){ return; }

        int mid = left + ((right-left) >> 1);
        mergeSort(a, left, mid);//左边有序
        mergeSort(a, mid + 1, right);//右边有序

        merge(a, left, mid, right);
    }

    private void merge(Integer[] a, int left, int mid, int right) {
        Integer[] temp = new Integer[right - left + 1];
        int i = 0;
        int l = left;
        int r = mid + 1;
        while(l <= mid && r <= right){
            temp[i++] = a[l] < a[r] ? a[l++] : a[r++];
        }
        while(l <= mid){
            temp[i++] = a[l++];
        }
        while(r <= right){
            temp[i++] = a[r++];
        }

        for(i = 0; i < temp.length; i++){
            a[left+i] = temp[i];
        }
    }

    protected String getName() {
        return "[merge sort] stable sort";
    }
}
