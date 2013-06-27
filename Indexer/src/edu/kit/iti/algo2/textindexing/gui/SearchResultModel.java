package edu.kit.iti.algo2.textindexing.gui;

import javax.swing.DefaultListModel;

import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResult;

public class SearchResultModel extends DefaultListModel<Occurrence> {
	private static final long serialVersionUID = 5888503407253538963L;

	public SearchResultModel(SearchResult sr) {
		for (Occurrence e : sr.result) {
			addElement(e);
		}
	}

	public SearchResultModel() {
	}

}
