package com.alaindroid.parser.byteparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alaindroid.parser.byteparser.parser.ByteParserEvaluator;

public class MultiByteParserEvaluator implements ByteParserEvaluator {

	List<ByteParserEvaluator> evaluators;

	protected MultiByteParserEvaluator(List<ByteParserEvaluator> evaluators) {
		this.evaluators = evaluators;
	}

	public boolean isValid(byte b) {
		boolean isValid = false;
		List<ByteParserEvaluator> forRemoval = new ArrayList<ByteParserEvaluator>();
		for (ByteParserEvaluator eval : evaluators) {
			if (eval.isValid(b)) {
				isValid = true;
			} else {
				forRemoval.add(eval);
			}
		}
		evaluators.removeAll(forRemoval);
		return isValid;
	}

	public Map<String, Object> map() throws Exception {
		if (evaluators.size() != 1) {
			throw new Exception("Too many evaluations possible!");
		}
		return evaluators.get(0).map();
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

}
