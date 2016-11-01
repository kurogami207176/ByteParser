package com.alaindroid.parser.byteparser.parser;

import java.util.Map;

public interface ByteParser {
	public Map<String, Object> map(byte[] bytes) throws Exception;

	public void printParseUnits();

	public ByteParserEvaluator getEvaluator();

}
