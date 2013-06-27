package edu.kit.iti.algo2.textindexing.gui;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.AbstractListModel;

import edu.kit.iti.algo2.textindexing.searchengine.SearchResult;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResultEntry;

public class SearchResultModel extends AbstractListModel<SearchResultEntry> {
	private static final long serialVersionUID = -3150996617748311085L;
	private SearchResultEntry[] list;

	public SearchResultModel() {
	}

	public SearchResultModel(SearchResult sr) {
		int i = 0;
		list = new SearchResultEntry[sr.result.size()];
		for (SearchResultEntry s : sr.result) {
			list[i++] = s;
		}
		Arrays.sort(list, new ScoreComparator());
	}

	@Override
	public int getSize() {
		return list.length;
	}

	@Override
	public SearchResultEntry getElementAt(int index) {
		return list[index];
	}
}

class ScoreComparator implements Comparator<SearchResultEntry> {
	@Override
	public int compare(SearchResultEntry o1, SearchResultEntry o2) {
		return Integer.compare(o1.getScore(), o2.getScore());
	}
}