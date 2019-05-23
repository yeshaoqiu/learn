package com.org.hm.magic;

public class MagicNum {
    public static void main(String[] args) {
        Integer a = 50;
        Integer b = 50;

        Integer c = 150;
        Integer d = 150;
        System.out.println("[a == b]:" + (a == b));//true
        System.out.println("[c != d]:" + (c != d));//true

        System.out.println("[Math.abs(Integer.MIN_VALUE) < 0]:" + (Math.abs(Integer.MIN_VALUE) < 0));//true
        System.out.println("[Integer.MIN_VALUE == Math.abs(Integer.MIN_VALUE)]:" + (Integer.MIN_VALUE == Math.abs(Integer.MIN_VALUE)));//true

        int i = Integer.MAX_VALUE;
        System.out.println("[i+1<i]:"+(i+1<i));//true

        int j = Integer.MIN_VALUE;
        System.out.println("[j != 0 && j == -j]:"+ (j != 0 && j == -j));//true

        double k = Double.POSITIVE_INFINITY;
        System.out.println("[k == k + 1]:" + (k == k + 1));//true
        System.out.println("[k == k - 1]:" + (k == k - 1));//true

        double l = Double.NaN;
        System.out.println("[l != l]:" + (l != l));//true

    }
}
