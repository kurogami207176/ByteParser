package com.alaindroid.parser.byteparser.parser.impl;

import java.io.ByteArrayOutputStream;
import java.security.InvalidParameterException;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.alaindroid.parser.byteparser.enums.UnitType;
import com.alaindroid.parser.byteparser.parser.Mappable;
import com.alaindroid.parser.byteparser.parser.MappableParseResult;
import com.alaindroid.parser.byteparser.parser.Mapped;
import com.alaindroid.parser.byteparser.parser.ParseResult;
import com.alaindroid.parser.byteparser.parser.ParseUnit;
import com.alaindroid.parser.byteparser.parser.ParseUnitEvaluator;
import com.alaindroid.parser.byteparser.util.Util;

public class DynamicLengthMappedParseUnit implements ParseUnit, Mapped {
	String name;
	String lenName;
	UnitType type;
	ByteArrayOutputStream baos;

	public DynamicLengthMappedParseUnit(String name, UnitType type, String lenName) {
		this.name = name;
		this.lenName = lenName;
		this.type = type;
		baos = new ByteArrayOutputStream();
	}

	@Override
	public String toString() {
		return "(" + name + "|L" + lenName + "|" + type.name() + ")";
	}

	@Override
	public ParseResult validate(byte[] b, Map<String, Object> prop) {
		Object o = prop.get(lenName);
		int len = -1;

		if (!(o instanceof Integer)) {
			throw new InvalidParameterException("Invalid length value " + (o == null ? "null" : o.toString() + ")"));
		}

		len = (Integer) o;
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
		return new DynamicLengthMappedParseUnitEvaluator(name, type, lenName);
	}

	public static class DynamicLengthMappedParseUnitEvaluator implements ParseUnitEvaluator, Mappable {
		public DynamicLengthMappedParseUnitEvaluator(String name, UnitType type, String lenName) {
			this.name = name;
			this.lenName = lenName;
		}

		String name;
		String lenName;
		UnitType type;
		int len = -1;
		int counter = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		public void setLen(Map<String, byte[]> prop) {
			byte[] bytes = prop.get(lenName);
			try {
				len = Integer.parseInt(new String(bytes));
			} catch (Exception e) {
				throw new InvalidParameterException(
						"Invalid length value " + (bytes == null ? "null" : Util.printBytes(bytes)));

			}

		}

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
