package edu.kit.iti.algo2.textindexing;

import edu.kit.iti.algo2.textindexing.searchengine.SearchResult;

public interface IIndex {
	public SearchResult lookup(String word);
}
