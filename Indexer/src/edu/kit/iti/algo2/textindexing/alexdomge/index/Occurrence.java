package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.Serializable;
import java.util.UUID;

public class Occurrence implements Comparable<Occurrence>, Serializable {
	private static final long serialVersionUID = -5290073216052377404L;

	private final UUID docID;
	private final int timeslot;
	int count = 1;

	public Occurrence(UUID docID, int timeslot) {
		this.docID = docID;
		this.timeslot = timeslot;
	}

	public Occurrence(String uuid, int startTime) {
		this(UUID.fromString(uuid), startTime);
	}

	public Occurrence(Occurrence occurrence) {
		this(occurrence.docID, occurrence.timeslot);
	}

	@Override
	public String toString() {
		return "Occurrence [docID=" + docID + ", timeslot=" + timeslot
				+ ", count=" + count + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((docID == null) ? 0 : docID.hashCode());
		result = prime * result + timeslot;
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
		Occurrence other = (Occurrence) obj;
		if (docID == null) {
			if (other.docID != null)
				return false;
		} else if (!docID.equals(other.docID))
			return false;
		if (timeslot != other.timeslot)
			return false;
		return true;
	}

	@Override
	public int compareTo(Occurrence o) {
		int c = docID.compareTo(o.getDocID());
		if (c == 0) {
			return Integer.compare(timeslot, o.getTimeSlot());
		} else {
			return c;
		}
	}

	public UUID getDocID() {
		return docID;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTimeSlot() {
		return timeslot;
	}

	public static Occurrence create() {
		return new Occurrence(UUID.randomUUID(), 0);
	}
}