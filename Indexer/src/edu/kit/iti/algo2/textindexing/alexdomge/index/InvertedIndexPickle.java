package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.kit.iti.algo2.textindexing.alexdomge.indexer.Pickle;

public class InvertedIndexPickle {

	public static String SFX_XML_GZIP = ".xml.gz";
	public static String SFX_XML = ".xml";
	public static String SFX_ZIP = ".zip";
	public static String SFX_DUMP = ".dump";
	public static String SFX_DUMP_GZIP = ".dump.gzip";

	public static void storeToXml(String basename, InvertedIndex ii)
			throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(basename + SFX_XML);
		IndexXMLProcessor.writeFile(fos, ii.getInternal());
	}

	public static InvertedIndex loadFromXml(String basename)
			throws FileNotFoundException {
		InvertedIndex ii = new InvertedIndex();
		InputStream fis = new FileInputStream(basename + SFX_XML);
		Map<String, List<Occurrence>> a = IndexXMLProcessor.read(fis);
		ii.setInternal(a);
		return ii;
	}

	public static void storeToGZipXml(String basename, InvertedIndex ii)
			throws FileNotFoundException, IOException {
		GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(
				basename + SFX_XML));
		IndexXMLProcessor.writeFile(fos, ii.getInternal());
		fos.close();
	}

	public static InvertedIndex loadFromGZipXml(String basename)
			throws IOException {
		InvertedIndex ii = new InvertedIndex();
		InputStream fis = new GZIPInputStream(new FileInputStream(basename
				+ SFX_XML));
		Map<String, List<Occurrence>> a = IndexXMLProcessor.read(fis);
		ii.setInternal(a);
		return ii;
	}

	public static InvertedIndex loadFromDump(String file)
			throws FileNotFoundException {
		return (InvertedIndex) Pickle.readObject(new File(file + SFX_DUMP));
	}

	public static void storeToDump(String file, InvertedIndex ii)
			throws IOException {
		Pickle.saveObject(new File(file + SFX_DUMP), ii);
	}

	public static InvertedIndex loadFromDumpGZip(String file)
			throws FileNotFoundException, IOException {
		return (InvertedIndex) Pickle.readObject(new GZIPInputStream(
				new FileInputStream(file + SFX_DUMP_GZIP)));
	}

	public static void storeToDumpGZip(String file, InvertedIndex ii)
			throws IOException {
		Pickle.saveObject(new GZIPOutputStream(new FileOutputStream(file
				+ SFX_DUMP_GZIP)), ii);
	}

}
