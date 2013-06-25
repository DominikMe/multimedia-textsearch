package edu.kit.iti.algo2.textindexing.searchengine.expr;

public class StrictWordExpr implements Expr {
	private String word;

	public StrictWordExpr(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public String toString() {
		return "StrictWordExpr [word=" + word + "]";
	}

}