package com.alaindroid.parser.byteparser.parser;

public interface ParseUnitEvaluator {
	public boolean isValid(byte b);

	public boolean isTerminated(byte b);
}
