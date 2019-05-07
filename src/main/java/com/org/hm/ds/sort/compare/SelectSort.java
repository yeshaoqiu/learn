package com.org.hm.ds.sort.compare;

import com.org.hm.ds.sort.AbstractSort;

public class SelectSort extends AbstractSort {

    /**
     * 工作原理：是每一次从待排序的数据元素中选出最小（或最大）的一个元素，存放在序列的起始位置，然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。
     * 以此类推，直到全部待排序的数据元素排完。选择排序是不稳定的排序方法。
     *
     * 基于此思想的算法主要有简单选择排序、树型选择排序和堆排序。
     *
     * 思路：给定数组：int[] arr={里面n个数据}；
     * 第1趟排序，在待排序数据arr[1]~arr[n]中选出最小的数据，将它与arrr[1]交换；
     * 第2趟，在待排序数据arr[2]~arr[n]中选出最小的数据，将它与r[2]交换；
     * 以此类推，第i趟在待排序数据arr[i]~arr[n]中选出最小的数据，将它与r[i]交换，直到全部排序完成
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        int minIndex;
        int swap = 0;
        //从开始遍历
        for(int i = 0; i < a.length-1; i++){
            minIndex = i;//当前值设置为最小值
            //前面为有序数组，从当前值往后遍历，逐个与当前值进行比较，标记最小值序号
            for(int j = i+1; j < a.length; j++){
                if(a[j] < a[minIndex]){
                    minIndex = j;
                }
            }
            //最小值未变
            if(minIndex == i){ continue; }

            swap(a, i, minIndex);
            swap++;
        }
        System.out.println("swap " + swap + " times");
        return a;
    }

    protected String getName() {
        return "[select sort] nonstable sort";
    }
}
