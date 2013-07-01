package edu.kit.iti.algo2.textindexing.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;

import edu.kit.iti.algo2.textindexing.TimedDocument;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndex;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndexPickle;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.DocumentRepository;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.TimedDocumentIndexBuilder;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.EnglishSpecific;

public class TimedDocumentIndexerMain {
	private static final String FILE_ENDING = ".td.xml";

	public static void main(String[] args) throws IOException {
		TimedDocumentIndexBuilder indexer = new TimedDocumentIndexBuilder(
				new EnglishSpecific());

		List<String> fileNames = new ArrayList<String>();

		for (String string : args) {
			fileNames.add(string);
		}

		String out = (fileNames.get(0));
		File repo = new File(fileNames.get(1));

		System.out.println("init document repository");
		DocumentRepository dr = DocumentRepository.init(repo);
		fileNames.remove(1);
		fileNames.remove(0);

		for (String filename : fileNames) {
			TimedDocument td;
			try {
				td = TimedDocument.readFromFile(new File(filename));
				dr.add(td);
				indexer.addDocuments(td);
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}

		indexer.build();
		InvertedIndex ii = indexer.getIndex();
		InvertedIndexPickle.storeToDumpGZip(out, ii);
		InvertedIndexPickle.storeToDump(out, ii);
		InvertedIndexPickle.storeToXml(out, ii);
	}
}
