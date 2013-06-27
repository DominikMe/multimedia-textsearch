package edu.kit.iti.algo2.textindexing.searchengine;

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
	public Set<Occurrence> result = new TreeSet<>(new DocIdComparator());

	public void add(Occurrence occurrence) {
		result.add(occurrence);
	}

	public SearchResult intersect(SearchResult other) {
		SearchResult sr = new SearchResult();

		if (result.size() == 0 || other.result.size() == 0)
			return new SearchResult();

		Iterator<Occurrence> a = other.result.iterator();
		Iterator<Occurrence> b = result.iterator();

		Occurrence currentA = a.next();
		Occurrence currentB = b.next();

		DocIdComparator comp = new DocIdComparator();

		do {
			int c = comp.compare(currentA, currentB);
			if (c == 0) {
				Occurrence o = new Occurrence(currentA.getDocID());
				o.setCount(currentA.getCount() + currentB.getCount());
				sr.add(o);

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
}
