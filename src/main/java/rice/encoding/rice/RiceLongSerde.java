/*
 * MIT License
 *
 * Copyright (c) 2022 Can Özbey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rice.encoding.rice;

import rice.encoding.LongSerde;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.LongConsumer;

/**
 * A golomb/rice long serializer
 */
public class RiceLongSerde implements LongSerde {

    @Override
    public void encode(OutputStream os, Collection<Long> ids) throws IOException {
        Diffs diffs = new Diffs(ids);
        byte log2avg = (byte) Math.max((byte) 1, log2(diffs.avg));
        try (RiceSinkByteFsm rs = new RiceSinkByteFsm(log2avg, os)) {
            os.write(log2avg);
            for (long diff : diffs.diffs) {
                rs.write(diff);
            }
        }
    }

    public static byte log2(long val) {
        if (val == 0) {
            return 0;
        }

        return (byte) (63 - Long.numberOfLeadingZeros(val));
    }

    @Override
    public void decode(InputStream bis, LongConsumer action) throws IOException {
        byte pow2mid = (byte) bis.read();
        RiceSourceByteFsm rs = new RiceSourceByteFsm(pow2mid, new LastValueCachedInputStream(bis));

        final long[] last = {0};
        boolean hasNext;
        do {
            hasNext = rs.tryAdvance(diff -> {
                long id = last[0] + diff;
                last[0] = id;
                action.accept(id);
            });
        } while (hasNext);
    }


    private final static class Diffs {
        private final List<Long> diffs;
        private final long avg;

        private Diffs(Collection<Long> original) {
            int sz = original.size();
            diffs = new ArrayList<>(sz);
            LongRunningAvg runningAvg = new LongRunningAvg();
            long last = 0;
            for (long id : original) {
                long diff = id - last;
                diffs.add(diff);
                runningAvg.add(diff);
                last = id;
            }
            avg = runningAvg.get();
        }
    }

    /**
     * A running average
     */
    private static class LongRunningAvg {
        double currentAverage = 0;
        long sz = 0;

        public long add(double value) {
            return (long) (currentAverage = (currentAverage * sz + value) / ++sz);
        }

        public long get() {
            return (long) currentAverage;
        }
    }
}
