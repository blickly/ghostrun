package com.ghostrun.util;

import java.util.Random;

public class RandUtils {
    private static Random rand = new Random();

    public static int nextInt(int i) {
        return rand.nextInt(i);
    }

}
