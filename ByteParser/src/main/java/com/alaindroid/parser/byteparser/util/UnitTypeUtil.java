package com.alaindroid.parser.byteparser.util;

import com.alaindroid.parser.byteparser.enums.UnitType;
import com.alaindroid.parser.byteparser.parser.impl.MappedUnitType;
import com.alaindroid.parser.byteparser.parser.impl.ParamMappedUnitType;

public class UnitTypeUtil {
	// ACCEPTS type or type.PARAM
	public static MappedUnitType getUnitType(String name) {
		for (UnitType type : UnitType.values()) {
			String tmp = type.name() + ".";
			if (type.name().equalsIgnoreCase(name)) {
				return new MappedUnitType(type);
			} else if (name.toUpperCase().indexOf(tmp) == 0) {
				String param = name.substring(tmp.length());
				return new ParamMappedUnitType(type, param);
			}
		}
		return null;
	}

}
