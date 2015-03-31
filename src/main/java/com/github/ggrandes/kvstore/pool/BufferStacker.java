/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.github.ggrandes.kvstore.pool;

import java.nio.ByteBuffer;

import com.github.ggrandes.kvstore.structures.hash.IntHashMap;

/**
 * Pool of ByteBuffers ByteBuffer池. 就像连接池中存放的是多个连接. ByteBuffer池也存放多个ByteBuffer
 * This class is Thread-Safe
 * 
 * <a href="http://www.evanjones.ca/software/java-bytebuffers.html">Efficient Java I/O: ByteBuffer</a>
 * 
 * @author Guillermo Grandes / guillermo.grandes[at]gmail.com
 */
public class BufferStacker {
	//ByteBuffer池
	private static IntHashMap<BufferStacker> INSTANCES = new IntHashMap<BufferStacker>(BufferStacker.class);
	//
	private java.util.Deque<ByteBuffer> stack = new java.util.ArrayDeque<ByteBuffer>();
	private final int bufferLen;
	private int created = 0;

	//
	private BufferStacker() {
		this(1024);
	}

	private BufferStacker(final int bufferLen) {
		this.bufferLen = bufferLen;
	}

	//
	public static BufferStacker getInstance(final int bufferLen, final boolean isDirect) {
		final int key = composeKey(bufferLen, isDirect);
		synchronized (INSTANCES) {
			BufferStacker bs = INSTANCES.get(key);
            //如果这个ByteBuffer没有初始化, 则初始化, 并放入ByteBuffer池中
            //如果已经存在, 则直接返回给调用者. 就像连接池中的连接如果已经存在, 则直接返回给客户端
			if (bs == null) {
				bs = new BufferStacker(bufferLen);
				INSTANCES.put(key, bs);
			}
			return bs;
		}
	}

	//给定一个长度, 使用ByteBuffer池给它分配一个ByteBuffer
	private static final int composeKey(final int bufferLen, final boolean isDirect) {
		return (((bufferLen & 0x3FFFFFFF) << 1) | (isDirect ? 1 : 0));
	}

	//
	public synchronized void push(final ByteBuffer buf) {
		stack.addFirst(buf);
	}

	public synchronized ByteBuffer pop() {
		final ByteBuffer buf = stack.pollFirst();
		if (buf == null) {
			created++;
			return ByteBuffer.allocate(bufferLen);
		}
		buf.clear();
		return buf;
	}

	public String toString() {
		return super.toString() + ": created=" + created;
	}
}
