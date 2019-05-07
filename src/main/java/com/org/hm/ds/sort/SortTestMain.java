package com.org.hm.ds.sort;

import com.org.hm.ds.sort.compare.*;

import java.util.List;

public class SortTestMain {
    public static void main(String[] args){
        InsertSort insertSort = new InsertSort();
        List<Integer> list = insertSort.randomList(20, 100);
//        insertSort.sortTest();
        insertSort.sortTest(list);

        SelectSort selectSort = new SelectSort();
//        selectSort.sortTest();
        selectSort.sortTest(list);

        QuickSort quickSort = new QuickSort();
//        quickSort.sortTest();
        quickSort.sortTest(list);

        BubbleSort bubbleSort = new BubbleSort();
//        bubbleSort.sortTest();
        bubbleSort.sortTest(list);

        MergeSort mergeSort = new MergeSort();
//        mergeSort.sortTest();
        mergeSort.sortTest(list);

        ShellSort shellSort = new ShellSort();
//        shellSort.sortTest();
        shellSort.sortTest(list);

        BinaryTreeSort bts = new BinaryTreeSort();
//        bts.sortTest();
        bts.sortTest(list);

        CountSort countSort = new CountSort();
//        countSort.sortTest();
        countSort.sortTest(list);

        RadixSort radixSort = new RadixSort();
//        radixSort.sortTest();
        radixSort.sortTest(list);

        BucketSort bucketSort = new BucketSort();
//        bucketSort.sortTest();
        bucketSort.sortTest(list);

        HeapSort heapSort = new HeapSort();
//        heapSort.sortTest();
        heapSort.sortTest(list);

    }
}
