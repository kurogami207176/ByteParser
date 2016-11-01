package com.alaindroid.parser.byteparser.enums;

import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.bind.DatatypeConverter;

public enum UnitType {
	FIXED(p -> true, f -> new String(f)), // fixed
	STRING(p -> true, f -> new String(f)), // any string
	LETTER(p -> (p.byteValue() >= 'a' && p.byteValue() <= 'z') || (p.byteValue() >= 'A' && p.byteValue() <= 'Z'),
			f -> new String(f)), // a-zA-Z
	HEX(p -> (p.byteValue() >= '0' && p.byteValue() <= '9') || (p.byteValue() >= 'A' && p.byteValue() <= 'F')
			|| ((p.byteValue() >= 'a' && p.byteValue() <= 'f')), f -> DatatypeConverter.printHexBinary(f)), // 0-9a-zA-Z
	INTEGER(p -> p.byteValue() >= '0' && p.byteValue() <= '9', f -> Integer.parseInt(new String(f))), // number
	FLOAT(p -> (p.byteValue() >= '0' && p.byteValue() <= '9') || p.byteValue() == '.', f -> Double.parseDouble(new String(f))), // number
	BYTE(p -> true, f -> f); // byte stream

	Predicate<Byte> p;
	Function<byte[], Object> f;

	private UnitType(Predicate<Byte> p, Function<byte[], Object> f) {
		this.p = p;
		this.f = f;
	}

	public boolean isValid(byte b) {
		return p.test(b);
	}

	public Object transform(byte[] bytes) {
		return f.apply(bytes);
	}

}
