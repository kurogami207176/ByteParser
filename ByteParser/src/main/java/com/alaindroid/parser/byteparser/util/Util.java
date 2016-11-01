package com.alaindroid.parser.byteparser.util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.DatatypeConverter;

import com.alaindroid.parser.byteparser.enums.UnitType;
import com.alaindroid.parser.byteparser.parser.Mapped;
import com.alaindroid.parser.byteparser.parser.Parametered;
import com.alaindroid.parser.byteparser.parser.ParseUnit;
import com.alaindroid.parser.byteparser.parser.impl.DynamicLengthMappedParseUnit;
import com.alaindroid.parser.byteparser.parser.impl.FixedLengthMappedParseUnit;
import com.alaindroid.parser.byteparser.parser.impl.FixedParseUnit;
import com.alaindroid.parser.byteparser.parser.impl.MappedFixedParseUnit;
import com.alaindroid.parser.byteparser.parser.impl.MappedUnitType;
import com.alaindroid.parser.byteparser.parser.impl.TerminatedMappedParseUnit;

public class Util {

	public static List<ParseUnit> load(String load) {
		int specIndex = load.indexOf('%');
		int pipeIndex = load.indexOf('|');
		int endSpecIndex = pipeIndex > 0 ? load.indexOf('%', pipeIndex) : -1;
		List<ParseUnit> retVal = new ArrayList<ParseUnit>();
		// mappable
		if (specIndex == 0 && pipeIndex > specIndex && endSpecIndex > pipeIndex) {
			String typeString = load.substring(specIndex + 1, pipeIndex);
			MappedUnitType type = UnitTypeUtil.getUnitType(typeString);
			if (type == null) {
				throw new InvalidParameterException("Invalid type: " + typeString + " from " + load);
			}
			String nameString = load.substring(pipeIndex + 1, endSpecIndex);
			String rest = load.substring(endSpecIndex + 1);
			if (type instanceof Parametered) {
				Parametered paramType = (Parametered) type;
				String parameter = paramType.getParameter();
				try {
					int len = Integer.parseInt(parameter);
					ParseUnit unit = new FixedLengthMappedParseUnit(nameString, type.getType(), len);
					retVal.add(unit);
				} catch (NumberFormatException nfe) {
					UnitType ty = type.getType();
					if (ty == UnitType.FIXED) {
						ParseUnit unit = new MappedFixedParseUnit(nameString, parameter);
						retVal.add(unit);
					} else {
						ParseUnit unit = new DynamicLengthMappedParseUnit(nameString, ty, parameter);
						retVal.add(unit);
					}
				}
				if (rest != null && rest.length() > 0) {
					List<ParseUnit> restUnits = load(rest);
					if (restUnits != null) {
						retVal.addAll(restUnits);
					}
				}
			} else {
				byte terminator;
				if (rest != null && rest.length() > 0) {
					terminator = rest.getBytes()[0];
					ParseUnit unit = new TerminatedMappedParseUnit(nameString, type.getType(), terminator);
					retVal.add(unit);
					List<ParseUnit> restUnits = load(rest);
					if (restUnits != null) {
						retVal.addAll(restUnits);
					}
				} else {
					ParseUnit unit = new TerminatedMappedParseUnit(nameString, type.getType(), (byte) 0);
					retVal.add(unit);
				}
			}
		}
		// has mappable next
		else if (specIndex > 0 && pipeIndex > specIndex && endSpecIndex > pipeIndex)

		{
			String fixedString = load.substring(0, specIndex);
			String rest = load.substring(specIndex);
			ParseUnit unit = new FixedParseUnit(fixedString);
			retVal.add(unit);
			List<ParseUnit> restUnits = load(rest);
			if (restUnits != null) {
				retVal.addAll(restUnits);
			}
		} else {
			ParseUnit unit = new FixedParseUnit(load);
			retVal.add(unit);
		}
		return retVal;
	}

	public static Set<String> getKeys(List<ParseUnit> parseUnits) {
		Set<String> name = new TreeSet<String>();
		for (ParseUnit unit : parseUnits) {
			if (unit instanceof Mapped) {
				Mapped mapped = (Mapped) unit;
				String key = mapped.getName();
				if (name.contains(key)) {
					throw new InvalidParameterException("Duplicate key: " + key);
				}
				name.add(key);
			}
		}
		return name;
	}

	public static String printBytes(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes) + "(" + new String(bytes) + ")";
	}

}
