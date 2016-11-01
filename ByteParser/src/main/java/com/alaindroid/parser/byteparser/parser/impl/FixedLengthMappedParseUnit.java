package com.alaindroid.parser.byteparser.parser.impl;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import com.alaindroid.parser.byteparser.enums.UnitType;
import com.alaindroid.parser.byteparser.parser.Mappable;
import com.alaindroid.parser.byteparser.parser.MappableParseResult;
import com.alaindroid.parser.byteparser.parser.Mapped;
import com.alaindroid.parser.byteparser.parser.ParseResult;
import com.alaindroid.parser.byteparser.parser.ParseUnit;
import com.alaindroid.parser.byteparser.parser.ParseUnitEvaluator;

public class FixedLengthMappedParseUnit implements ParseUnit, Mapped {
	String name;
	int len;
	UnitType type;
	ByteArrayOutputStream baos;

	public FixedLengthMappedParseUnit(String name, UnitType type, int len) {
		this.name = name;
		this.len = len;
		this.type = type;
		baos = new ByteArrayOutputStream();
	}

	@Override
	public String toString() {
		return "(" + name + "|L" + len + "|" + type.name() + ")";
	}

	@Override
	public ParseResult validate(byte[] b, Map<String, Object> prop) {
		if (b != null && b.length >= len) {
			byte[] value = new byte[len];
			System.arraycopy(b, 0, value, 0, value.length);
			for (byte v : value) {
				if (!type.isValid(v)) {
					return ParseResult.invalid(b);
				}
			}
			byte[] rest;
			if (b.length > len) {
				rest = new byte[b.length - len];
				System.arraycopy(b, len, rest, 0, rest.length);
			} else {
				rest = new byte[] {};
			}
			return new MappableParseResult(true, rest, type, name, value);

		}
		return ParseResult.invalid(b);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParseUnitEvaluator evaluator() {
		return new FixedLengthMappedParseUnitEvaluator(name, type, len);
	}

	private static class FixedLengthMappedParseUnitEvaluator implements ParseUnitEvaluator, Mappable {
		public FixedLengthMappedParseUnitEvaluator(String name, UnitType type, int len) {
			this.len = len;
			this.name = name;
			this.type = type;
		}

		String name;
		int len;
		int counter = 0;
		UnitType type;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		@Override
		public boolean isValid(byte b) {
			counter++;
			baos.write(b);
			return true;
		}

		@Override
		public boolean isTerminated(byte b) {
			return counter >= len;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public byte[] getValue() {
			return baos.toByteArray();
		}

		@Override
		public Object getParsedValue() {
			return type.transform(getValue());
		}

		@Override
		public byte[] currentValue() {
			return baos.toByteArray();
		}

	}

}
