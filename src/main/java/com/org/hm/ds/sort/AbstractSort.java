package com.org.hm.ds.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class AbstractSort {
    private int MAX = 100;
    private Random random = new Random();

    protected List<Integer> randomList(int num, int max){
        List<Integer> result = new ArrayList<Integer>();
        if(num <= 0){ return result; }
        if(max <= 0){ max = MAX; }

        for(int i=0; i < num; i++){
            result.add(random.nextInt(max));
        }
        return result;
    }

    protected void printList(String prefixMessage, List<Integer> list){
        if(list == null || list.isEmpty()){
            return;
        }
        System.out.print(prefixMessage);
        for(Integer num : list){
            System.out.print(num);
            System.out.print(" ");
        }
        System.out.println();
    }

    protected Integer[] toArray(List<Integer> list){
        Integer[] a = new Integer[list.size()];
        return list.toArray(a);
    }

    public List<Integer> sortList(List<Integer> list){
        if(list == null || list.isEmpty()){ return list; }

        Integer[] a = toArray(list);
        return Arrays.asList(sortArray(a));
    }

    /**
     * 交换数据
     * @param a 数组
     * @param i
     * @param j
     */
    protected void swap(Integer[] a, int i, int j) {
        a[i] = a[i] ^ a[j];
        a[j] = a[i] ^ a[j];
        a[i] = a[i] ^ a[j];
    }

    public void sortTest(){
        int num = Integer.valueOf(System.getProperty("array_size", "20"));
        int max = Integer.valueOf(System.getProperty("array_max_value", "100"));
        List<Integer> randomList = randomList(num, max);
        sortTest(randomList);
    }

    public void sortTest(List<Integer> randomList){
        System.out.println(getName());
        printList("[origin list]<== ", randomList);

        long start = System.currentTimeMillis();
        List<Integer> sortedList = sortList(randomList);
        System.out.println("[used time]: " + (System.currentTimeMillis() - start) + " ms");

        printList("[sorted list]==> ", sortedList);
        System.out.println();
    }

    public abstract Integer[] sortArray(Integer a[]);
    protected abstract String getName();
}
