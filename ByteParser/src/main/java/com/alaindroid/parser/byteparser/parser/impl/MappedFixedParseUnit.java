package com.alaindroid.parser.byteparser.parser.impl;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Map;

import com.alaindroid.parser.byteparser.enums.UnitType;
import com.alaindroid.parser.byteparser.parser.Mappable;
import com.alaindroid.parser.byteparser.parser.MappableParseResult;
import com.alaindroid.parser.byteparser.parser.Mapped;
import com.alaindroid.parser.byteparser.parser.ParseResult;
import com.alaindroid.parser.byteparser.parser.ParseUnit;
import com.alaindroid.parser.byteparser.parser.ParseUnitEvaluator;

public class MappedFixedParseUnit implements ParseUnit, Mapped {

	String name;

	String unit;
	byte[] unitBytes;
	ByteArrayOutputStream baos;

	// Unit type is always fixed
	public MappedFixedParseUnit(String name, String unit) {
		this.name = name;
		this.unit = unit;
		baos = new ByteArrayOutputStream();
	}

	@Override
	public String toString() {
		return "(" + name + "|F" + unit + ")";
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
	public String getName() {
		return name;
	}

	@Override
	public ParseUnitEvaluator evaluator() {
		return new MappedFixedParseUnitEvaluator(name, unitBytes);
	}

	private static class MappedFixedParseUnitEvaluator implements ParseUnitEvaluator, Mappable {
		public MappedFixedParseUnitEvaluator(String name, byte[] unit) {
			this.unit = unit;
			this.name = name;
		}

		String name;
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

		@Override
		public String getName() {
			return name;
		}

		@Override
		public byte[] getValue() {
			return currentValue();
		}

		@Override
		public Object getParsedValue() {
			return UnitType.FIXED.transform(getValue());
		}

	}

}
