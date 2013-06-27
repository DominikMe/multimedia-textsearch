package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.Serializable;
import java.lang.reflect.Field;

public class Occurrence implements Comparable<Occurrence>, Serializable {
	private static final long serialVersionUID = -5290073216052377404L;

	private final int docID;
	private int count = 1;

	public Occurrence(int docID) {
		this.docID = docID;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Class<? extends Occurrence> clazz = this.getClass();
		sb.append(clazz.getSimpleName() + "{ ");
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			sb.append(f.getName() + "=");
			try {
				sb.append(f.get(this) + ", ");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int compareTo(Occurrence o) {
		return Integer.compare(docID, o.getDocID());
	}

	public int getDocID() {
		return docID;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}