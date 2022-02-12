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

public enum State {

    S {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.moveAndRead() & 0xFF;

            if (b > 1) {
                return Q2;
            }
            if (b == 1) {
                return Q1;
            }
            return Q0;
        }

    },

    Q0 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.moveAndRead() & 0xFF;

            var.q = 8;

            if (b > 1) {
                return Q5;
            }
            if (b == 1) {
                return Q4;
            }
            return Q3;
        }

    },
    Q1 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            stream.move();

            var.q = 7;

            if (var.d < 8) {
                return R2;
            }
            if (var.d > 8) {
                return R0;
            }
            return R1;
        }

    },
    Q2 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLast() & 0xFF;

            var.c = var.log2[b];

            var.q = 7 - var.c;

            if (var.d < var.c) {
                return R7;
            }
            if (var.d > var.c) {
                return T0;
            }
            return R6;
        }

    },
    Q3 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.moveAndRead() & 0xFF;

            var.q += 8;

            if (b > 1) {
                return Q5;
            }
            if (b == 1) {
                return Q4;
            }
            return Q3;
        }
    },
    Q4 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            stream.move();

            var.q += 7;

            if (var.d < 8) {
                return R2;
            }
            if (var.d > 8) {
                return R0;
            }
            return R1;
        }

    },
    Q5 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLast() & 0xFF;

            var.c = var.log2[b];

            var.q += 7 - var.c;

            if (var.d < var.c) {
                return R7;
            }
            if (var.d > var.c) {
                return T0;
            }
            return R6;
        }

    },
    Q6 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            stream.move();

            var.q = var.c - var.d - 1;

            var.d = var.k;

            if (var.d < 8) {
                return R2;
            }
            if (var.d > 8) {
                return R0;
            }
            return R1;
        }

    },
    Q7 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            long t = var.log2[var.m];

            var.q = var.c - var.d - t - 1;

            var.c = t;

            var.d = var.k;

            if (var.d < var.c) {
                return R7;
            }
            if (var.d > var.c) {
                return T0;
            }
            return R6;
        }

    },
    T0 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLastAndMove() & 0xFF;

            var.r = b & (255 >> (8 - var.c));

            var.d -= var.c;

            var.c = 8;

            if (var.d < 8) {
                return R5;
            }
            if (var.d > 8) {
                return R3;
            }
            return R4;
        }

    },
    R0 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            var.r = stream.readLastAndMove() & 0xFF;

            var.d -= 8;

            if (var.d < 8) {
                return R5;
            }
            if (var.d > 8) {
                return R3;
            }
            return R4;
        }

    },
    R1 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLastAndMove() & 0xFF;

            var.r = b;

            var.emit(action);    //accept

            if (stream.isAtEof()) return F;

            b = stream.readLast() & 0xFF;

            if (b > 1) {
                return Q2;
            }
            if (b == 1) {
                return Q1;
            }
            return Q0;
        }

    },
    R2 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLast() & 0xFF;

            var.r = b >> (8 - var.d);

            var.emit(action);    //accept

            var.m = b & (255 >> var.d);

            var.c = 8;

            if (var.m > 1) {
                return Q7;
            }
            if (var.m == 1) {
                return Q6;
            }
            return T1;
        }

    },
    R3 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLastAndMove() & 0xFF;

            var.r = (var.r << 8) | b;

            var.d -= 8;

            if (var.d < 8) {
                return R5;
            }
            if (var.d > 8) {
                return R3;
            }
            return R4;
        }


    },
    R4 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLastAndMove() & 0xFF;

            var.r = (var.r << 8) | b;

            var.emit(action);    //accept

            var.d = var.k;

            if (stream.isAtEof()) return F;

            b = stream.readLast() & 0xFF;

            if (b > 1) {
                return Q2;
            }
            if (b == 1) {
                return Q1;
            }
            return Q0;
        }

    },
    R5 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLast() & 0xFF;

            long t = b >> (8 - var.d);

            var.m = b & (255 >> var.d);

            var.r = (var.r << var.d) | t;

            var.emit(action);    //accept

            if (var.m > 1) {
                return Q7;
            }
            if (var.m == 1) {
                return Q6;
            }
            return T1;
        }

    },
    R6 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLastAndMove() & 0xFF;

            var.r = b & (255 >> (8 - var.d));

            var.emit(action);    //accept

            if (stream.isAtEof()) return F;

            b = stream.readLast() & 0xFF;

            if (b > 1) {
                return Q2;
            }
            if (b == 1) {
                return Q1;
            }
            return Q0;
        }

    },
    R7 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.readLast() & 0xFF;

            long t = 8 - var.c;

            var.r = (b & (255 >> t)) >> (var.c - var.d);

            var.emit(action);    //accept

            var.m = (b & (255 >> (t + var.d)));

            if (var.m > 1) {
                return Q7;
            }
            if (var.m == 1) {
                return Q6;
            }
            return T1;
        }

    },
    T1 {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            int b = stream.moveAndRead() & 0xFF;

            if (stream.isAtEof()) return F;

            var.q = var.c - var.d;

            var.d = var.k;

            if (b > 1) {
                return Q5;
            }
            if (b == 1) {
                return Q4;
            }
            return Q3;
        }
    },
    F {
        @Override
        public State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException {

            return null;
        }
    };

    public abstract State execute(LastValueCachedInputStream stream, StateVariables var, LongConsumer action) throws IOException;


}
