package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.util.List;

public interface Searchable {
	public List<Occurrence> searchConjunctively(String... keywords);
}
