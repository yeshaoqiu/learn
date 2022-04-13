package com.org.hm.funny;

import java.util.Random;

/**
 * @author hanming
 * @date 2022/4/13 20:45
 */
public class RandomWord {
    public static void main(String[] args) {
//        System.out.println(randomString(-229985452) + " " + randomString(-147909649));

        int love = generateSeed("love", Integer.MIN_VALUE, Integer.MAX_VALUE);
        int you = generateSeed("you", Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println(randomString(love) + " " + randomString(you));

    }

    private static String randomString(int i) {
        Random ran = new Random(i);
        StringBuilder sb = new StringBuilder();
        while (true) {
            int k = ran.nextInt(27);
            if (k == 0) {
                break;
            }

            sb.append((char) ('`' + k));
        }
        return sb.toString();
    }

    public static int generateSeed(String goal, int start, int finish) {
        char[] input = goal.toCharArray();
        char[] pool = new char[input.length];
        label:
        for (int seed = start; seed < finish; seed++) {
            Random random = new Random(seed);

            for (int i = 0; i < input.length; i++) {
                pool[i] = (char) (random.nextInt(27) + '`');
            }

            if (random.nextInt(27) == 0) {
                for (int i = 0; i < input.length; i++) {
                    if (input[i] != pool[i]) {
                        continue label;
                    }
                }
                return seed;
            }
        }
        return 0;
    }
}
