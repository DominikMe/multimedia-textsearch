package edu.kit.iti.algo2.textindexing.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndex;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndexPickle;
import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;

public class RetrievalIndex {
	public static void main(String[] args) throws IOException {
		InvertedIndex inv = InvertedIndexPickle.loadFromGZipXml("index");

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
