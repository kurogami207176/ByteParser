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

public class TerminatedMappedParseUnit implements ParseUnit, Mapped {
	String name;
	byte terminator;
	UnitType type;

	public TerminatedMappedParseUnit(String name, UnitType type, byte terminator) {
		this.name = name;
		this.terminator = terminator;
		this.type = type;
	}

	public boolean isTerminated(byte b) {
		boolean terminated = terminator == b;
		return terminated;
	}

	@Override
	public String toString() {
		return "(" + name + "|T" + (char) terminator + "|" + type.name() + ")";
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return name.equals(o);
	}

	@Override
	public ParseResult validate(byte[] b, Map<String, Object> prop) {
		int terminatorIndex = -1;
		for (int i = 0; i < b.length; i++) {
			if (b[i] == terminator) {
				terminatorIndex = i;
				break;
			}
		}
		if (terminatorIndex > 0) {
			byte[] value = new byte[terminatorIndex];
			System.arraycopy(b, 0, value, 0, terminatorIndex);
			for (byte v : value) {
				if (!type.isValid(v)) {
					return ParseResult.invalid(b);
				}
			}
			int restLen = b.length - terminatorIndex;
			if (restLen > 0) {
				byte[] rest = new byte[restLen];
				if (terminatorIndex < b.length) {
					System.arraycopy(b, terminatorIndex, rest, 0, rest.length);
				}
				return new MappableParseResult(true, rest, type, name, value);
			} else {
				return new MappableParseResult(true, new byte[] {}, type, name, value);
			}
		}
		return ParseResult.invalid(b);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParseUnitEvaluator evaluator() {
		return new TerminatedMappedParseUnitEvaluator(name, type, terminator);
	}

	private static class TerminatedMappedParseUnitEvaluator implements ParseUnitEvaluator, Mappable {
		public TerminatedMappedParseUnitEvaluator(String name, UnitType type, byte terminator) {
			this.terminator = terminator;
			this.name = name;
			this.type = type;
		}

		String name;
		int terminator;
		UnitType type;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		@Override
		public boolean isValid(byte b) {
			baos.write(b);
			return true;
		}

		@Override
		public boolean isTerminated(byte b) {
			return b == terminator;
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

	}

}
