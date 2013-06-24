package edu.kit.iti.algo2.textindexing.alexdomge.indexer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndex;

public class IndexerMain {

	private static final String FILE_ENDING = ".trscrpt";

	public static void main(String[] args) {
		IndexBuilder builder = new IndexBuilder();

		List<String> fileNames = new ArrayList<String>();
		String out = null;

		try {
			int i = 0;
			while (i < args.length) {
				switch (args[i]) {
				case "-o":
					out = args[++i];
					break;
				case "-i":
					while (!args[++i].startsWith("-"))
						fileNames.add(args[i]);
					i--;
					break;
				default:
					showUsageHint();
					return;
				}
				i++;
			}
		} catch (IndexOutOfBoundsException e) {
			showError();
			showUsageHint();
		}

		if (fileNames.isEmpty()) {
			System.out.println("No input defined!");
			showUsageHint();
			return;
		}

		List<File> files = validateInputFiles(fileNames);
		if (out == null) {
			out = "index_" + System.currentTimeMillis() + ".xml";
		} else if (!out.endsWith(".xml"))
			out += ".xml";

		builder.addDocuments(files.toArray(new File[0]));
		System.out.println("Build inverted index...");
		InvertedIndex index = builder.build();
		System.out.println("Done.");
		System.out.println("Write output to " + out);
		index.saveXML(out);
		index.saveZippedXML(out);
	}

	private static List<File> validateInputFiles(List<String> in) {
		List<File> files = new ArrayList<File>();
		for (String file : in) {
			File f = new File(file);
			if (!f.exists()) {
				System.out.println("File " + file + " does not exist!");
				return null;
			}
			if (file.endsWith(FILE_ENDING)) {
				files.add(f);
			} else {
				if (!f.isDirectory()) {
					System.out.println(file
							+ " is neither a directory nor does it end on "
							+ FILE_ENDING + "!");
					return null;
				} else {
					File[] children = f.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(FILE_ENDING);
						}
					});
					for (File c : children) {
						files.add(c);
					}
				}
			}
		}
		return files;
	}

	private static void showUsageHint() {
		System.out
				.println("Usage: -i File1 File2 Dir1 File3 ... -o output.xml");
		System.out
				.println("Important: Input must be " + FILE_ENDING + " files");
	}

	private static void showError() {
		System.out.println("Malformed command!");
	}

}
