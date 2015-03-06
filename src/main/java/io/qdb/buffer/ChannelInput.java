/*
 * Copyright 2012 David Tinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.qdb.buffer;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Fast class to treat a FileChannel more or less like a stream with synchronization on the channel. Reading past
 * the end of the channel will produce an IOException.
 */
class ChannelInput {

    private final FileChannel channel;
    private final ByteBuffer buffer;

    private int nextBufferPosition;

    ChannelInput(FileChannel channel, int position, int bufferSize) throws IOException {
        this.channel = channel;
        this.nextBufferPosition = position;
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
        buffer.limit(0);
    }

    private void fill() throws IOException {
        buffer.compact();
        synchronized (channel) {
            channel.position(nextBufferPosition);
            int sz = channel.read(buffer);
            if (sz == 0) throw new EOFException();
            nextBufferPosition += sz;
        }
        buffer.flip();
    }

    public int position() {
        return nextBufferPosition - buffer.remaining();
    }

    public void position(int newPosition) {
        if (newPosition == position()) return;
        if (newPosition >= nextBufferPosition) {
            nextBufferPosition = newPosition;
            buffer.limit(0);
            return;
        }
        int startOfBuffer = nextBufferPosition - buffer.limit();
        if (newPosition < startOfBuffer) {
            nextBufferPosition = newPosition;
            buffer.limit(0);
            return;
        }
        // position to seek to is in buffer
        buffer.position(newPosition - startOfBuffer);
    }

    public byte readByte() throws IOException {
        if (!buffer.hasRemaining()) fill();
        return buffer.get();
    }

    public short readShort() throws IOException {
        if (buffer.remaining() < 2) fill();
        return buffer.getShort();
    }

    public int readInt() throws IOException {
        if (buffer.remaining() < 4) fill();
        return buffer.getInt();
    }

    public long readLong() throws IOException {
        if (buffer.remaining() < 8) fill();
        return buffer.getLong();
    }

    public void read(byte[] dst, int offset, int length) throws IOException {
        for (; length > 0; ) {
            int remaining = buffer.remaining();
            if (length <= remaining) {
                buffer.get(dst, offset, length);
                break;
            }
            buffer.get(dst, offset, remaining);
            offset += remaining;
            length -= remaining;
            fill();
        }
    }

    public void skip(int bytes) {
        int remaining = buffer.remaining();
        if (bytes < remaining) {
            buffer.position(buffer.position() + bytes);
        } else {
            buffer.position(0);
            buffer.limit(0);
            nextBufferPosition += bytes - remaining;
        }
    }
}
