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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.LongConsumer;
import rice.encoding.GolombParamAlgo;
import rice.encoding.LongSerde;

/** A golomb/rice long serializer */
public class RiceLongSerde implements LongSerde {

  private final GolombParamAlgo golombParamAlgo;

  public RiceLongSerde(GolombParamAlgo golombParamAlgo) {
    this.golombParamAlgo = golombParamAlgo;
  }

  @Override
  public void encode(OutputStream os, Collection<Long> ids) throws IOException {
    Diffs diffs = new Diffs(ids, golombParamAlgo);
    byte k = golombParamAlgo.calculateK();
    try (RiceSinkByteFsm rs = new RiceSinkByteFsm(k, os)) {
      os.write(k);
      for (long diff : diffs.diffs) {
        rs.write(diff);
      }
    }
  }

  @Override
  public void decode(InputStream bis, LongConsumer action) throws IOException {
    byte pow2mid = (byte) bis.read();
    RiceSourceByteFsm rs = new RiceSourceByteFsm(pow2mid, new LastValueCachedInputStream(bis));

    final long[] last = {0};
    boolean hasNext;
    do {
      hasNext =
          rs.tryAdvance(
              diff -> {
                long id = last[0] + diff;
                last[0] = id;
                action.accept(id);
              });
    } while (hasNext);
  }

  private static final class Diffs {
    private final List<Long> diffs;
    private final long avg;

    private Diffs(Collection<Long> original, GolombParamAlgo golombParamAlgo) {
      int sz = original.size();
      diffs = new ArrayList<>(sz);
      LongRunningAvg runningAvg = new LongRunningAvg();
      long last = 0;
      for (long id : original) {
        long diff = id - last;
        diffs.add(diff);
        runningAvg.add(diff);
        golombParamAlgo.acceptElement(diff);
        last = id;
      }
      avg = runningAvg.get();
    }
  }

  /** A running average */
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
