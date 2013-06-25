package edu.kit.iti.algo2.textindexing.searchengine.expr;

public class SearchExprFactory {
	public static Expr parseLispSyntax(String query) {
		return new LispSyntaxParser(query).parse();
	}

	public static MultiExpr and(Expr... e) {
		MultiExpr me = new MultiExpr();
		me.setOperation(Operation.AND);
		for (Expr expr : e) {
			me.add(expr);
		}
		return me;
	}

	public static MultiExpr or(Expr... e) {
		MultiExpr me = new MultiExpr();
		me.setOperation(Operation.OR);
		for (Expr expr : e) {
			me.add(expr);
		}
		return me;
	}

	public static StrictWordExpr word(String word) {
		return new StrictWordExpr(word);
	}

	public static StrictWordExpr pattern(String pattern) {
		return new FuzzyWordExpr(pattern);
	}
}
