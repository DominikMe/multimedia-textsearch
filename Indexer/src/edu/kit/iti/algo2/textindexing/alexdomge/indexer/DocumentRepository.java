package edu.kit.iti.algo2.textindexing.alexdomge.indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.jdom2.JDOMException;

import edu.kit.iti.algo2.textindexing.TimeSlot;
import edu.kit.iti.algo2.textindexing.TimedDocument;
import edu.kit.iti.algo2.textindexing.alexdomge.index.Occurrence;

public class DocumentRepository {
	private static DocumentRepository INSTANCE = null;
	private static Map<UUID, TimedDocument> cache = new WeakHashMap<>();
	private File repoDir;

	public DocumentRepository(File dir) throws IOException {
		this.repoDir = dir;
	}

	public void add(TimedDocument td) throws IOException {
		Pickle.saveObjectGZip(new File(repoDir, td.getUuid()), td);
	}

	public TimeSlot getTimeSlotFor(Occurrence occurrence) {
		TimedDocument td = loadTimedDocument(occurrence.getDocID());
		if (td == null) {
			return null;
		}

		for (TimeSlot ts : td.getTimeSlots()) {
			if (ts.getStartTime() == occurrence.getTimeSlot()) {
				return ts;
			}
		}
		return null;
	}

	private TimedDocument loadTimedDocument(UUID docID) {
		if (cache.containsKey(docID)) {
			return cache.get(docID);
		}

		File file = uuidToFilename(docID);
		TimedDocument td;
		try {
			td = TimedDocument.readFromFile(file);
			cache.put(docID, td);
			return td;
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private File uuidToFilename(UUID docID) {
		return new File(repoDir, docID.toString());
	}

	public static DocumentRepository init(File repo) {
		if (!repo.exists())
			repo.mkdirs();

		try {
			INSTANCE = new DocumentRepository(repo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return INSTANCE;
	}

	public static DocumentRepository getInstance() {
		return INSTANCE;
	}
}
