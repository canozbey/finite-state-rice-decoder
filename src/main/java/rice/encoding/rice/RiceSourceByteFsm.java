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
import java.util.function.LongConsumer;

/**
 * A "rice" encoding source that reads from an {@link java.io.InputStream}
 * <p>
 * This class is *not* thread safe
 * <p>
 * This implementation is based on <a href="https://www.researchgate.net/publication/344635127_Efficient_Finite-State_Decoding_of_Rice_Codes_for_Large_Alphabets">this paper</a>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Golomb_coding">Rice Encoding</a>
 */
public class RiceSourceByteFsm {
    private final LastValueCachedInputStream stream;
    private State state;
    StateVariables var;


    public RiceSourceByteFsm(byte k, LastValueCachedInputStream riceroni) {
        stream = riceroni;
        var = new StateVariables((int) k);
        state = State.S;
    }

    public boolean tryAdvance(LongConsumer action) throws IOException {
        if (isAtEof()) {
            return false;
        }
        state = state.execute(stream, var, action);
        return true;
    }

    private boolean isAtEof() {
        return stream.isAtEof() || state.equals(State.F);
    }
}
