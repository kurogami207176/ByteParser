package com.alaindroid.parser.byteparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

import com.alaindroid.parser.byteparser.parser.ByteParser;
import com.alaindroid.parser.byteparser.parser.ByteParserEvaluator;
import com.alaindroid.parser.byteparser.parser.Mappable;
import com.alaindroid.parser.byteparser.parser.ParseResult;
import com.alaindroid.parser.byteparser.parser.ParseUnit;
import com.alaindroid.parser.byteparser.parser.ParseUnitEvaluator;
import com.alaindroid.parser.byteparser.util.Util;

public class UnitByteParser implements ByteParser {
	// Sample inputs
	// #RSP,%string|IMEI%,%integer|SOMETHING%$

	private String loaded;
	private List<ParseUnit> parseUnits;
	private Set<String> keys;

	public UnitByteParser(String loaded) {
		this.loaded = loaded;
		parseUnits = Util.load(this.loaded);
		keys = Util.getKeys(parseUnits);
	}

	public Map<String, Object> map(byte[] bytes) throws Exception {
		Map<String, Object> valueMap = new TreeMap<String, Object>();
		for (String key : keys) {
			valueMap.put(key, null);
		}
		int len = parseUnits.size();
		byte[] forEval = bytes;
		for (int i = 0; i < len; i++) {
			if (forEval == null || forEval.length == 0) {
				throw new Exception("bytes too short");
			}
			ParseUnit unit = parseUnits.get(i);
			System.out
					.println("forEval=" + DatatypeConverter.printHexBinary(forEval) + "(" + new String(forEval) + ")");
			System.out.println(" evaluator=" + unit.getClass().getCanonicalName());
			ParseResult result = unit.validate(forEval, valueMap);
			if (!result.isValid()) {
				throw new Exception(
						"Invalid bytes " + DatatypeConverter.printHexBinary(forEval) + "(" + new String(forEval) + ")");
			}
			if (result instanceof Mappable) {
				Mappable mappable = (Mappable) result;
				String name = mappable.getName();
				Object value = mappable.getParsedValue();
				System.out.println("(" + name + "," + value.toString() + ")");
				valueMap.put(name, value);
			}
			forEval = result.getRemainder();
		}
		if (forEval != null && forEval.length > 0) {
			throw new Exception("Could not map " + DatatypeConverter.printHexBinary(forEval));
		}
		return valueMap;
	}

	public ByteParserEvaluator getEvaluator() {
		return new UnitByteParserEvaluator(getEvaluators());
	}

	private List<ParseUnitEvaluator> getEvaluators() {
		List<ParseUnitEvaluator> retVal = new ArrayList<ParseUnitEvaluator>();
		for (ParseUnit unit : parseUnits) {
			retVal.add(unit.evaluator());
		}
		return retVal;
	}

	public void printParseUnits() {
		for (ParseUnit unit : parseUnits) {
			System.out.println(unit);
		}
	}

}
