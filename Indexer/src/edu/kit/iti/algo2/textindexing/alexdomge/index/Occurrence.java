package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.Serializable;
import java.lang.reflect.Field;

class Occurrence implements Comparable<Occurrence>, Serializable {
	final int docID;
	int count = 1;

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
		if (docID < o.docID)
			return -1;
		if (docID > o.docID)
			return 1;
		return 0;
	}
}