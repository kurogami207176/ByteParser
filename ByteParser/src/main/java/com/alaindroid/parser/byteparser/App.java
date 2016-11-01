package com.alaindroid.parser.byteparser;

import java.util.Map;

import com.alaindroid.parser.byteparser.parser.ByteParser;
import com.alaindroid.parser.byteparser.parser.ByteParserEvaluator;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		String loadable = "#RSP,%string|IMEI%,%integer|SOMETHING%$";
		String loadable2 = "#RSP,%string|IMEI%,%integer|SOMETHING%";
		String toEvaluate = "#RSP,0011232,123";
		System.out.println(toEvaluate.length());
		System.out.println(toEvaluate.getBytes().length);

		//ByteParser parser = new UnitByteParser(loadable);
		// ByteParser parser = new UnitByteParser(loadable2);
		ByteParser parser = new MultiByteParser(loadable, loadable2);
		// byte array
		parser.printParseUnits();
		Map<String, Object> map;
		try {
			map = parser.map(toEvaluate.getBytes());
			System.out.println("Parser.map");
			for (String key : map.keySet()) {
				Object value = map.get(key);
				System.out.println("key=" + key);
				System.out.println("value=" + (value == null ? "NULL" : value.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// byte by byte
		ByteParserEvaluator evaluator = parser.getEvaluator();
		for (byte b : toEvaluate.getBytes()) {
			boolean isValid = evaluator.isValid(b);
		}
		System.out.println("ParserEvaluator.map");
		try {
			map = evaluator.map();
			System.out.println("Parser.map");
			for (String key : map.keySet()) {
				Object value = map.get(key);
				System.out.println("key=" + key);
				System.out.println("value=" + (value == null ? "NULL" : value.toString()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
