package com.alaindroid.parser.byteparser.parser;

import java.util.Map;

public interface ByteParserEvaluator {
	public boolean isTerminated();

	public boolean isValid(byte b);

	public Map<String, Object> map() throws Exception;

}
