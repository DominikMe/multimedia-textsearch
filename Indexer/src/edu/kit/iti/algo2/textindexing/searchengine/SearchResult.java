package edu.kit.iti.algo2.textindexing.searchengine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;

/**
 * 
 * @author weigla
 * 
 */
public class SearchResult {
    public Set<SearchResultEntry> result = new TreeSet<>();

    public boolean isEmpty() {
    	return result.isEmpty();
    }
    
    public void add(Occurrence occurrence, String... word) {
	result.add(new SearchResultEntry(occurrence, word));
    }

    public void add(SearchResultEntry sre) {
	result.add(sre);
    }

    public void add(Occurrence o, Set<String> mw) {
	final SearchResultEntry sre = new SearchResultEntry(o);
	sre.setMatchedWords(mw);
	add(sre);
    }

    public SearchResult intersect(SearchResult other) {
	SearchResult sr = new SearchResult();

	if (result.size() == 0 || other.result.size() == 0)
	    return new SearchResult();

	Iterator<SearchResultEntry> a = other.result.iterator();
	Iterator<SearchResultEntry> b = result.iterator();

	SearchResultEntry currentA = a.next();
	SearchResultEntry currentB = b.next();

	do {
	    int c = currentA.compareTo(currentB);
	    if (c == 0) {
		Occurrence o = new Occurrence(currentA.getOccurrence());
		o.setCount(currentA.getOccurrence().getCount()
			+ currentB.getOccurrence().getCount());
		Set<String> mw = new HashSet<>();
		mw.addAll(currentA.getMatchedWords());
		mw.addAll(currentB.getMatchedWords());
		sr.add(o, mw);

		if (!a.hasNext() && !b.hasNext())
		    break;

		currentA = a.next();
		currentB = b.next();
	    }

	    if (c < 0) {
		if (!a.hasNext())
		    break;
		currentA = a.next();
	    }

	    if (c > 0) {
		if (!b.hasNext())
		    break;
		currentB = b.next();
	    }
	} while (true);
	return sr;
    }

    @Override
    public String toString() {
	return "SearchResult [result=" + result + "]";
    }

    public SearchResult union(SearchResult other) {
	SearchResult sr = new SearchResult();
	sr.result.addAll(result);
	sr.result.addAll(other.result);
	return sr;
    }

    public int size() {
	return 0;
    }

}
