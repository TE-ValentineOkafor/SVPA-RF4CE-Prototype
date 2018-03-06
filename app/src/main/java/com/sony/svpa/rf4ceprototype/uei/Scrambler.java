/* 
 *
 *
 * Copyright (C) 1999-2011 Universal Electronics Inc.
 *
 * 
 */
package com.sony.svpa.rf4ceprototype.uei;

/**
 * The Class Scrambler.
 */
public class Scrambler {

	/** The Constant Key. */
	public static final byte[] _ScramblerKey = new byte[] {
			(byte)0x01, (byte)0x37, (byte)0x39, (byte)0x35, (byte)0x34, (byte)0x6E,
			(byte)0x64, (byte)0x73, (byte)0x66, (byte)0x6A, (byte)0x6B, (byte)0x68,
			(byte)0x61, (byte)0x6B, (byte)0x6C, (byte)0x66, (byte)0x64, (byte)0x6A,
			(byte)0x6B, (byte)0x6C, (byte)0x34, (byte)0x65, (byte)0x37, (byte)0x39,
			(byte)0x38, (byte)0x09, (byte)0x34, (byte)0x20, (byte)0x2E, (byte)0x22,
			(byte)0x7E, (byte)0x31, (byte)0x35, (byte)0x34, (byte)0x66, (byte)0x0A,
			(byte)0x6A, (byte)0x68, (byte)0x66, (byte)0x6B, (byte)0x68, (byte)0x67,
			(byte)0x38, (byte)0x37, (byte)0x34, (byte)0x39, (byte)0x38, (byte)0x68,
			(byte)0x74, (byte)0x51, (byte)0x51, (byte)0x34, (byte)0x32, (byte)0x32,
			(byte)0x39, (byte)0x33, };

	/** The Constant Keylen. */
	private static final int KEY_LEN = _ScramblerKey.length;

	/**
	 * Encrypt/Decrypt byte array
	 *
	 * @param buf
	 *            the buf
	 * @return the byte[]
	 */
	public static byte[] vencr(byte[] buf) {
		byte[] out = buf;
		if (buf != null) {
			out = new byte[buf.length];
			int enCounter = KEY_LEN / 3;
			int buf_ctr = 0;
			int length = buf.length;
			while (buf_ctr < length) {
				out[buf_ctr] = (byte) ((buf[buf_ctr]) ^ ((byte) _ScramblerKey[(enCounter++) % KEY_LEN]));
				++buf_ctr;
			}
		}
		return out;
	}
}
