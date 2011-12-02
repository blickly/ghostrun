package com.ghostrun.util;

//import java.util.Date;
import java.util.Random;

public class RandUtils {
//    private static Random rand = new Random(new Date().getTime());
    private static Random rand = new Random(27);

    public static int nextInt(int i) {
        return rand.nextInt(i);
    }

    public static double nextDouble() {
        return rand.nextDouble();
    }

}
