package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class IndexXMLProcessor {

    private static final String COUNT = "count";
    private static final String DOCUMENT = "document";
    private static final String OCCURRENCE = "occurrence";
    private static final String WORD = "word";
    private static final String ENTRY = "entry";
    private static final String INDEX = "index";
    private static final String TIMESLOT = "ts";

    static Map<String, List<Occurrence>> read(InputStream stream) {
	Map<String, List<Occurrence>> index = null;
	XMLStreamReader parser = null;
	try {
	    parser = XMLInputFactory.newInstance()
		    .createXMLStreamReader(stream);
	    index = buildIndexFromXML(parser);
	    IOUtils.closeQuietly(stream);
	} catch (XMLStreamException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (FactoryConfigurationError e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		parser.close();
	    } catch (XMLStreamException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	return index;
    }

    static Map<String, List<Occurrence>> readZipArchive(String filename) {
	Map<String, List<Occurrence>> index = null;
	XMLStreamReader parser = null;
	ZipInputStream zip = null;
	try {
	    zip = new ZipInputStream(new FileInputStream(filename));
	    zip.getNextEntry();
	    parser = XMLInputFactory.newInstance().createXMLStreamReader(zip);
	    index = buildIndexFromXML(parser);
	} catch (XMLStreamException | FactoryConfigurationError | IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		parser.close();
	    } catch (XMLStreamException e) {
		e.printStackTrace();
	    }
	}
	return index;
    }

    private static Map<String, List<Occurrence>> buildIndexFromXML(
	    XMLStreamReader parser) throws XMLStreamException {
	Map<String, List<Occurrence>> index = new HashMap<String, List<Occurrence>>();
	List<Occurrence> list = null;
	while (parser.hasNext()) {
	    if (parser.getEventType() != XMLStreamConstants.START_ELEMENT) {
		parser.next();
		continue;
	    }

	    String name = parser.getLocalName();
	    String word;

	    switch (name) {
	    case ENTRY:
		word = parser.getAttributeValue(null, WORD);
		list = new ArrayList<Occurrence>();
		index.put(word, list);
		break;
	    case OCCURRENCE:
		UUID docID = UUID.fromString(parser.getAttributeValue(null,
			DOCUMENT));

		int tsID = Integer.parseInt(parser.getAttributeValue(null,
			TIMESLOT));
		int count = Integer.parseInt(parser.getAttributeValue(null,
			COUNT));
		Occurrence occ = new Occurrence(docID, tsID);
		occ.setCount(count);
		list.add(occ);
	    }
	    parser.next();
	}
	return index;
    }

    static void writeFile(OutputStream stream,
	    Map<String, List<Occurrence>> index2) {

	try {
	    Document dom = buildDOM(index2);
	    Transformer transf = buildTransformer();

	    transf.transform(new DOMSource(dom), new StreamResult(stream));
	    IOUtils.closeQuietly(stream);
	} catch (TransformerFactoryConfigurationError | TransformerException e) {
	    e.printStackTrace();
	}
    }

    static void writeZipArchive(String filename,
	    Map<String, List<Occurrence>> index) {
	Document dom = buildDOM(index);
	Transformer transf;
	try {
	    transf = buildTransformer();
	    ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
		    filename + ".zip"));
	    ZipEntry entry = new ZipEntry(filename);
	    zip.putNextEntry(entry);

	    transf.transform(new DOMSource(dom), new StreamResult(zip));
	    zip.closeEntry();
	    zip.close();
	} catch (TransformerFactoryConfigurationError | TransformerException
		| IOException e) {
	    e.printStackTrace();
	}
    }

    private static Transformer buildTransformer()
	    throws TransformerConfigurationException,
	    TransformerFactoryConfigurationError {
	Transformer transf = TransformerFactory.newInstance().newTransformer();
	transf.setOutputProperty(OutputKeys.INDENT, "yes");
	transf.setOutputProperty(OutputKeys.METHOD, "xml");
	transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	transf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
	transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
		"4");
	return transf;
    }

    private static Document buildDOM(Map<String, List<Occurrence>> index) {
	Document dom = null;
	try {
	    dom = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		    .newDocument();
	    Element root = dom.createElement(INDEX);
	    for (Entry<String, List<Occurrence>> wordEntry : index.entrySet()) {
		Element wordNode = dom.createElement(ENTRY);
		wordNode.setAttribute(WORD, wordEntry.getKey());
		for (Occurrence occ : wordEntry.getValue()) {
		    Element occurenceNode = dom.createElement(OCCURRENCE);
		    occurenceNode.setAttribute(DOCUMENT, "" + occ.getDocID());
		    occurenceNode
			    .setAttribute(TIMESLOT, "" + occ.getTimeSlot());
		    occurenceNode.setAttribute(COUNT, "" + occ.getCount());
		    wordNode.appendChild(occurenceNode);
		}
		root.appendChild(wordNode);
	    }
	    dom.appendChild(root);
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
	return dom;
    }

}