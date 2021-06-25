package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
       /* int a = 0x00000000;
        a = a >> 2;
        fff(a);
        System.out.print("\n");
        a = a >> 1;
        fff(a);
        System.out.print("\n");
        a = a << 2;
        fff(a);
        System.out.print("\n");
        System.out.print(a);*/
        int num = 5;
        String a = "0";
        if (a.length() > 0) {
            a = a.substring(0, a.length() - 1);
        }
        a = a + "2";
        System.out.print(a);
        StringBuilder sb;
        sb.replace()

    }

    public void fff(int aa) {
        for (int i = 0; i < 8; i++) {
            int c = aa >> i & 1;
            System.out.print(c);
        }
    }
}