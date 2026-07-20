package com.ruoyi.asset.designPatterns.test;

public class demo1 {
    public static void main(String[] args) {
        Integer a = 245;
        Integer b = 245;
        int c = 245;
        System.out.println(a == b);
        System.out.println(a == c);
        System.out.println(a.equals(c));
    }
}
