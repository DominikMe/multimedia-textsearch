package edu.kit.iti.algo2.textindexing;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;

import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.GermanSpecific;
import edu.kit.iti.algo2.textindexing.util.TokenTool;

public class TimeSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private Collection<String> content;
    private int startTime;

    private int endTime;
    private TimedDocument timedDocument;

    public static TimeSlot fromXmlElement(Element slotE, TimedDocument td) {
	TimeSlot ts = new TimeSlot();
	ts.content = new GermanSpecific().tokenizeAndClean(slotE.getTextTrim());
	ts.startTime = Integer.parseInt(slotE.getAttributeValue("start"));
	ts.endTime = Integer.parseInt(slotE.getAttributeValue("end"));
	ts.setTimedDocument(td);
	return ts;
    }

    public static TimeSlot combine(TimeSlot tsA, TimeSlot tsB) {
	TimeSlot ts = new TimeSlot();
	ts.setTime(Math.max(tsA.getTime(), tsB.getTime()));
	ts.setTime(Math.min(tsA.getEndTime(), tsB.getEndTime()));

	List<String> content = new LinkedList<>();
	content.addAll(tsA.getContent());
	content.addAll(tsB.getContent());

	ts.setContent(content);
	return ts;
    }

    public int getTime() {
	return startTime;
    }

    public void setTime(int time) {
	this.startTime = time;
    }

    public int getStartTime() {
	return startTime;
    }

    public void setStartTime(int startTime) {
	this.startTime = startTime;
    }

    public Collection<String> getContent() {
	return content;
    }

    public void setContent(Collection<String> collection) {
	this.content = collection;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + startTime;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TimeSlot other = (TimeSlot) obj;
	if (startTime != other.startTime)
	    return false;
	return true;
    }

    public int getEndTime() {
	return endTime;
    }

    public void setEndTime(int endTime) {
	this.endTime = endTime;
    }

    public TimedDocument getTimedDocument() {
	return timedDocument;
    }

    public void setTimedDocument(TimedDocument timedDocument) {
	this.timedDocument = timedDocument;
    }

    static class StartTimeComparator implements Comparator<TimeSlot>,
	    Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(TimeSlot o1, TimeSlot o2) {
	    return Integer.compare(o1.getStartTime(), o2.getStartTime());
	}

    }
}
