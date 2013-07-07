package edu.kit.iti.algo2.textindexing.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jdom2.JDOMException;

import edu.kit.iti.algo2.textindexing.TimedDocument;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndex;
import edu.kit.iti.algo2.textindexing.alexdomge.index.InvertedIndexPickle;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.DocumentRepository;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.TimedDocumentIndexBuilder;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.Language;
import edu.kit.iti.algo2.textindexing.alexdomge.indexer.lang.LanguageSpecific;

public class TimedDocumentIndexerMain {
//    private static final String FILE_ENDING = ".td.xml";

    public static void main(String[] args) throws IOException, ParseException {
	CommandLine cmd = parseInput(args);

	@SuppressWarnings("unchecked")
	List<String> files = cmd.getArgList();

	LanguageSpecific language = Language.valueOf(
		cmd.getOptionValue('l', "ENGLISH")).getLanguageSpecific();

	File indexFile = new File(cmd.getOptionValue('i'));
	File repoFolder = new File(cmd.getOptionValue('r'));

	TimedDocumentIndexBuilder indexer = new TimedDocumentIndexBuilder(
		language);

	if (indexFile.exists()) {
	    InvertedIndex index = InvertedIndexPickle.loadFrom(indexFile);
	    indexer.setIndex(index);
	}

	DocumentRepository dr = DocumentRepository.init(repoFolder);

	for (String filename : files) {
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
	System.out.format("Writing %s%n", indexFile.getAbsolutePath());
	InvertedIndexPickle.storeTo(indexFile, ii);
    }

    static CommandLine parseInput(String[] args) throws ParseException {
	Options options = new Options();
	options.addOption("i", "index", true,
		"defines the index that should be written or updated");
	options.addOption("r", "repository", true, "repository folder");
	options.addOption("v", "verbose", false, "verbose output");
	options.addOption("h", "help", false, "help");

	CommandLineParser clip = new GnuParser();
	CommandLine cmd = clip.parse(options, args);

	if (cmd.hasOption('h')) {
	    HelpFormatter hf = new HelpFormatter();
	    hf.printHelp("TimedDocumentIndexerMain", options);
	    System.exit(0);
	}

	if (!cmd.hasOption('i')) {
	    System.err.println("no index provided");
	    System.exit(1);
	}

	if (!cmd.hasOption('r')) {
	    System.err.println("no repo provided");
	    System.exit(1);
	}
	return cmd;
    }
}