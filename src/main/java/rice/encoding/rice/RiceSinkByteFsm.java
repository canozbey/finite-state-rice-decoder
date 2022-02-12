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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A "Rice" encoding sink that wraps an {@code OutputStream}. Clients must call the {@link #close}
 * method to commit changes to the output stream when finished writing.
 *
 * <p>This class is *not* thread safe
 *
 * <p>This implementation is based on <a
 * href="https://www.researchgate.net/publication/344635127_Efficient_Finite-State_Decoding_of_Rice_Codes_for_Large_Alphabets">this
 * paper</a>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Golomb_coding">Rice Encoding</a>
 */
public class RiceSinkByteFsm implements Closeable {
  private final int k;
  private final OutputStream outputStream;
  long mask;
  long bitCount = 0;
  long last = 0;

  public RiceSinkByteFsm(byte k, OutputStream outputStream) {
    this.k = k;
    mask = lastNBits(0xffffffff, k);
    this.outputStream = outputStream;
  }

  public void write(long val) throws IOException {
    // if length of q does not fit to int whole compressing doesn't make sense
    int q = Math.toIntExact(val >> k) + 1;
    long r = val & mask;
    int len = q + k;
    long toConsume = 8 - tailLength();

    if (len <= toConsume) {
      last = (last << len) | ((1L << k) | r);
      bitCount += len;

      if (len == toConsume) {
        outputStream.write((int) last);
        last = 0;
      }

    } else {

      if (q <= toConsume) {
        last = (last << q) | 1;
        long headLengthR = toConsume - q; // length of r head to consume
        long remainLengthR = k - headLengthR; // remaining length of r
        last = (last << headLengthR) | (r >> remainLengthR);
        r = writeBytesBlongToR(r, remainLengthR);
        bitCount += len;
        last = r;

      } else {
        outputStream.write((int) (last << toConsume)); // append 0's and send it
        long y = q - toConsume; // remaining length of q

        while (y > 8) {
          y -= 8;
          outputStream.write(0);
        }
        toConsume = 8;
        last = 0;

        if (y == 8) {
          outputStream.write(1);

        } else {
          toConsume -= y;
        }

        if (toConsume > k) {
          last = (1L << k) | r;

          bitCount += len;

        } else {
          y = k - toConsume; // remaining length of r
          last = (1L << toConsume) | (r >> y);
          r = writeBytesBlongToR(r, y);
          bitCount += len;
          last = r;
        }
      }
    }
  }

  private long writeBytesBlongToR(long r, long y) throws IOException {
    outputStream.write((int) last); // append this byte;
    r = lastNBits(r, y); // rest of r

    while (y >= 8) {
      y -= 8;
      outputStream.write((int) (r >> y)); // append part of r;
      r = lastNBits(r, y);
    }
    return r;
  }

  private long tailLength() {
    return bitCount & 7;
  }

  private long lastNBits(long val, long numberOfRemainBits) {
    return val & ((1L << numberOfRemainBits) - 1L);
  }

  @Override
  public void close() throws IOException {
    long tail = tailLength();

    if (tail != 0) {
      outputStream.write((int) (last << (8 - tail))); // make padding and then add
    }
  }
}
