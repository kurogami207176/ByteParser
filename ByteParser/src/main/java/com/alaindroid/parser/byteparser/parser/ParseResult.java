package com.alaindroid.parser.byteparser.parser;

import com.alaindroid.parser.byteparser.enums.UnitType;

public class ParseResult {

	private boolean valid;
	private byte[] remainder;

	public ParseResult(boolean valid, byte[] remainder) {
		this.valid = valid;
		this.remainder = remainder;
	}

	public boolean isValid() {
		return valid;
	}

	public byte[] getRemainder() {
		return remainder;
	}

	public static ParseResult invalid(byte[] b) {
		return new ParseResult(false, b);
	}
}
