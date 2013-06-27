package edu.kit.iti.algo2.textindexing.searchengine;

import java.util.Comparator;

import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;

public class DocIdComparator implements Comparator<Occurrence> {
	@Override
	public int compare(Occurrence o1, Occurrence o2) {
		return Integer.compare(o1.getDocID(), o2.getDocID());
	}

}
