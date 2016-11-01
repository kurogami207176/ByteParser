package com.alaindroid.parser.byteparser;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alaindroid.parser.byteparser.parser.Mappable;
import com.alaindroid.parser.byteparser.parser.ParseUnitEvaluator;
import com.alaindroid.parser.byteparser.util.Util;

public class ByteParserEvaluator {

	List<ParseUnitEvaluator> evaluators;
	int ctr = 0;
	ParseUnitEvaluator currentEvaluator;

	protected ByteParserEvaluator(List<ParseUnitEvaluator> evaluators) {
		this.evaluators = evaluators;
		this.ctr = 0;
		updateEvaluator();
	}

	private void updateEvaluator() {
		currentEvaluator = evaluators.get(ctr);
	}

	public boolean isValid(byte b) {
		if (currentEvaluator.isTerminated(b)) {
			ctr++;
			updateEvaluator();
		}
		return currentEvaluator.isValid(b);
	}

	public Map<String, Object> map() throws Exception {
		Map<String, Object> valueMap = new TreeMap<String, Object>();
		for (ParseUnitEvaluator parseUnitEvaluator : evaluators) {
			if (!parseUnitEvaluator.isTerminated((byte) 0)) {
				throw new Exception("Incomplete evaluation " + Util.printBytes(parseUnitEvaluator.currentValue()));
			}
			if (parseUnitEvaluator instanceof Mappable) {
				Mappable mappable = (Mappable) parseUnitEvaluator;
				String name = mappable.getName();
				Object value = mappable.getParsedValue();
				System.out.println("(" + name + "," + value.toString() + ")");
				valueMap.put(name, value);
			}
		}
		return valueMap;
	}

}
