package com.org.hm.ds.sort;


public class HeapSort extends AbstractSort {
    /**
     * 堆是具有以下性质的完全二叉树：
     * 每个结点的值都大于或等于其左右孩子结点的值，称为大顶堆；
     * 或者每个结点的值都小于或等于其左右孩子结点的值，称为小顶堆
     *
     * 查找数组中某个数的父结点和左右孩子结点，比如已知索引为i的数，那么
     * 1.父结点索引：(i-1)/2（这里计算机中的除以2，省略掉小数）
     * 2.左孩子索引：2*i+1
     * 3.右孩子索引：2*i+2
     *
     * 大顶堆：arr[i] >= arr[2i+1] && arr[i] >= arr[2i+2]
     * 小顶堆：arr[i] <= arr[2i+1] && arr[i] <= arr[2i+2]
     *
     *
     * 堆排序（升序）基本思想：
     *
     * 1.首先将待排序的数组构造成一个大根堆，此时，整个数组的最大值就是堆结构的顶端
     *
     * 2.将顶端的数与末尾的数交换，此时，末尾的数为最大值，剩余待排序数组个数为n-1
     *
     * 3.将剩余的n-1个数再构造成大根堆，再将顶端数与n-1位置的数交换，如此反复执行，便能得到有序数组
     */

    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        //1.构建大顶堆
        for(int i = a.length/2-1; i>= 0; i--){
            //循环版本
            adjustBigHeapNode(a, i, a.length);
            //递归版本
//            adjustBigHeapNodeRecursion(a, i, a.length);
        }

        //2.调整堆结构+交换堆顶元素与末尾元素
        for(int i = a.length-1; i > 0; i--){
            swap(a, 0, i);
            //循环版本
            adjustBigHeapNode(a, 0, i);
            //递归版本
//            adjustBigHeapNodeRecursion(a, 0, i);
        }
        return a;
    }

    private void adjustBigHeapNode(Integer[] a, int i, int length) {
        int child;
        int curNode;
        for (curNode = a[i]; 2*i+1 < length; i = child) {
            // 子结点的位置=2*（父结点位置）+ 1，左子节点
            child = 2 * i + 1;
            // 得到子结点中较大的结点，右子节点比较大
            if (child < length-1 && a[child + 1] > a[child]) {
                ++child;
            }
            // 如果较大的子结点大于父结点那么把较大的子结点往上移动，替换它的父结点
            if (curNode < a[child]) {
                a[i] = a[child];
                a[child]= curNode;
            } else {
                // 否则退出循环
                break;
            }
        }
    }

    private void adjustBigHeapNodeRecursion(Integer[] a, int i, int heapSize) {
        int left = 2*i+1;
        int right = left+1;

        //优化
        if(i > heapSize/2){ return; }

        //找出当前节点和左右子节点最大值，当前节点小，则交换并继续调整子树

        //方式1
        if(left >= heapSize){ return; }
        int k = left;
        if(right < heapSize){
            if(a[left] < a[right]){
                k = right;
            }
        }

        if(a[k] > a[i]){
            swap(a, k, i);
            adjustBigHeapNodeRecursion(a, k, heapSize);
        }

        //方式2，更于理解
//        int largest = i;
//        if(left < heapSize && a[left] > a[largest]){
//            largest = left;
//        }
//        if(right < heapSize && a[right] > a[largest]){
//            largest = right;
//        }
//
//        if(i != largest){
//            swap(a, largest, i);
//            adjustBigHeapNodeRecursion(a, largest, heapSize);
//        }
    }

    @Override
    protected String getName() {
        return "[heap sort] nonstable sort";
    }
}
