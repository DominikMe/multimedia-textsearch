package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.kit.iti.algo2.textindexing.IIndex;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResult;

public class InvertedIndex implements Searchable, Serializable, IIndex {
	private static final long serialVersionUID = 2523937211988280815L;
	private Map<String, List<Occurrence>> index;

	public InvertedIndex() {
		index = new HashMap<String, List<Occurrence>>();
	}

	public void put(String word, Occurrence docID) {
		if (index.get(word) == null) {
			index.put(word, new ArrayList<Occurrence>());
		}
		updateOrInsertOccurence(word, docID);
	}

	private void updateOrInsertOccurence(String word, Occurrence occ) {
		List<Occurrence> list = index.get(word);
		if (list.contains(occ)) {
			int pos = list.indexOf(occ);
			Occurrence o = list.get(pos);
			o.count++;
		} else {
			occ.count = 1;
			index.get(word).add(occ);
		}
	}

	@Override
	public SearchResult lookup(String word) {
		List<Occurrence> list = index.get(word);

		SearchResult sr = new SearchResult();
		if (list.isEmpty())
			return sr;

		for (Occurrence occurrence : list) {
			sr.add(occurrence);
		}
		return sr;
	}

	@Override
	public List<Occurrence> searchConjunctively(String... keywords) {
		List<Occurrence> result = new ArrayList<Occurrence>();
		result = index.get(keywords[0]);
		for (int i = 0; i < keywords.length; i++) {
			List<Occurrence> occs = index.get(keywords[i]);
			result = intersect(result, occs);
		}
		return result;
	}

	// l1 and l2 have to be sorted in ascending order
	private <T extends Comparable<T>> List<T> intersect(List<T> l1, List<T> l2) {
		List<T> result = new ArrayList<T>();
		if (l1 == null || l1.isEmpty()) {
			return result;
		}
		if (l2 == null || l2.isEmpty()) {
			return result;
		}

		Iterator<T> i1 = l1.iterator();
		Iterator<T> i2 = l2.iterator();
		T t1 = i1.next(), t2 = i2.next();
		try {
			while (true) {
				switch (t1.compareTo(t2)) {
				case 0:
					result.add(t1);
					result.add(t2);
					t1 = i1.next();
					t2 = i2.next();
					break;
				case -1:
					t1 = i1.next();
					break;
				case 1:
					t2 = i2.next();
					break;
				default:
					throw new IllegalStateException(
							"Comparison must yield either -1, 0 or 1");
				}
			}
		} catch (NoSuchElementException e) {
			// one cursor reached list end -> no more common elements
			return result;
		}
	}

	Map<String, List<Occurrence>> getInternal() {
		return index;
	}

	public void setInternal(Map<String, List<Occurrence>> internal) {
		index = internal;
	}

}
