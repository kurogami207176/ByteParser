package com.alaindroid.parser.byteparser.parser;

import com.alaindroid.parser.byteparser.enums.UnitType;

public class MappableParseResult extends ParseResult implements Mappable {
	String name;
	byte[] value;
	UnitType type;

	public MappableParseResult(boolean valid, byte[] remainder, UnitType type, String name, byte[] value) {
		super(valid, remainder);
		this.name = name;
		this.value = value;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public Object getParsedValue() {
		return type.transform(getValue());
	}

}
