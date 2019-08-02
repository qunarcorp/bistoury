/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.instrument.client.metrics.adapter;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 15-5-7
 * Time: 下午7:12
 */
public class StatsBuffer {

    private static final long ONE_HALF_MINUTES = TimeUnit.SECONDS.toMillis(90);

    private static final int INIT_SIZE = 1000;

    private final double[] percentiles;
    private final double[] percentileValues;
    private final AtomicInteger count;
    private int[] values;
    private final int maxSize;
    private long lastResetTime;

    private final AtomicBoolean statsComputed = new AtomicBoolean(false);
    private final ReadWriteLock valuesGuard = new ReentrantReadWriteLock();
    private final Lock recordValueGuard = valuesGuard.readLock();
    private final Lock computeStatsGuard = valuesGuard.writeLock();

    /**
     * Create a circular buffer that will be used to record values and compute useful stats.
     *
     * @param maxSize        The capacity of the buffer
     * @param percentiles Array of percentiles to compute. For example { 95.0, 99.0 }.
     *                    If no percentileValues are required pass a 0-sized array.
     */
    public StatsBuffer(int maxSize, double[] percentiles) {
        Preconditions.checkArgument(maxSize > 0, "Size of the buffer must be greater than 0");
        Preconditions.checkArgument(percentiles != null,
                "Percents array must be non-null. Pass a 0-sized array "
                        + "if you don't want any percentileValues to be computed.");
        Preconditions.checkArgument(validPercentiles(percentiles),
                "All percentiles should be in the interval (0.0, 100.0]");
        this.count = new AtomicInteger(0);
        this.values = new int[INIT_SIZE];
        this.maxSize = maxSize;
        this.percentiles = Arrays.copyOf(percentiles, percentiles.length);
        this.percentileValues = new double[percentiles.length];

        reset();
    }

    private static boolean validPercentiles(double[] percentiles) {
        for (double percentile : percentiles) {
            if (percentile <= 0.0 || percentile > 100.0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reset our local state: All values are set to 0.
     */
    public void reset() {
        lastResetTime = System.currentTimeMillis();
        statsComputed.set(false);
        count.set(0);
        Arrays.fill(percentileValues, 0.0);
    }

    /**
     * Record a new value for this buffer.
     */
    public void record(int n) {
        recordValueGuard.lock();
        try {
            final int countAndIncrement = count.getAndIncrement();
            int index = Math.abs(countAndIncrement % values.length);
            values[index] = n;
        } finally {
            recordValueGuard.unlock();
        }
    }

    private void expandCapacityIfNeed() {
        if (previousComputeExist() && count.get() > values.length && values.length < maxSize) {
            values = new int[values.length * 2];
        }
    }

    private boolean previousComputeExist() {
        return System.currentTimeMillis() - lastResetTime < ONE_HALF_MINUTES;
    }

    /**
     * Compute stats for the current set of values.
     */
    public void computeStats() {
        if (statsComputed.getAndSet(true)) {
            return;
        }

        if (count.get() == 0) {
            return;
        }

        computeStatsGuard.lock();
        try {

            int curSize = Math.min(count.get(), values.length);
            Arrays.sort(values, 0, curSize); // to compute percentileValues
            computePercentiles(curSize);
            expandCapacityIfNeed();
        } finally {
            computeStatsGuard.unlock();
        }
    }

    private void computePercentiles(int curSize) {
        for (int i = 0; i < percentiles.length; ++i) {
            percentileValues[i] = calcPercentile(curSize, percentiles[i]);
        }
    }

    private double calcPercentile(int curSize, double percent) {
        if (curSize == 0) {
            return 0.0;
        }
        if (curSize == 1) {
            return values[0];
        }

         /*
          * We use the definition from http://cnx.org/content/m10805/latest
          * modified for 0-indexed arrays.
          */
        final double rank = percent * curSize / 100.0; // SUPPRESS CHECKSTYLE MagicNumber
        final int ir = (int) Math.floor(rank);
        final int irNext = ir + 1;
        final double fr = rank - ir;
        if (irNext >= curSize) {
            return values[curSize - 1];
        } else if (fr == 0.0) {
            return values[ir];
        } else {
            // Interpolate between the two bounding values
            final double lower = values[ir];
            final double upper = values[irNext];
            return fr * (upper - lower) + lower;
        }
    }

    /**
     * Get the number of entries recorded.
     */
    public int getCount() {
        return count.get();
    }

    public double[] getPercentileValues() {
        return percentileValues;
    }

    /**
     * Return the percentiles we will compute: For example: 95.0, 99.0.
     */
    public double[] getPercentiles() {
        return Arrays.copyOf(percentiles, percentiles.length);
    }
}
