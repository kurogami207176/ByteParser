package com.alaindroid.parser.byteparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alaindroid.parser.byteparser.parser.ByteParser;
import com.alaindroid.parser.byteparser.parser.ByteParserEvaluator;

public class MultiByteParser implements ByteParser {
	List<UnitByteParser> bunch;

	public MultiByteParser() {
		bunch = new ArrayList<UnitByteParser>();
	}

	public MultiByteParser(String... loads) {
		this();
		add(loads);
	}

	public void add(String... loads) {
		for (String load : loads) {
			bunch.add(new UnitByteParser(load));
		}
	}

	public Map<String, Object> map(byte[] bytes) throws Exception {
		Map<String, Object> retVal = null;
		for (UnitByteParser parser : bunch) {
			try {
				if (retVal != null) {
					throw new Exception("Too many evaluations possible!");
				}
				retVal = parser.map(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (retVal == null) {
			throw new Exception("Unable to evaluate!");
		}
		return retVal;
	}

	public void printParseUnits() {
		int ctr = 0;
		for (UnitByteParser unit : bunch) {
			System.out.print("[ctr=" + ctr++ + "]");
			unit.printParseUnits();
		}
	}

	private List<ByteParserEvaluator> getEvaluators() {
		List<ByteParserEvaluator> retVal = new ArrayList<ByteParserEvaluator>();
		for (UnitByteParser b : bunch) {
			retVal.add(b.getEvaluator());
		}
		return retVal;
	}

	public ByteParserEvaluator getEvaluator() {
		return new MultiByteParserEvaluator(getEvaluators());
	}

}
