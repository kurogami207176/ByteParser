package com.alaindroid.parser.byteparser.parser.impl;

import com.alaindroid.parser.byteparser.enums.UnitType;
import com.alaindroid.parser.byteparser.parser.Parametered;

public class ParamMappedUnitType extends MappedUnitType implements Parametered {
	String parameter;

	public ParamMappedUnitType(UnitType type, String param) {
		super(type);
		this.parameter = param;
	}

	@Override
	public String getParameter() {
		return parameter;
	}

}
