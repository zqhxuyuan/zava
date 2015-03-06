package org.zbus.common.remoting.nio;

import java.nio.ByteBuffer;

public final class IoBuffer {
	private ByteBuffer buf = null;

	private IoBuffer(ByteBuffer buf) {
		this.buf = buf; 
	}

	public static IoBuffer wrap(ByteBuffer buf) {
		return new IoBuffer(buf);
	}

	public static IoBuffer wrap(byte[] buf) {
		return new IoBuffer(ByteBuffer.wrap(buf));
	}
	
	public static IoBuffer wrap(String str){
		return wrap(str.getBytes());
	}

	public static IoBuffer allocate(int capacity) {
		return new IoBuffer(ByteBuffer.allocate(capacity));
	}

	public static IoBuffer allocateDirect(int capacity) {
		return new IoBuffer(ByteBuffer.allocateDirect(capacity));
	}

	public byte[] array(){
		return this.buf.array();
	}
	
	public byte get() {
		return this.buf.get();
	}

	public IoBuffer get(byte[] dst) {
		this.buf.get(dst);
		return this;
	}

	public IoBuffer get(byte[] dst, int offset, int length) {
		this.buf.get(dst, offset, length);
		return this;
	}

	public short getShort() {
		return this.buf.getShort();
	}

	public int getInt() {
		return this.buf.getInt();
	}
	
	public long getLong() {
		return this.buf.getLong();
	}

	public IoBuffer put(byte value) {
		return put(new byte[] { value });
	}

	public IoBuffer put(byte[] src) {
		return put(src, 0, src.length);
	}

	public IoBuffer put(byte[] src, int offset, int length) {
		autoExpand(src.length, offset, length);
		this.buf.put(src, offset, length);
		return this;
	}

	public IoBuffer put(char c) {
		this.buf.putChar(c);
		return this;
	}

	public IoBuffer put(short value) {
		this.buf.putShort(value);
		return this;
	}

	public IoBuffer put(long value) {
		this.buf.putLong(value);
		return this;
	}
	public IoBuffer put(int value) {
		this.buf.putInt(value);
		return this;
	}
	
	public IoBuffer put(String value) {
		this.put(value.getBytes());
		return this;
	}

	public final IoBuffer flip() {
		this.buf.flip();
		return this;
	}

	public final byte get(int index) {
		return this.buf.get(index);
	}

	public final IoBuffer unflip() {
		buf.position(buf.limit());
		if (buf.limit() != buf.capacity())
			buf.limit(buf.capacity());
		return this;
	}
	
	

	public final IoBuffer rewind() {
		this.buf.rewind();
		return this;
	}

	public final int remaining() {
		return this.buf.remaining();
	}

	public final IoBuffer mark() {
		this.buf.mark();
		return this;
	}

	public final IoBuffer reset() {
		this.buf.reset();
		return this;
	}

	public final int position() {
		return this.buf.position();
	}

	public final IoBuffer position(int newPosition) {
		this.buf.position(newPosition);
		return this;
	}
	
	public final int limit(){
		return this.buf.limit();
	}

	public final IoBuffer duplicate() {
		return IoBuffer.wrap(this.buf.duplicate());
	}

	public final IoBuffer compact() {
		this.buf.compact();
		return this;
	}

	public final ByteBuffer buf() {
		return this.buf;
	}

	private void autoExpand(int size, int offset, int length) {
		int newCapacity = this.buf.capacity();
		int newSize = this.buf.position() + length;
		ByteBuffer newBuffer = null;

		if (size < length)
			throw new IndexOutOfBoundsException();

		while (newSize > newCapacity) {
			newCapacity = newCapacity * 2;
		}

		// Auto expand capacity
		if (newCapacity != this.buf.capacity()) {
			if (this.buf.isDirect()) {
				newBuffer = ByteBuffer.allocateDirect(newCapacity);
			} else {
				newBuffer = ByteBuffer.allocate(newCapacity);
			}

			newBuffer.put(this.buf.array());
			newBuffer.position(this.buf.position());

			this.buf = newBuffer;
		}
	}

	@Override
	public String toString() {
		return "IoBuffer [remaining=" + buf.remaining() + "]";
	}
	
	
	public static void main(String[] args){
		IoBuffer buf = IoBuffer.allocate(8192);
		System.out.println(buf.remaining());
	}
	
}
