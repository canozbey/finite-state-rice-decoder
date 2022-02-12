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

package rice.encoding.rice.param;

/**
 * @see <a
 *     href=https://www.researchgate.net/publication/252469081_Selecting_the_Golomb_Parameter_in_Rice_Coding>Selecting
 *     the Golomb Parameter in Rice Coding</a>
 */
public class KielysFormula extends Log2ArithmeticMean {
  // golden ratio minus 1
  public static final double FI_MINUS_ONE = 0.61803398875;

  @Override
  public byte calculateK() {

    double ratio = Math.log(FI_MINUS_ONE) / Math.log(currentAverage / (currentAverage + 1));

    return (byte) Math.max((int) (Math.log(ratio) / Math.log(2)) + 1, 0);
  }
}
