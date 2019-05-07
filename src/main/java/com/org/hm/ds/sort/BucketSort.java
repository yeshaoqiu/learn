package com.org.hm.ds.sort;

import com.org.hm.ds.sort.compare.InsertSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BucketSort extends AbstractSort {

    /**
     * 基本思想
     * 桶排序的基本思想是将一个数据表分割成许多buckets，然后每个bucket各自排序，或用不同的排序算法，或者递归的使用bucket sort算法。也是典型的divide-and-conquer分而治之的策略。它是一个分布式的排序，介于MSD基数排序和LSD基数排序之间。
     *
     * 基本流程
     * 建立一堆buckets；
     * 遍历原始数组，并将数据放入到各自的buckets当中；
     * 对非空的buckets进行排序；
     * 按照顺序遍历这些buckets并放回到原始数组中即可构成排序后的数组。
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        int bucketSize = 5;//默认5个桶
        return bucketSort(a, bucketSize);
    }

    private Integer[] bucketSort(Integer[] a, int bucketSize) {
        int max = a[0];
        int min = a[0];
        for(int i = 1; i < a.length; i++){
            max = max > a[i] ? max : a[i];
            min = min < a[i] ? min : a[i];
        }

        int bucketNum = (int)Math.floor((max - min)/bucketSize) + 1;
//        Integer[][] buckets = new Integer[bucketNum][0];//二维数组实现

        ArrayList<LinkedList<Integer>> bucketList = new ArrayList<LinkedList<Integer>>(bucketNum);
        for(int i = 0; i < bucketNum; i++){
            bucketList.add(new LinkedList<Integer>());
        }

        for(int i = 0; i < a.length; i++){
            int index = (int)Math.floor((a[i] - min)/bucketSize);
//            buckets[index] = arrAppend(buckets[index], a[i]);//二维数组实现

            bucketList.get(index).add(a[i]);
        }

        int arrIndex = 0;
        for(int i = 0; i < bucketNum; i++){
//            if(buckets[i].length <= 0){ continue; }//二维数组实现

            if(bucketList.get(i).size() <= 0){ continue; }

            InsertSort insertSort = new InsertSort();
//            List<Integer> sortList = insertSort.sortList(Arrays.asList(buckets[i]));//二维数组实现

            List<Integer> sortList = insertSort.sortList(bucketList.get(i));
            if(sortList == null || sortList.isEmpty()){ continue;}

            for(Integer num : sortList){
                a[arrIndex++] = num;
            }
        }
        return a;
    }

    /**
     * 自动扩容，并保存数据
     *
     * @param arr
     * @param value
     */
    private Integer[] arrAppend(Integer[] arr, int value) {
        arr = Arrays.copyOf(arr, arr.length + 1);
        arr[arr.length - 1] = value;
        return arr;
    }

    protected String getName() {
        return "[bucket sort] stable sort";
    }
}
