package com.org.hm.ds.sort.compare;

import com.org.hm.ds.sort.AbstractSort;

public class BubbleSort extends AbstractSort {

    /**
     * 原理：比较两个相邻的元素，将值大的元素交换至右端。
     *
     * 思路：依次比较相邻的两个数，将小数放在前面，大数放在后面。
     * 即在第一趟：首先比较第1个和第2个数，将小数放前，大数放后。
     * 然后比较第2个数和第3个数，将小数放前，大数放后，
     * 如此继续，直至比较最后两个数，将小数放前，大数放后。
     * 重复第一趟步骤，直至全部排序完成。
     *
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        int lastSwap;
        int swap = 0, loop = 0;
        for(int i = a.length-1; i > 0; i = lastSwap){
            lastSwap = 0;
            for(int j = 0; j < i; j++){
                if(a[j] > a[j+1]){
                    swap(a, j, j+1);
                    swap++;
                    lastSwap = j;
                }
                loop++;
            }
        }

//        for(int i = a.length-1; i > 0; i--){
//            for(int j = 0; j < i; j++){
//                if(a[j] > a[j+1]){
//                    swap(a, j, j+1);
//                    swap++;
//                }
//                loop++;
//            }
//        }

        System.out.println("swap " + swap + " times, loop " + loop + " times");
        return a;
    }

    protected String getName() {
        return "[bubble sort] stable sort";
    }
}
