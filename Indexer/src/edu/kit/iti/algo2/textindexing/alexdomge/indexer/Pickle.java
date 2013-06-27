package edu.kit.iti.algo2.textindexing.alexdomge.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.kit.iti.algo2.textindexing.TimedDocument;

public class Pickle {

	public static void saveObject(File file, Object obj)
			throws FileNotFoundException {
		saveObject(new FileOutputStream(file), obj);
	}

	public static void saveObject(OutputStream os, Object obj) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object readObject(File file) throws FileNotFoundException {
		return readObject(new FileInputStream(file));
	}

	public static Object readObject(InputStream in) {
		try {
			ObjectInputStream oos = new ObjectInputStream(in);
			Object obj = oos.readObject();
			oos.close();
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveObjectGZip(File file, Object td)
			throws FileNotFoundException, IOException {
		saveObject(new GZIPOutputStream(new FileOutputStream(file)), td);
	}

	public static Object readObjectGZip(File file)
			throws FileNotFoundException, IOException {
		return readObject(new GZIPInputStream(new FileInputStream(file)));
	}
}
