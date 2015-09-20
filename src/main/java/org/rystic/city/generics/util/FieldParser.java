package org.rystic.city.generics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class FieldParser {
	public static boolean parseBoolean(String value_) {
		try {
			return Boolean.parseBoolean(value_);
		} catch (Exception e_) {
			e_.printStackTrace();
		}
		return DEFAULT_BOOL_VALUE;
	}

	public static byte parseByte(String value_) {
		try {
			return Byte.parseByte(value_);
		} catch (Exception e_) {
			e_.printStackTrace();
		}
		return DEFAULT_BYTE_VALUE;
	}

	public static int parseInt(String value_) {
		try {
			return Integer.parseInt(value_);
		} catch (Exception e_) {
			e_.printStackTrace();
		}
		return DEFAULT_INTEGER_VALUE;
	}

	public static double parseDouble(String value_) {
		try {
			return Double.parseDouble(value_);
		} catch (Exception e_) {
			e_.printStackTrace();
		}
		return DEFAULT_DOUBLE_VALUE;
	}

	public static ByteBuffer parseTexture(String value_) {
		try {
			InputStream in = new FileInputStream(new File("src/main/art/" + value_));
			PNGDecoder decoder = new PNGDecoder(in);

			ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			buf.order(ByteOrder.nativeOrder());
			
			return buf;
		} catch (Exception e_) {
			e_.printStackTrace();
		}

		return null;
	}

	private static final boolean DEFAULT_BOOL_VALUE = false;
	private static final byte DEFAULT_BYTE_VALUE = 0;
	private static final int DEFAULT_INTEGER_VALUE = 0;
	private static final double DEFAULT_DOUBLE_VALUE = 0D;
}
