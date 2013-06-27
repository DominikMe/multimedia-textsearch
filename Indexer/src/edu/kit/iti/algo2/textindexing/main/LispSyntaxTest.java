package edu.kit.iti.algo2.textindexing.main;

import edu.kit.iti.algo2.textindexing.searchengine.expr.LispSyntaxParser;

public class LispSyntaxTest {
	public static void main(String[] args) {
		String query1 = "(and 'abc' 'def' 'ghi')";
		String query2 = "(or 'abc' 'def' (and 'ghi' 'ijk'))";
		System.out.println(new LispSyntaxParser(query1).parse());
		System.out.println(new LispSyntaxParser(query2).parse());
	}
}
