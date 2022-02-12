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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

public abstract class DemoTest {

  protected abstract LongSerde createSerde();

  private byte[] encode(Collection<Long> longs) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      createSerde().encode(new DataOutputStream(baos), longs);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return baos.toByteArray();
  }

  private List<Long> decode(byte[] data) {
    LongSerde s = createSerde();
    List<Long> ret = new ArrayList<>();
    try {
      s.decode(new ByteArrayInputStream(data), ret);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ret;
  }

  @Test
  public void testNullEncode() {
    assertThrows(NullPointerException.class, () -> createSerde().encode(null, null));
    assertThrows(
        NullPointerException.class,
        () -> createSerde().encode(new DataOutputStream(new ByteArrayOutputStream()), null));
    assertThrows(
        NullPointerException.class, () -> createSerde().encode(null, Collections.emptyList()));
  }

  @Test
  public void testNullDecode() {
    assertThrows(
        NullPointerException.class, () -> createSerde().decode(null, (Collection<Long>) null));
    assertThrows(
        NullPointerException.class,
        () ->
            createSerde()
                .decode(new ByteArrayInputStream(new byte[] {7, 8, 9}), (Collection<Long>) null));
    assertThrows(NullPointerException.class, () -> createSerde().decode(null, new ArrayList<>()));
  }

  @Test
  public void testEmptyEncodeDecode() {
    assertThat(decode(encode(Collections.emptyList())), equalTo(Collections.emptyList()));
    assertThat(decode(new byte[] {}), equalTo(Collections.emptyList()));
  }

  @Test
  public void testMinAvg() {
    List<Long> longs = LongStream.range(0, 1000).boxed().sorted().collect(Collectors.toList());
    assertThat(decode(encode(longs)), equalTo(longs));
  }

  @Test
  public void testLowAvg() {
    List<Long> longs = LongStream.range(0, 2).boxed().collect(Collectors.toList());
    assertThat(decode(encode(longs)), equalTo(longs));
  }

  @Test
  public void testHighAvg() {
    List<Long> longs =
        LongStream.range(1, 4)
            .map(l -> Long.MAX_VALUE / l)
            .boxed()
            .sorted()
            .collect(Collectors.toList());
    assertThat(decode(encode(longs)), equalTo(longs));
  }

  @Test
  public void testMaxDiffValue() {
    List<Long> longs = List.of(0L, (long) (Integer.MAX_VALUE * 2.5));
    assertThat(decode(encode(longs)), equalTo(longs));
  }

  @Test
  public void testRandomEncodeDecode() {
    int iterations = 1000;
    int maxElements = 1000;
    Random r = new Random(7);
    LongSerde s = createSerde();
    AtomicLong totalEncodedBytes = new AtomicLong();
    AtomicLong totalRawBytes = new AtomicLong();
    LongSupplier nextLong = () -> Math.abs(r.nextLong());
    IntStream.range(0, iterations)
        .forEach(
            iteration -> {
              List<Long> elements =
                  LongStream.generate(nextLong)
                      .limit(maxElements)
                      .sorted()
                      .boxed()
                      .collect(Collectors.toList());
              totalRawBytes.addAndGet(elements.size() * 8L);
              byte[] encoded = encode(elements);
              totalEncodedBytes.addAndGet(encoded.length);
              List<Long> decoded = decode(encoded);
              assertThat(elements, equalTo(decoded));
            });
    double compressionRatio = totalRawBytes.get() / (double) totalEncodedBytes.get();
    System.out.printf("%s compression ratio - %s", s.getClass().getSimpleName(), compressionRatio);
  }
}
