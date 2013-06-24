package edu.kit.iti.algo2.textindexing.alexdomge.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InvertedIndex {
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
			if (occ.docID == docID) {
				occ.count++;
				return;
			}
		}
		Occurrence occ = new Occurrence(docID);
		index.get(word).add(occ);
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

	static class Occurrence {
		final int docID;
		int count = 1;

		public Occurrence(int docID) {
			this.docID = docID;
		}
	}

	public static void main(String[] args) {
		InvertedIndex inv = new InvertedIndex();
		inv.loadZippedXML("index.xml.zip");
		return;
	}

	static class IndexXMLProcessor {

		private static final String COUNT = "count";
		private static final String DOCUMENT = "document";
		private static final String OCCURRENCE = "occurrence";
		private static final String WORD = "word";
		private static final String ENTRY = "entry";
		private static final String INDEX = "index";

		static Map<String, List<Occurrence>> readFile(String filename) {
			Map<String, List<Occurrence>> index = new HashMap<String, List<Occurrence>>();
			XMLStreamReader parser = null;
			try {
				parser = XMLInputFactory.newInstance().createXMLStreamReader(
						new FileInputStream(filename));

				List<Occurrence> list = null;
				while (parser.hasNext()) {
					if (parser.getEventType() != XMLStreamConstants.START_ELEMENT) {
						parser.next();
						continue;
					}
					String name = parser.getLocalName();
					String word;
					if (name.equals(ENTRY)) {
						word = parser.getAttributeValue(null, WORD);
						list = new ArrayList<Occurrence>();
						index.put(word, list);
					} else if (name.equals(OCCURRENCE)) {
						int docID = Integer.parseInt(parser.getAttributeValue(
								null, DOCUMENT));
						int count = Integer.parseInt(parser.getAttributeValue(
								null, COUNT));
						Occurrence occ = new InvertedIndex.Occurrence(docID);
						occ.count = count;
						list.add(occ);
					}
					parser.next();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			Map<String, List<Occurrence>> index = new HashMap<String, List<Occurrence>>();
			XMLStreamReader parser = null;
			ZipInputStream zip = null;
			try {
				zip = new ZipInputStream(new FileInputStream(filename));
				zip.getNextEntry();
				parser = XMLInputFactory.newInstance().createXMLStreamReader(
						zip);

				List<Occurrence> list = null;
				while (parser.hasNext()) {
					if (parser.getEventType() != XMLStreamConstants.START_ELEMENT) {
						parser.next();
						continue;
					}
					String name = parser.getLocalName();
					String word;
					if (name.equals(ENTRY)) {
						word = parser.getAttributeValue(null, WORD);
						list = new ArrayList<Occurrence>();
						index.put(word, list);
					} else if (name.equals(OCCURRENCE)) {
						int docID = Integer.parseInt(parser.getAttributeValue(
								null, DOCUMENT));
						int count = Integer.parseInt(parser.getAttributeValue(
								null, COUNT));
						Occurrence occ = new InvertedIndex.Occurrence(docID);
						occ.count = count;
						list.add(occ);
					}
					parser.next();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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

		static void writeFile(String filename,
				Map<String, List<Occurrence>> index) {

			try {
				Document dom = buildDOM(index);
				Transformer transf = buildTransformer();

				transf.transform(new DOMSource(dom), new StreamResult(
						new FileOutputStream(filename)));

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		static void writeZipArchive(String filename,
				Map<String, List<Occurrence>> index) {

			try {
				Document dom = buildDOM(index);
				Transformer transf = buildTransformer();
				ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
						filename + ".zip"));
				ZipEntry entry = new ZipEntry(filename);
				zip.putNextEntry(entry);

				transf.transform(new DOMSource(dom), new StreamResult(zip));
				zip.closeEntry();
				zip.close();

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private static Transformer buildTransformer()
				throws TransformerConfigurationException,
				TransformerFactoryConfigurationError {
			Transformer transf = TransformerFactory.newInstance()
					.newTransformer();
			transf.setOutputProperty(OutputKeys.INDENT, "yes");
			transf.setOutputProperty(OutputKeys.METHOD, "xml");
			transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
			transf.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			return transf;
		}

		private static Document buildDOM(Map<String, List<Occurrence>> index) {
			Document dom = null;
			try {
				dom = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument();
				Element root = dom.createElement(INDEX);
				for (Entry<String, List<Occurrence>> wordEntry : index
						.entrySet()) {
					Element wordNode = dom.createElement(ENTRY);
					wordNode.setAttribute(WORD, wordEntry.getKey());
					for (Occurrence occ : wordEntry.getValue()) {
						Element occurenceNode = dom.createElement(OCCURRENCE);
						occurenceNode.setAttribute(DOCUMENT, "" + occ.docID);
						occurenceNode.setAttribute(COUNT, "" + occ.count);
						wordNode.appendChild(occurenceNode);
					}
					root.appendChild(wordNode);
				}
				dom.appendChild(root);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return dom;
		}

	}
}
