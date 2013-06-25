package edu.kit.iti.algo2.textindexing.searchengine;

import edu.kit.iti.algo2.textindexing.searchengine.expr.Expr;

public interface SearchEngine {
	public SearchResult find(Expr expr);
}
