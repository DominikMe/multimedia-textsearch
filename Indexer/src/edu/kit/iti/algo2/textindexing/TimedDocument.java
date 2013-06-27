package edu.kit.iti.algo2.textindexing;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * A {@link TimedDocument} describes a document with time associated content,
 * e.g. a video file or something else...
 * 
 * @author weigla
 * 
 */
public class TimedDocument {
	private Set<TimeSlot> timeSlots = new TreeSet<>();
	private String multimediaFile;
	private String uuid;

	public TimedDocument() {
		setUuid(UUID.randomUUID().toString());
	}

	public TimedDocument merge(TimedDocument other) {
		List<TimeSlot> a = new LinkedList<>(timeSlots);
		List<TimeSlot> b = new LinkedList<>(other.timeSlots);

		TimedDocument td = new TimedDocument();
		td.setMultimediaFile(multimediaFile);

		int i = 0, j = 0;

		while (i <= a.size() && j <= b.size()) {
			TimeSlot tsA = a.get(i);
			TimeSlot tsB = b.get(i);

			TimeSlot ts = TimeSlot.combine(tsA, tsB);
			td.add(ts);

			if (tsA.getTime() == tsB.getTime()) {
				i++;
				j++;
			}

			if (tsA.getTime() < tsB.getTime()) {
				i++;
			}

			if (tsA.getTime() > tsB.getTime()) {
				j++;
			}
		}
		return td;
	}

	public static TimedDocument readFromFile(File file) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(file);
		Element root = document.getRootElement();

		TimedDocument td = new TimedDocument();
		td.setMultimediaFile(root.getAttributeValue("file"));
		td.setUuid(root.getAttributeValue("uuid"));

		for (Element slotE : root.getChildren()) {
			td.add(TimeSlot.fromXmlElement(slotE));
		}

		return td;
	}

	public int size() {
		return timeSlots.size();
	}

	public boolean isEmpty() {
		return timeSlots.isEmpty();
	}

	public boolean contains(Object o) {
		return timeSlots.contains(o);
	}

	public Iterator<TimeSlot> iterator() {
		return timeSlots.iterator();
	}

	public boolean add(TimeSlot e) {
		return timeSlots.add(e);
	}

	public boolean remove(Object o) {
		return timeSlots.remove(o);
	}

	public void clear() {
		timeSlots.clear();
	}

	public String getMultimediaFile() {
		return multimediaFile;
	}

	public void setMultimediaFile(String multimediaFile) {
		this.multimediaFile = multimediaFile;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Set<TimeSlot> getTimeSlots() {
		return timeSlots;
	}

}
