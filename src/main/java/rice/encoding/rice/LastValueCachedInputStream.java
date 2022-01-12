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

/**
 * Input stream which caches the last read value and has special methods for moving along across the underlying stream
 */
public class LastValueCachedInputStream extends InputStream {
    private final InputStream in;
    private int lastRead;

    /**
     * Construct a {@code CountingInputStream}
     *
     * @param in input stream
     */
    public LastValueCachedInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public int read() throws IOException {
        lastRead = in.read();
        return lastRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        lastRead = in.read(b, off, len);
        return lastRead;
    }

    public int readLast() {
        return lastRead;
    }

    public int readLastAndMove() throws IOException {
        int lastRead = this.lastRead;
        read();
        return lastRead;
    }

    public int moveAndRead() throws IOException {
        return read();
    }

    public void move() throws IOException {
        read();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
    
    public boolean isAtEof() {
        return lastRead == -1;
    }
}