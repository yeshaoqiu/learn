package com.org.hm.ds.sort.compare;

import com.org.hm.ds.sort.AbstractSort;

public class ShellSort extends AbstractSort {

    /**
     * 希尔排序是插入排序的一种又称“缩小增量排序”，是直接插入排序算法的一种更高效的改进版本。希尔排序是非稳定排序算法
     *
     * 思路：记录按下标的一定增量分组，对每组使用直接插入排序算法排序；随着增量逐渐减少，每组包含的关键词越来越多，当增量减至1时，整个文件恰被分成一组，算法便终止。
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        int swap = 0;
        for(int gap = a.length/2; gap > 0; gap/=2){
            for(int i = gap; i < a.length; i++){
                //交换法
//                int j = i;
//                while (j-gap >= 0 && a[j] < a[j-gap]){
//                    swap(a, j, j-gap);
//                    j-=gap;
//                    swap++;
//                }

                //移动法
                int j = i;
                int temp = a[j];
                // j - step 就是代表与它同组隔壁的元素
                while (j - gap >= 0 && a[j - gap] > temp) {
                    a[j] = a[j - gap];
                    j-=gap;
                }
                a[j] = temp;
            }
        }
        System.out.println("swap " + swap + " times");
        return a;
    }

    protected String getName() {
        return "[shell sort] nonstable sort";
    }
}
