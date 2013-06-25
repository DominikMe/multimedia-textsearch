package edu.kit.iti.algo2.textindexing.searchengine;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.iti.algo2.textindexing.IIndex;
import edu.kit.iti.algo2.textindexing.searchengine.expr.Expr;
import edu.kit.iti.algo2.textindexing.searchengine.expr.MultiExpr;
import edu.kit.iti.algo2.textindexing.searchengine.expr.StrictWordExpr;

public class DefaultSearchEngine implements SearchEngine {
	private IIndex invertedIndex;
	private Map<StrictWordExpr, SearchResult> lookupCache;

	public DefaultSearchEngine(IIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	@Override
	public SearchResult find(Expr expr) {
		reset();
		List<StrictWordExpr> swes = new LinkedList<>();
		gatherLookups(expr, swes);

		// TODO intersect and union the lookups in the hashmap after expr
		return null;
	}

	private void reset() {
		lookupCache.clear();

	}

	private void gatherLookups(Expr expr, List<StrictWordExpr> swes) {
		if (expr instanceof StrictWordExpr) {
			StrictWordExpr strictWordExpr = (StrictWordExpr) expr;
			lookupCache.put(strictWordExpr,
					invertedIndex.lookup(strictWordExpr.getWord()));
			return;
		}

		if (expr instanceof MultiExpr) {
			MultiExpr me = (MultiExpr) expr;
			for (Expr e : me.child()) {
				gatherLookups(e, swes);
			}
		}
	}
}
