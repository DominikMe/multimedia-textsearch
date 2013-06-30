package edu.kit.iti.algo2.textindexing.video;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.javatuples.Pair;

import edu.kit.iti.algo2.textindexing.TimeSlot;
import edu.kit.iti.algo2.textindexing.TimedDocument;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.EnglishSpecific;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.GermanSpecific;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.LanguageSpecific;

public class Transformer {

    private TimeSlot[] timeContent;

    public Transformer(List<Pair<Integer, String>> list, LanguageSpecific lang) {
	this.timeContent = new TimeSlot[list.size()];
	for (Pair<Integer, String> pair : list) {

	    TimeSlot ts = new TimeSlot();
	    ts.setStartTime(pair.getValue0());
	    ts.setContent(lang.tokenizeAndClean(pair.getValue1()));
	}

	Arrays.sort(timeContent, new TimeSorter());

	for (int i = 1; i < timeContent.length; i++) {
	    timeContent[i - 1].setEndTime(timeContent[i].getStartTime());
	}
    }

    public static TimedDocument run(ConversionOptions co) throws IOException {
	List<Pair<Integer, String>> list = Video2Text.run(co);
	LanguageSpecific lang = new GermanSpecific();
	switch (co.getLanguage()) {
	case "deu":
	    lang = new GermanSpecific();
	    break;
	case "eng":
	    lang = new EnglishSpecific();
	}
	Transformer t = new Transformer(list, lang);
	return t.getDocument(co);
    }

    private TimedDocument getDocument(ConversionOptions co) {
	TimedDocument td = new TimedDocument();
	td.setMultimediaFile(co.getVideoFile().getAbsolutePath());
	for (TimeSlot ts : timeContent) {
	    td.add(ts);
	}
	return td;
    }

    static class TimeSorter implements Comparator<TimeSlot> {
	@Override
	public int compare(TimeSlot o1, TimeSlot o2) {
	    return Integer.compare(o1.getStartTime(), o2.getStartTime());
	}

    }
}
