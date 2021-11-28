package qinfen.zheng;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/25 21:18
 * @dec
 */
public class TestArrays {
    public static void main(String[] args) {
        Method[] methods = Integer.class.getDeclaredMethods();
        for (Method method : methods) {

            System.out.println(method.getName());

        }

    }
}
