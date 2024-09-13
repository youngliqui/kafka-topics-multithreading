package ru.clevertec.util;

import ru.clevertec.exception.RandomTimeoutGeneratorException;

import java.util.Random;

public class RandomTimeoutGenerator {
    private static final Random random = new Random();

    public static long generateRandomTimeout(long min, long max) {
        if (min < 0 || max < 0 || min >= max) {
            throw new RandomTimeoutGeneratorException(
                    "The minimum value must be less than the maximum and both values must be positive");
        }
        return min + (long) (random.nextDouble() * (max - min));
    }
}
