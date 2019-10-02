package com.org.hm.Generic;

import java.util.ArrayList;
import java.util.List;

public class GenericTest {
    public static void main(String[] args) {

        /**
         * 界限通配符在应用于集合的时候会影响集合的读写行为：
         *
         * 上界<? extends T> 限制了类型上限，只能向上转型，可以读，但是没法写，因为子类型不确定，没法向下转型；
         *
         * 下界<? super T>限制类型的下限，只能向下转型，可以写，但是没法读，因为父类型不确定，没法向上转型。
         *
         */

        //<? extends B> 范围: A类或者A的子类
        //由于下限不确定，所以无法向下转型至具体类型
        List<? extends B> list1 = new ArrayList<B>(){{add(new B());}};
//        list1.add(new B()); //无法添加该类型, 向下转型无法确定目标类型
//        list1.add(new C());
        A a = list1.get(0); //正常向上转型

        //<? super B> 范围: B类或者B的父类
        //由于上限不确定，所以B类和B类的子类均可以加入，但是B类的父类不行
        List<? super B> list2 = new ArrayList<>();
//        list2.add(new A()); //无法向下转型
        list2.add(new B()); //正常向上转型
        list2.add(new C());
//        C c = list2.get(0);//无法向下转型，不加强制转换会报错
        C c = (C)list2.get(0);
    }
    //   A -> B -> C
    static class A {};
    static class B extends A {};
    static class C extends B {};
}
