package edu.kit.iti.algo2.textindexing.searchengine.expr;


public class LispSyntaxParser {
	private static final char LPAREN = '(';
	private static final char RPAREN = ')';
	private static final char WORD = '\'';

	int pos = -1;
	private String input;
	private char current;

	public LispSyntaxParser(String query) {
		input = query.trim();
		consume();
		if (!match(LPAREN) && !match(WORD)) {
			throw new IllegalArgumentException(
					"query should start with LPAREN or WORD");
		}

	}

	public Expr parse() {
		while (pos < input.length()) {

			if (match(' ')) {
				consume();
			}

			if (match(LPAREN)) {
				return parseParenLeft();
			}

			if (match(WORD)) {
				return parseWord();
			}
			consume();
		}
		return null;
	}

	private Expr parseWord() {
		if (!match(WORD))
			throw new IllegalStateException("expected: WORD (" + WORD
					+ ") got:" + current);
		consume();
		String w = consumeUntil(WORD);

		if (w.contains("*")) {
			return new FuzzyWordExpr(w);
		} else {
			return new StrictWordExpr(w);
		}
	}

	private Expr parseParenLeft() {
		if (!match(LPAREN))
			throw new IllegalStateException();
		consume();
		String op = consumeUntil(' ');

		MultiExpr me = new MultiExpr();
		me.setOperation(Operation.valueOf(op.toUpperCase()));

		while (!match(RPAREN)) {
			me.add(parse());
		}
		consume(); // RPAREN
		return me;
	}

	private String consumeUntil(char c) {
		int p = input.indexOf(c, pos);
		String s = input.substring(pos, p);
		pos = p;
		consume();
		return s;
	}

	private void consume() {
		pos++;
		if (pos < input.length())
			current = input.charAt(pos);
	}

	private boolean match(char c) {
		boolean b = current == c;
		return b;
	}

	public static void main(String[] args) {
		String query1 = "(and 'abc' 'def' 'ghi')";
		String query2 = "(or 'abc' 'def' (or 'ghi' 'ijk'))";

		System.out.println(new LispSyntaxParser(query1).parse());
		System.out.println(new LispSyntaxParser(query2).parse());
	}
}