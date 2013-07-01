package edu.kit.iti.algo2.textindexing.alexdomge.indexer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.kit.iti.algo2.textindexing.TimeSlot;
import edu.kit.iti.algo2.textindexing.TimedDocument;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndex;
import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.LanguageSpecific;

public class TimedDocumentIndexBuilder {
	private InvertedIndex index;
	private List<TimedDocument> docs;
	private LanguageSpecific lang;

	public TimedDocumentIndexBuilder(LanguageSpecific lang) {
		setIndex(new InvertedIndex());
		docs = new LinkedList<TimedDocument>();
		this.lang = lang;
	}

	public void addDocuments(TimedDocument... files) {
		for (TimedDocument f : files)
			docs.add(f);
	}

	public InvertedIndex build() {
		if (docs.isEmpty())
			throw new IllegalStateException(
					"Cannot build index from 0 documents.");
		for (TimedDocument td : docs) {
			processDoc(td);
		}
		return getIndex();
	}

	private void processDoc(TimedDocument td) {
		for (TimeSlot ts : td.getTimeSlots()) {
			process(td, ts);
		}
	}

	private void process(TimedDocument td, TimeSlot ts) {
		Collection<String> content = lang.clean(ts.getContent());
		Occurrence occ = new Occurrence(td.getUuid(), ts.getStartTime());
		for (String s : content) {
			getIndex().put(s, occ);
		}
	}

	public InvertedIndex getIndex() {
		return index;
	}

	public void setIndex(InvertedIndex index) {
		this.index = index;
	}
}
