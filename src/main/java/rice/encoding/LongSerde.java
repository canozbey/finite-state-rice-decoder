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

package rice.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.function.LongConsumer;

/** A long integer encoding interface */
public interface LongSerde {
  /**
   * Encode a sorted collection of longs into an output stream
   *
   * @param os output stream
   * @param longs sorted collection of longs
   * @throws IOException if an encoding error occurs
   */
  void encode(OutputStream os, Collection<Long> longs) throws IOException;

  /**
   * Decode a sorted collection of longs from an input stream
   *
   * @param is input stream
   * @param output output collection to insert longs into
   * @throws IOException if a decoding error occurs
   */
  default void decode(InputStream is, Collection<Long> output) throws IOException {
    decode(is, output::add);
  }

  /**
   * Decode longs from an input stream and do some action on every decoded value
   *
   * @param is input stream
   * @throws IOException if a decoding error occurs
   */
  void decode(InputStream is, LongConsumer action) throws IOException;
}
