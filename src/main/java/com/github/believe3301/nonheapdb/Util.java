package com.github.believe3301.nonheapdb;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Util {
	public static int Gb(double giga) {
		return (int) giga * 1024 * 1024 * 1024;
	}

	public static int Mb(double mega) {
		return (int) mega * 1024 * 1024;
	}

	public static int Kb(double kilo) {
		return (int) kilo * 1024;
	}

	public static int unlimited() {
		return -1;
	}
	
	public static int align(final int value, final int align) {
		return (value + align - 1) & ~(align - 1);
	}
	
	public static long align(final long value, final int align) {
		return (value + align - 1) & ~(align - 1);
	}

	/*
	 * protobuf varint
	 */
	public static byte[] writeVarInt(int value) {
		byte[] arr = new byte[8];
		ByteBuffer buf = ByteBuffer.wrap(arr);
		writeVarInt(value, buf);
		return Arrays.copyOf(arr, buf.position());
	}

	public static void writeVarInt(int value, ByteBuffer buf) {
		while ((value & 0xFFFFFF80) != 0L) {
			buf.put((byte) ((value & 0x7F) | 0x80));
			value >>>= 7;
		}
		buf.put((byte) (value & 0x7F));
	}

	public static int readVarInt(byte[] arr) {
		return readVarInt(ByteBuffer.wrap(arr));
	}

	public static int readVarInt(byte[] arr, int offset, int length) {
		return readVarInt(ByteBuffer.wrap(arr, offset, length));
	}

	public static int readVarInt(ByteBuffer buf) {
		int value = 0;
		int i = 0;
		int b;
		while (((b = buf.get()) & 0x80) != 0) {
			value |= (b & 0x7F) << i;
			i += 7;
		}
		return value | (b << i);
	}
	
	public static byte[] writeVarLong(long value) {
		byte[] arr = new byte[12];
		ByteBuffer buf = ByteBuffer.wrap(arr);
		writeVarLong(value, buf);
		return Arrays.copyOf(arr, buf.position());
	}

	
	public static void writeVarLong(long value, ByteBuffer buf) {
		while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
			buf.put((byte) ((value & 0x7F) | 0x80));
			value >>>= 7;
		}
		buf.put((byte) (value & 0x7F));
	}
	
	public static long readVarLong(byte[] arr) {
		return readVarLong(ByteBuffer.wrap(arr));
	}

	public static long readVarLong(byte[] arr, int offset, int length) {
		return readVarLong(ByteBuffer.wrap(arr, offset, length));
	}
	
	public static long readVarLong(ByteBuffer buf) {
		long value = 0L;
		int i = 0;
		long b;
		while (((b = buf.get()) & 0x80L) != 0) {
			value |= (b & 0x7F) << i;
		    i += 7;
		}
		return value | (b << i);
	}


	/*
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 * contributor license agreements. See the NOTICE file distributed with this
	 * work for additional information regarding copyright ownership. The ASF
	 * licenses this file to You under the Apache License, Version 2.0 (the
	 * "License"); you may not use this file except in compliance with the
	 * License. You may obtain a copy of the License at
	 * 
	 * http://www.apache.org/licenses/LICENSE-2.0
	 * 
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 */

	/*
	 * hexdump,start from offset, dump length bytes
	 */
	public static String hexDump(final byte[] data, final int offset,
			final int length) {

		int display_offset = offset;
		StringBuilder sb = new StringBuilder();
		final StringBuilder buffer = new StringBuilder(74);

		for (int j = offset; j < data.length; j += 16) {
			int chars_read = data.length - j;

			if (chars_read > 16) {
				chars_read = 16;
			}
			dump(buffer, display_offset).append(' ');
			for (int k = 0; k < 16; k++) {
				if (k < chars_read) {
					dump(buffer, data[k + j]);
				} else {
					buffer.append("  ");
				}
				buffer.append(' ');
			}
			for (int k = 0; k < chars_read; k++) {
				if (data[k + j] >= ' ' && data[k + j] < 127) {
					buffer.append((char) data[k + j]);
				} else {
					buffer.append('.');
				}
			}
			buffer.append(EOL);
			sb.append(buffer);
			buffer.setLength(0);
			display_offset += chars_read;
		}

		return sb.toString();
	}

	/**
	 * The line-separator (initializes to "line.separator" system property.
	 */
	public static final String EOL = System.getProperty("line.separator");
	
	private static final char[] _hexcodes = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final int[] _shifts = { 28, 24, 20, 16, 12, 8, 4, 0 };

	/**
	 * Dump a long value into a StringBuilder.
	 * 
	 * @param _lbuffer
	 *            the StringBuilder to dump the value in
	 * @param value
	 *            the long value to be dumped
	 * @return StringBuilder containing the dumped value.
	 */
	private static StringBuilder dump(final StringBuilder _lbuffer,
			final long value) {
		for (int j = 0; j < 8; j++) {
			_lbuffer.append(_hexcodes[(int) (value >> _shifts[j]) & 15]);
		}
		return _lbuffer;
	}

	/**
	 * Dump a byte value into a StringBuilder.
	 * 
	 * @param _cbuffer
	 *            the StringBuilder to dump the value in
	 * @param value
	 *            the byte value to be dumped
	 * @return StringBuilder containing the dumped value.
	 */
	private static StringBuilder dump(final StringBuilder _cbuffer,
			final byte value) {
		for (int j = 0; j < 2; j++) {
			_cbuffer.append(_hexcodes[value >> _shifts[j + 6] & 15]);
		}
		return _cbuffer;
	}

}
