package com.alaindroid.parser.byteparser.parser;

import java.util.Map;

public interface ParseUnit {
	/**
	 * 
	 * @param b
	 * @return remaining bytes to be passed to next validator
	 */
	public ParseResult validate(byte[] b, Map<String, Object> prop);

	/**
	 * Gets a unique evaluator for a parse unit
	 * 
	 * @return
	 */
	public ParseUnitEvaluator evaluator();

}
