package edu.kit.iti.algo2.textindexing.searchengine;

import java.util.HashSet;
import java.util.Set;

import edu.kit.iti.algo2.textindexing.TimeSlot;
import edu.kit.iti.algo2.textindexing.TimedDocument;
import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.DocumentRepository;

public class SearchResultEntry implements Comparable<SearchResultEntry> {
    private Occurrence occurrence;
    private Set<String> matchedWords = new HashSet<>();
    private int score;

    public SearchResultEntry(Occurrence occurrence, String... matched) {
	this.setOccurrence(occurrence);
	for (String s : matched) {
	    matchedWords.add(s);
	}
    }

    public TimeSlot getTimeSlot() {
	return DocumentRepository.getInstance().getTimeSlotFor(occurrence);
    }

    public TimedDocument getTimedDocument() {
	return DocumentRepository.getInstance().getTimeSlotFor(occurrence)
		.getTimedDocument();
    }

    public Set<String> getMatchedWords() {
	return matchedWords;
    }

    public void setMatchedWords(Set<String> matchedWords) {
	this.matchedWords = matchedWords;
    }

    @Override
    public int compareTo(SearchResultEntry o) {
	return occurrence.compareTo(o.occurrence);
    }

    public int getScore() {
	return score;
    }

    public void setScore(int score) {
	this.score = score;
    }

    public Occurrence getOccurrence() {
	return occurrence;
    }

    public void setOccurrence(Occurrence occurrence) {
	this.occurrence = occurrence;
    }

}
