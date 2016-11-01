package com.alaindroid.parser.byteparser.parser.impl;

import java.util.Arrays;
import java.util.Map;

import com.alaindroid.parser.byteparser.parser.ParseResult;
import com.alaindroid.parser.byteparser.parser.ParseUnit;
import com.alaindroid.parser.byteparser.parser.ParseUnitEvaluator;

public class FixedParseUnit implements ParseUnit {

	String unit;
	byte[] unitBytes;
	int counter = 0;

	public FixedParseUnit(String unit) {
		this.unit = unit;
		this.unitBytes = unit.getBytes();
	}

	public boolean isValid(byte b) {
		byte bUnit = unitBytes[counter];
		if (bUnit == b) {
			counter++;
			return true;
		}
		counter = 0;
		return false;
	}

	@Override
	public String toString() {
		return "[" + unit + "]";
	}

	@Override
	public ParseResult validate(byte[] b, Map<String, Object> prop) {
		if (b != null && unitBytes != null && b.length >= unitBytes.length) {
			byte[] forEval = new byte[unitBytes.length];
			System.arraycopy(b, 0, forEval, 0, forEval.length);
			for (int i = 0; i < forEval.length; i++) {
				if (forEval[i] != unitBytes[i]) {
					return ParseResult.invalid(b);
				}
			}
			byte[] rest;
			if (b.length > unitBytes.length) {
				rest = new byte[b.length - unitBytes.length];
				System.arraycopy(b, forEval.length, rest, 0, rest.length);
			} else {
				rest = new byte[] {};
			}
			return new ParseResult(true, rest);

		}
		return ParseResult.invalid(b);
	}

	@Override
	public ParseUnitEvaluator evaluator() {
		return new FixedParseUnitEvaluator(unitBytes);
	}

	private static class FixedParseUnitEvaluator implements ParseUnitEvaluator {
		public FixedParseUnitEvaluator(byte[] unit) {
			this.unit = unit;
		}

		byte[] unit;
		int counter = 0;

		@Override
		public boolean isValid(byte b) {
			boolean valid = unit[counter] == b;
			if (valid) {
				counter++;
				return valid;
			}
			return false;
		}

		@Override
		public boolean isTerminated(byte b) {
			return counter >= unit.length;
		}

		@Override
		public byte[] currentValue() {
			return Arrays.copyOf(unit, counter);
		}

	}

}
