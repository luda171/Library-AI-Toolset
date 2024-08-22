package gov.lanl.urlcit.arxiv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import javax.xml.parsers.DocumentBuilderFactory;

//import javax.xml.parsers.DocumentBuilderFactory;

//import org.grobid.core.data.BiblioItem;
import org.grobid.core.document.Document;
import org.grobid.core.engines.Engine;
import org.grobid.core.engines.config.GrobidAnalysisConfig;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.utilities.GrobidProperties;
import org.grobid.core.utilities.OffsetPosition;
import org.grobid.core.utilities.SentenceUtilities;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.opencsv.CSVWriter;

import gov.lanl.urlcit.InputArgs;
import gov.lanl.urlcit.ResourceToTei;
import gov.lanl.urlcit.Sentence;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

//import org.xml.sax.InputSource;
//import org.w3c.dom.*;
//import org.xml.sax.InputSource;
//import org.w3c.dom.*;
//import javax.xml.parsers.*;
//import java.io.*;
//import javax.xml.transform.*;
//import javax.xml.transform.dom.*;
//import javax.xml.transform.stream.*;
//import org.w3c.dom.traversal.DocumentTraversal;
//import org.w3c.dom.traversal.NodeFilter;
//import org.w3c.dom.traversal.TreeWalker;

public class CitParser {
	static InputArgs args;
	private static List<String> textualElements = Arrays.asList("p", "figDesc");
	private static Properties properties;

	public static void main(String[] margs) {
		args = new InputArgs(margs);
		boolean grobid_as_service = false;
		Engine eng;
		String pGrobidHome;
		String dir;
		String host;
		String port;
		String sleeptime;
		String mname;
		String hdir="";
		 String pyear="2020";
		if (grobid_as_service) {
			dir = args.get("sdir", "/datasets/rassti-dataset/2022-01-20");
			host = args.get("grobidhost", "/datasets/rassti-dataset/2022-01-20");
			port = args.get("grobidport", "/datasets/rassti-dataset/2022-01-20");
			mname = args.get("grobidmname", "/datasets/rassti-dataset/2022-01-20");
			sleeptime = args.get("sleeptime", "/datasets/rassti-dataset/2022-01-20");
		} else {
			// pGrobidHome = args.get("grobidhome",
			// "/Users/ludab/Laptop/project2023/grobid/grobid-home/");
			dir = args.get("sdir", "/datasets/rassti-dataset/2022-01-20");
			hdir = args.get("hdir", "/datasets/rassti-dataset/2022-01-20");
			    pyear = args.get("year", "2020");
			// eng= init_grobid(pGrobidHome);
		}
		CitParser r = new CitParser();
		properties = new Properties();
		r.loadProperties(hdir);
		r.LoopFileStructure(dir,hdir,pyear);
		// r.LoopFileStructure(dir);

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	private void loadProperties(String hdir) {
		try (InputStream input = new FileInputStream(hdir + "config.properties")) {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveProperties(String hdir) {
		try (OutputStream output = new FileOutputStream(hdir + "config.properties")) {
			properties.store(output, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String[] createStringArrayFromList(List<String> list) {
		  String[] stringArray = new String[list.size()];

		  for (int i = 0; i < list.size(); i++) {
		    stringArray[i] = list.get(i);
		  }

		  return stringArray;
		}
	
	public static void test(String[] margs) {
		CitParser p = new CitParser();
		Map <String, Integer> dp  = new HashMap();
		List<String> list = Arrays.asList("1901.03697v1.1.pdf", "1901.03697v1.2.pdf",
				  "1901.03697v2d.pdf", "1901.03697v3.pdf", "1901.03697v3.pdf",
				  "1901.03698v1", "1901.03698v1", "1901.03699v1",
				  "1901.03699v1", "1901.03700v1", "1901.03700v1",
				  "1901.03701v1", "1901.03701v1");
		String[] pdfs = createStringArrayFromList(list);
		for (int j = 0; j < pdfs.length; j++) {
				//File pdf = pdfs[j];
				String pdfn = pdfs[j];
				pdfn=pdfn.replace(".pdf", "");
				String[] f1Parts = pdfn.split("v");
				System.out.println(f1Parts[1]);
				int v1 = p.Convert(f1Parts[1]);
				System.out.println(v1);
				
				//if (!pdfn.contains("pdf")) continue;
		}
		}
	public int  Convert(String s) {
		 String versionString = s.replaceAll("[^\\d.]", "");
		 //System.out.println(versionString);
	     // If the version number is 1.2, convert it to 2.
	        if (versionString.contains(".")) {
	            versionString = versionString.split("\\.")[1];
	        }

			int v1 = Integer.parseInt(versionString);
			return v1;
	}
	public List<String>  filter_latestversion (File[] pdfs) {
 	   
        List dpdfs = new ArrayList();
        Map <String, String> dp  = new HashMap();
        
 	   for (int j = 0; j < pdfs.length; j++) {
			File pdf = pdfs[j];
			String pdfn = pdf.getName();
			if (!pdfn.contains(".pdf")) continue;
			pdfn=pdfn.replace(".pdf", "");
			String[] f1Parts = pdfn.split("v");
		    
			
			int v1 = Convert(f1Parts[1]);
			if (dp.containsKey(f1Parts[0])) {
				int v = Convert(dp.get(f1Parts[0]));
				if (v1>v) dp.put(f1Parts[0],f1Parts[1]);
			}
			else {
				dp.put(f1Parts[0],f1Parts[1]);
			}
			
 	   }
 	   
 	   for (Map.Entry entry : dp.entrySet()) {
 	        //System.out.println(entry.getKey() + ":" + entry.getValue());
 	        dpdfs.add(entry.getKey()+"v"+entry.getValue());
 	    }
 	   
 	           	   
 	   return dpdfs;
 	   }
	
	
	// this can be customized depends on how pdfs stored
	  public void LoopFileStructure(String dir,String homedir,String pyear ) {
     	File fdir = new File(dir);
  		File[] laurs = fdir.listFiles();
  		int startIndex = Integer.parseInt(properties.getProperty("lastProcessedIndex", "0"));
  		try ( BufferedWriter writer = new BufferedWriter(new FileWriter("directories_without_tei.txt"))){
  				
  		// laurs for arxiv would be 0704 dirs
  		for (int i = startIndex; i < laurs.length; i++) {

  			File ldir = laurs[i];
  			String id = ldir.getName();
  			System.out.println(id);
  			String p = ldir.getAbsolutePath();
  			System.out.println(p);
  			File pdfdir = new File(p);

  			File[] pdfs = pdfdir.listFiles();
  			List fpdfs = filter_latestversion (pdfs); 
  			
  			// we may need to filter versions
  			for (int j = 0; j < pdfs.length; j++) {
  				File pdf = pdfs[j];
  				String pdfn = pdf.getName();
  				String nid = pdfn.replace(".pdf", "");
  				if (!fpdfs.contains(nid)) continue;
  				System.out.println(pdfn);
  				String pp = pdf.getParent();
  				try {
  					
  					
  					String tei = readFile(pp + "/" + nid + ".tei", StandardCharsets.UTF_8);
  				
  					String year = 20+id.substring(0,2);
  					
  					 //if (!year.equals(pyear)) continue;
  					
  					String ntei = processTEIString(tei, nid, pp, year);
  					
  				} catch (Exception e) {
  					writer.write(pp+"/" + nid + ".tei"); // Save the directory path
					writer.newLine();
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}
  				properties.setProperty("lastProcessedIndex", String.valueOf(i));
				saveProperties(homedir);
  			}
  		}
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} finally {
  			
  			}
  	}


	

	private static Element getFirstDirectChild(Element parent, String name) {
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof Element && name.equals(child.getNodeName()))
				return (Element) child;
		}
		return null;
	}

	public static void setSection(Element pElement, Sentence s) {
		// head

		// Element headElement = pElement.getParentNode().getFirstChild(pElement,
		// "head");
		Node node = pElement.getParentNode();
		Element elem = (Element) node;
		Element pelem = getFirstDirectChild(elem, "p");
		if (pelem != null) {
			Element headElement = getFirstDirectChild(pelem, "head");
			if (headElement != null) {
				String localTextContent = headElement.getTextContent();
				if (localTextContent != null) {
					System.out.println(localTextContent);
					s.setHead(localTextContent);
				}

			}
		}
	}

	/*
	 * 
	 * The get_Sentences() function gets a list of sentences from a document and
	 * writes them to a CSV file. The function first creates a empty list of
	 * Sentence objects. The function then iterates through the document's p
	 * elements, which represent paragraphs. For each paragraph program gets text
	 * content and then it splits to sentences using nlp model. For each sentence,
	 * the function gets the sentence's text, creates a new Sentence object, and
	 * adds the Sentence to the list of Sentences. The function then uses a
	 * LinkExtractor object to extract the URLs from the sentence's text. The
	 * function then iterates through the list of Sentences and writes the sentences
	 * and their corresponding URLs to a CSV file. We keep oder of sentence in
	 * paragraph for futher analisys
	 */

	public static void getSentences(org.w3c.dom.Document document, String fname, String d, Map urltitle, String fdate, Map locs) {
		List sents = new ArrayList();
		List urlsent = new ArrayList();
		SentenceDetectorME detector = splittosentence();

		LinkExtractor linkExtractor = LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW)).build();
		NodeList pList = document.getElementsByTagName("p");
		int refcount = pList.getLength();
		File urlfile = new File(d + "/" + "urlsent" + fname + ".csv");
		// global counters
		int scount = 0;
		int pcount = 0;
		for (int i = 0; i < refcount; i++) {
			pcount = pcount + 1;
			Element pElement = (Element) pList.item(i);
			String ptext = pElement.getTextContent();
			String[] sentences = detector.sentDetect(ptext);
			for (int j = 0; j < sentences.length; j++) {
				scount = scount + 1;
				String sent = sentences[j];
				// sent = sent.replace("\n", " ");
				// sent = sent.replaceAll("( )+", " ");
				Sentence s = new Sentence();
				s.setOrder(scount);
				s.setPorder(pcount);
				
				if (sent.contains("##bib##")) {
					s.setLoc("B");
					sent = sent.replace("##bib##", "");
				}
				if (sent.contains("##foot##")) {
					s.setLoc("F");
					sent = sent.replace("##foot##", "");
				}
				s.setText(sent);
				s.setSentenceid(fname + "_" + scount);
				s.setDate(fdate);
				// head //this section title is absolite
				// head
				setSection(pElement, s);

				// urls

				Iterable<LinkSpan> links = linkExtractor.extractLinks(sent);
				Iterator<LinkSpan> it = links.iterator();
				while (it.hasNext()) {
					LinkSpan link = it.next();
					String url = sent.substring(link.getBeginIndex(), link.getEndIndex());
					if (urltitle.containsKey(url)) {
						String tit = (String) urltitle.get(url);
						s.setTitle(tit, url);
						s.setLocs("B", url);
					}
					if (locs.containsKey(url)) {
						String loc = (String) locs.get(url);
						s.setLocs("F", url);
					}
					s.setUrl(url);

				}
				sents.add(s);

			}

		}
		//serializeSentences(sents, d, fname);
		// serialize_sent(sents, d, fname);
		serialize_jsonln(sents, d, fname);
		

	}

	public static void serialize_jsonln(List sents, String fdir, String fname) {
		FileWriter writera = null;
		FileWriter writer = null;
		try  {
			writer = new FileWriter(fdir+"/urlsent"+fname+".jsonl");
			writera = new FileWriter(fdir+"/allsent"+fname+".jsonl");
			for (int k = 0; k < sents.size(); k++) {
				Sentence sent = (Sentence) sents.get(k);
				List urls = sent.getUrlList();
				int us = urls.size();
				String js = sent.toJson();
				if (us > 0) {
					writer.write(js + "\n");
				}
				writera.write(js + "\n");

			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} finally {
			try {
				writera.close();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				writer.close();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}

	public static void serializeSentences(List<Sentence> sentences, String directory, String filename) {
		File urlFile = new File(directory + "/urlsentv1" + filename + ".csv");
		File resFile = new File(directory + "/allsentv1" + filename + ".csv");

		try (CSVWriter cwr = new CSVWriter(new FileWriter(resFile));
				CSVWriter cwr2 = new CSVWriter(new FileWriter(urlFile))) {

			for (Sentence sent : sentences) {
				List<String> urls = sent.getUrlList();
				String[] nextLine;

				if (sent.getUrl().length() <= 1) {
					// No URL
					nextLine = new String[] { sent.getSentenceid(), String.valueOf(sent.getOrder()),
							String.valueOf(sent.getPorder()), sent.getUrl(), sent.getText(), sent.getHead(),
							sent.getTitle(), String.valueOf(sent.getUrlList().size()), sent.getLoc(), sent.getfdate() };

					cwr.writeNext(nextLine);
				} else {
					Iterator<String> it = urls.iterator();
					StringBuilder murl = new StringBuilder();
					StringBuilder mtit = new StringBuilder();

					while (it.hasNext()) {
						String url = it.next();
						Map<String, String> titles = sent.getUrlTitle();
						String title = titles.getOrDefault(url, "");
						murl.append(url).append("|##|");
						mtit.append(title).append("|##|");
					}

					nextLine = new String[] { sent.getSentenceid(), String.valueOf(sent.getOrder()),String.valueOf(sent.getPorder()), murl.toString(),
							sent.getText(), sent.getHead(), mtit.toString(), String.valueOf(sent.getUrlList().size()),
							sent.getLoc(), sent.getfdate() };

					cwr.writeNext(nextLine);
					cwr2.writeNext(nextLine);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static SentenceDetectorME splittosentence() {
		InputStream inputStream = null;
		String modeldir = args.get("mdir", "/Users/ludab//Downloads/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin");
		try {
			inputStream = new FileInputStream(modeldir);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SentenceModel model = null;
		try {
			model = new SentenceModel(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Instantiating the SentenceDetectorME class
		SentenceDetectorME detector = new SentenceDetectorME(model);
		// String sentences[] = detector.sentDetect(content);
		// Detecting the sentence
		// return detector.sentDetect(content);
		return detector;
	}

	public static String processTEIString(String xmlString, String FileID, String dir, String fdate) {
		String tei = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xmlString)));
			document.getDocumentElement().normalize();
			tei = processCitations(document, FileID, dir, fdate);

			// tei = processTEIDocument(document, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tei;
	}

	/*
	 * This function processes citations in a document. It first creates a map of
	 * URLs to titles, and then it iterates through the document's ref elements. For
	 * each ref element, it checks the element's type attribute. If the type is
	 * foot, the function looks for a corresponding note element with the same
	 * target attribute. If the type is bibr, the function looks for a corresponding
	 * biblStruct element with the same target attribute. In both cases, the
	 * function adds the element's target attribute to the map of URLs to titles.
	 * The function inserts url as text before ref element. Once the function has
	 * processed all of the ref elements, it removes them from the document. Also
	 * renaming figdesc to p Finally, it calls the get_Sentences() function to get a
	 * list of sentences that are relevant to the document's citations.
	 * 
	 * 
	 */
	public static String processCitations(org.w3c.dom.Document document, String FileID, String dir, String fdate) {
		// Element root = document.getDocumentElement();
		Map urltitle = new HashMap();
		Map urlloc = new HashMap();
		LinkExtractor linkExtractor = LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW)).build();
		
		List<Node> delete = new ArrayList<Node>();
		List<Node> rename = new ArrayList<Node>();
		NodeList refList = document.getElementsByTagName("ref");
		int refcount = refList.getLength();
		for (int i = 0; i < refcount; i++) {
			Element refElement = (Element) refList.item(i);
			delete.add(refList.item(i));

			if (refElement.hasAttribute("type")) {
				String type = refElement.getAttribute("type");
				System.out.println(type);
				if (type.equals("foot")) {
					String t = refElement.getAttribute("target");
					System.out.println(t);
					NodeList noteList = document.getElementsByTagName("note");
					int notec = noteList.getLength();
					for (int j = 0; j < notec; j++) {
						Element noteElement = (Element) noteList.item(j);
						if (noteElement.hasAttribute("xml:id")) {
							String id = "#" + noteElement.getAttribute("xml:id");
							if (id.equals(t)) {
								String notetext = "##foot##" + " " + noteElement.getTextContent() + " ";
								if (notetext.endsWith(".")) {
									notetext = notetext.substring(0, notetext.length() - 1);
								}
								Iterable<LinkSpan> links = linkExtractor.extractLinks(notetext);
								Iterator<LinkSpan> it = links.iterator();
								while (it.hasNext()) {
									LinkSpan link = it.next();
									String url = notetext.substring(link.getBeginIndex(), link.getEndIndex());
									urlloc.put(url,"F");
								}
								// notetext = notetext.replace("\n", " ");
								System.out.println(notetext);
								Node n = document.createTextNode(notetext);
								Node p = refElement.getParentNode();
								p.insertBefore(n, refElement);
								// p.removeChild(refElement);

							}

						}
					} // note
				}
				if (type.equals("bibr")) {
					String tt = refElement.getAttribute("target");
					// System.out.println(tt);
					NodeList bibList = document.getElementsByTagName("biblStruct");

					for (int jj = 0; jj < bibList.getLength(); jj++) {
						Element bibElement = (Element) bibList.item(jj);
						if (bibElement.hasAttribute("xml:id")) {
							String id = "#" + bibElement.getAttribute("xml:id");
							System.out.println(id);
							String title = "";
							if (id.equals(tt)) {
								NodeList bib1 = bibElement.getElementsByTagName("title");
								if (bib1 != null) {
									title = bib1.item(0).getTextContent();
									System.out.println(title);
								}
								NodeList bib2 = bibElement.getElementsByTagName("ptr");
								if (bib2 != null) {
									Element el = ((Element) bib2.item(0));
									String url = "";
									if (el != null) {
										urltitle.put(el.getAttribute("target"), title);
										url = "##bib##" + " " + el.getAttribute("target") + " ";
									}
									// String url = bib2.item(0).getTextContent();
									System.out.println(url);
									Node nn = document.createTextNode(url);
									Node pp = refElement.getParentNode();
									pp.insertBefore(nn, refElement);
								}

							}
						}
					}
				} // bibr

			}

		} // refelements

		for (int i = 0; i < delete.size(); i++) {
			Node node = delete.get(i);
			node.getParentNode().removeChild(node);
		}

		// for (Node inner : XMLHelper.getChildNodes(node)) { ... }
		// for (Element e: refList) {
		// e.getParentNode().removeChild(e);
		// }
		NodeList figs = document.getElementsByTagName("figDesc");
		for (int kkk = 0; kkk < figs.getLength(); kkk++) {
			rename.add(figs.item(kkk));
		}

		for (int i = 0; i < rename.size(); i++) {
			Node node = rename.get(i);
			document.renameNode(node, "http://www.tei-c.org/ns/1.0", "p");

		}

		// Element root = document.getDocumentElement();
		// segment(document, root);
		getSentences(document, FileID, dir, urltitle, fdate,urlloc);
		return serialize(document, null);

	}

	public static String serialize(org.w3c.dom.Document doc, Node node) {
		DOMSource domSource = null;
		String xml = null;
		try {
			if (node == null) {
				domSource = new DOMSource(doc);
			} else {
				domSource = new DOMSource(node);
			}
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			if (node != null)
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(domSource, result);
			xml = writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
		}
		return xml;
	}
	
}
