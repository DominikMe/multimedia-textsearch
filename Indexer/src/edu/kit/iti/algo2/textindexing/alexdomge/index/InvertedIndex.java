package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.kit.iti.algo2.textindexing.IIndex;
import edu.kit.iti.algo2.textindexing.searchengine.SearchResult;

public class InvertedIndex implements Searchable, Serializable, IIndex {
	private Map<String, List<Occurrence>> index;

	public InvertedIndex() {
		index = new HashMap<String, List<Occurrence>>();
	}

	public void put(String word, int docID) {
		if (index.get(word) == null) {
			index.put(word, new ArrayList<Occurrence>());
		}
		updateOrInsertOccurence(word, docID);
	}

	private void updateOrInsertOccurence(String word, int docID) {
		List<Occurrence> list = index.get(word);
		if (!list.isEmpty()) {
			Occurrence occ = list.get(list.size() - 1);
			if (occ.getDocID() == docID) {
				occ.setCount(occ.getCount() + 1);
				return;
			}
		}
		Occurrence occ = new Occurrence(docID);
		index.get(word).add(occ);
	}

	@Override
	public SearchResult lookup(String word) {
		List<Occurrence> list = index.get(word);

		SearchResult sr = new SearchResult();
		if (list.isEmpty())
			return sr;
		
		for (Occurrence occurrence : list) {
			sr.add(occurrence);
		}
		return sr;
	}

	public void saveXML(String filename) {
		IndexXMLProcessor.writeFile(filename, index);
	}

	public void saveZippedXML(String filename) {
		IndexXMLProcessor.writeZipArchive(filename, index);
	}

	public void loadXML(String filename) {
		index = IndexXMLProcessor.readFile(filename);
	}

	public void loadZippedXML(String filename) {
		index = IndexXMLProcessor.readZipArchive(filename);
	}

	public static InvertedIndex fromDump(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(file)));
		Object obj = ois.readObject();
		ois.close();
		return (InvertedIndex) obj;
	}

	public void saveDump(File file) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(file)));
		oos.writeObject(this);
		oos.close();
	}

	@Override
	public List<Occurrence> searchConjunctively(String... keywords) {
		List<Occurrence> result = new ArrayList<Occurrence>();
		result = index.get(keywords[0]);
		for (int i = 0; i < keywords.length; i++) {
			List<Occurrence> occs = index.get(keywords[i]);
			result = intersect(result, occs);
		}
		return result;
	}

	// l1 and l2 have to be sorted in ascending order
	private <T extends Comparable<T>> List<T> intersect(List<T> l1, List<T> l2) {
		List<T> result = new ArrayList<T>();
		if (l1 == null || l1.isEmpty()) {
			return result;
		}
		if (l2 == null || l2.isEmpty()) {
			return result;
		}

		Iterator<T> i1 = l1.iterator();
		Iterator<T> i2 = l2.iterator();
		T t1 = i1.next(), t2 = i2.next();
		try {
			while (true) {
				switch (t1.compareTo(t2)) {
				case 0:
					result.add(t1);
					result.add(t2);
					t1 = i1.next();
					t2 = i2.next();
					break;
				case -1:
					t1 = i1.next();
					break;
				case 1:
					t2 = i2.next();
					break;
				default:
					throw new IllegalStateException(
							"Comparison must yield either -1, 0 or 1");
				}
			}
		} catch (NoSuchElementException e) {
			// one cursor reached list end -> no more common elements
			return result;
		}
	}

	public static void main(String[] args) throws IOException {
		InvertedIndex inv = new InvertedIndex();
		inv.loadZippedXML("built_indexes/index.xml.zip");
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		System.out
				.println("Type search terms divided by spaces: (:q for exit)");
		String line = "";
		while (!line.equals(":q")) {
			line = r.readLine();
			// List<Occurrence> result = inv
			// .searchConjunctively("perplexity", "house", "rabble");
			List<Occurrence> result = inv.searchConjunctively(line.split(" "));
			if (result.isEmpty())
				System.out.println("No document matches all keywords.");
			for (Occurrence occ : result) {
				System.out.println(occ);
			}
			System.out.println();
		}
		r.close();
		return;
	}

}
