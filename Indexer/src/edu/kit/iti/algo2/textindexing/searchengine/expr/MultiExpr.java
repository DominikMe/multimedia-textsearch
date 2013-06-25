package edu.kit.iti.algo2.textindexing.searchengine.expr;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultiExpr implements Expr {
	private Operation op = null;
	private List<Expr> subexpr = new LinkedList<>();

	public boolean add(Expr e) {
		return subexpr.add(e);
	}

	public boolean remove(Object o) {
		return subexpr.remove(o);
	}

	public Expr remove(int index) {
		return subexpr.remove(index);
	}

	@Override
	public String toString() {
		return "MultiExpr [op=" + getOperation() + ", subexpr=" + subexpr + "]";
	}

	public Operation getOperation() {
		return op;
	}

	public void setOperation(Operation op) {
		this.op = op;
	}

	public Collection<Expr> child() {
		return Collections.unmodifiableCollection(subexpr);
	}
}