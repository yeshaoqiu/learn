package com.org.hm.ds.sort.compare;

import com.org.hm.ds.sort.AbstractSort;

public class QuickSort extends AbstractSort {
    private int swap = 0;

    /**
     * 基本思想是：通过一趟排序将要排序的数据分割成独立的两部分，其中一部分的所有数据都比另外一部分的所有数据都要小，然后再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列
     *
     * ①分解：
     *     在R[low..high]中任选一个记录作为基准(Pivot)，以此基准将当前无序区划分为左、右两个较小的子区间R[low..pivotpos-1)和R[pivotpos+1..high]，并使左边子区间中所有记录的关键字均小于等于基准记录(不妨记为pivot)的关键字pivot.key，右边的子区间中所有记录的关键字均大于等于pivot.key，而基准记录pivot则位于正确的位置(pivotpos)上，它无须参加后续的排序。
     *   注意：
     *     划分的关键是要求出基准记录所在的位置pivotpos。划分的结果可以简单地表示为(注意pivot=R[pivotpos])：
     *     R[low..pivotpos-1].keys≤R[pivotpos].key≤R[pivotpos+1..high].keys
     *                   其中low≤pivotpos≤high。
     * ②求解：
     *     通过递归调用快速排序对左、右子区间R[low..pivotpos-1]和R[pivotpos+1..high]快速排序。
     *
     * ③组合：
     *     因为当"求解"步骤中的两个递归调用结束时，其左、右两个子区间已有序。对快速排序而言，"组合"步骤无须做什么，可看作是空操作。
     */
    @Override
    public Integer[] sortArray(Integer[] a) {
        if(a == null || a.length <= 0){ return a; }

        quickSort(a, 0, a.length - 1);
        System.out.println("swap " + swap + " times");

        return a;
    }

    private void quickSort(Integer[] a, int left, int right) {
        if(a == null || a.length <= 0 || left >= right){
            return;
        }

        int base = a[left];
        int i = left;
        int j = right;
        while(i < j){
            while(i < j && a[j] >= base){
                j--;
            }
            if(i < j){
//                a[i] = a[j];
                a[i++] = a[j];
            }
            while(i < j && a[i] <= base){
                i++;
            }
            if(i < j){
//                a[j] = a[i];
                a[j--] = a[i];
            }
        }
        a[i] = base;
        swap++;

        quickSort(a, left, i-1);
        quickSort(a, i+1, right);
    }

//    private void quickSort(Integer[] a, int left, int right) {
//        if(a == null || a.length <= 0 || left > right){
//            return;
//        }
//
//        int base = a[left];
//        int i = left;
//        int j = right;
//        int temp;
//        while(i < j){
//            while(a[j] >= base && i < j){
//                j--;
//            }
//            while(a[i] <= base && i < j){
//                i++;
//            }
//            if(i < j){
//                swap(a, i, j);
//                swap++;
//            }
//        }
//        a[left] = a[i];
//        a[i] = base;
//        swap++;
//        quickSort(a, left, i-1);//递归处理左边
//        quickSort(a, i+1, right);//递归处理右边
//    }

    protected String getName() {
        return "[quick sort] nonstable sort";
    }
}
