package me.runelocus.utils;

import java.security.SecureRandom;

public class Random {
    private static final double[] pd;

    private static final ThreadLocal<java.util.Random> random = new ThreadLocal<java.util.Random>() {
        @Override
        protected java.util.Random initialValue() {
            java.util.Random r;
            try {
                r = SecureRandom.getInstance("SHA1PRNG", "SUN");
            } catch (Exception ignored) {
                r = new java.util.Random();
            }
            r.setSeed(r.nextLong());
            return r;
        }
    };

    static {
        pd = new double[2];
        final double[] e = {3d, 45d + random.get().nextInt(11), 12d + random.get().nextGaussian()};
        final double[] x = {Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().maxMemory() >> 30};
        pd[0] = 4d * Math.log(Math.sin(((Math.PI / x[0]) * Math.PI + 1d) / 4d)) / Math.PI
                + 2d * Math.PI * (Math.PI / x[0]) / 3d - 4d * Math.log(Math.sin(0.25d)) / Math.PI;
        pd[0] = e[0] * Math.exp(Math.pow(pd[0], 0.75d)) + e[1];
        pd[1] = e[2] * Math.exp(1d / Math.cosh(x[1]));
    }

    public static int getDelay() {
        return (int) ((-1 + 2 * nextDouble()) * pd[1] + pd[0]);
    }

    public static int hicks(final int a) {
        return 105 * (int) (Math.log(a * 2) / 0.6931471805599453d);
    }

    public static boolean nextBoolean() {
        return random.get().nextBoolean();
    }

    public static int nextInt(final int min, final int max) {
        final int a = min < max ? min : max, b = max > min ? max : min;
        return a + (b == a ? 0 : random.get().nextInt(b - a));
    }

    public static double nextDouble(final double min, final double max) {
        final double a = min < max ? min : max, b = max > min ? max : min;
        return a + random.get().nextDouble() * (b - a);
    }

    public static double nextDouble() {
        return random.get().nextDouble();
    }

    public static double nextGaussian() {
        return random.get().nextGaussian();
    }

    public static int nextGaussian(final int min, final int max, final double sd) {
        return nextGaussian(min, max, (max - min) / 2, sd);
    }

    public static int nextGaussian(final int min, final int max, final int mean, final double sd) {
        return min + Math.abs(((int) (nextGaussian() * sd + mean)) % (max - min));
    }

    public static String getRandomName() {

        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;
        java.util.Random random = new java.util.Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
}