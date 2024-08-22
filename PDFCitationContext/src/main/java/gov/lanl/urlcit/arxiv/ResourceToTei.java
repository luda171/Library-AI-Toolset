package gov.lanl.urlcit.arxiv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.grobid.core.engines.Engine;
import org.grobid.core.engines.config.GrobidAnalysisConfig;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.main.LibraryLoader;
import org.grobid.core.utilities.GrobidProperties;
import gov.lanl.urlcit.InputArgs;

public class ResourceToTei {

	static InputArgs args;
	private static Properties properties;
	private int start = -1;
	private int end = -1;
//this class converts pdf to tei and writes tei to same directory where pdfs are and uses pdf name with tei extention 
	
	public static Engine init_grobid(String pGrobidHome) {
		// LibraryLoader.load();
		// try {
		// String pGrobidHome = "/Users/ludab/Laptop/project2023/grobid/grobid-home/";

		// The GrobidHomeFinder can be instantiate without parameters to verify the
		// grobid home in the standard
		// location (classpath, ../grobid-home, ../../grobid-home)

		// If the location is customised:
		GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(Arrays.asList(pGrobidHome));

		// The grobid yaml config file needs to be instantiate using the correct
		// grobidHomeFinder or it will use the default
		// locations
		GrobidProperties.getInstance(grobidHomeFinder);

		// System.out.println(">>>>>>>> GROBID_HOME=" +
		// GrobidProperties.getGrobidHome());
		// Engine engine = GrobidFactory.getInstance().getEngine();
		Engine engine = GrobidFactory.getInstance().createEngine();

		// Biblio object for the result
		// BiblioItem resHeader = new BiblioItem();
		// fullTextToTEI(java.io.File inputFile, GrobidAnalysisConfig config)

		/*
		 * @param consolidate the consolidation option allows GROBID to exploit Crossref
		 * web services for improving header information
		 * 
		 * @param htmlFormat if the result has to be formatted to be displayed as html.
		 * 
		 * @param startPage give the starting page to consider in case of segmentation
		 * of the PDF, -1 for the first page (default)
		 * 
		 * @param endPage give the end page to consider in case of segmentation of the
		 * PDF, -1 for the last page (default)
		 * 
		 * @param generateIDs if true, generate random attribute id on the textual
		 * elements of the resulting TEI
		 */
		final boolean consolidate = false;
		GrobidAnalysisConfig config =

				GrobidAnalysisConfig.builder()
						// .consolidateHeader(consolidate)
						// .consolidateCitations(true)
						.startPage(-1).endPage(-1).generateTeiIds(true)
						// .withSentenceSegmentation(true)
						// .generateTeiCoordinates(true)
						.build();
		return engine;

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
	public Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
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
	//this can be customized depends on how pdfs  stored
	// this can be customized depends on how pdfs stored
		  public void LoopFileStructure(String dir,String homedir,Engine eng,String pyear ) {
	      	//FileWriter writer = null;
	  		//FileWriter writer2 = null;
	  		File fdir = new File(dir);
	  		File[] laurs = fdir.listFiles();
	  		int startIndex = Integer.parseInt(properties.getProperty("lastProcessedIndex", "0"));
	  		//try ( BufferedWriter writer = new BufferedWriter(new FileWriter("directories_without_tei.txt"))){
	  			try {
	  			//writer = new FileWriter(homedir + "/all_onecit_urls.jsonl");
	  			//writer2 = new FileWriter(homedir + "/all_3context_urls.jsonl");
	  			
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
	  				if (!pdfn.contains(".pdf")) continue;
	  				String nid = pdfn.replace(".pdf", "");
	  				if (!fpdfs.contains(nid)) continue;
	  				System.out.println(pdfn);
	  				String pp = pdf.getParent();
	  				try {
	  					
	  					//String tei = readFile(pdft, StandardCharsets.UTF_8);
	  					//String fid = pdfn.replace(".tei", "");
	  					//System.out.println(fid);
	  					//String ntei = processTEIString(tei, fid, p);
	  					File f = new File(pp + "/" + nid + ".tei");
	  					if(f.exists()) continue; 
	  					//String tei = readFile(pp + "/" + nid + ".tei", StandardCharsets.UTF_8);
	  					//if (tei==null) {
						//	writer.write(p); // Save the directory path
						//	writer.newLine();
						//	continue;
						//}
	  					 String year = 20+id.substring(0,2);
	  					 if (!year.equals(pyear)) continue;
	  					 String pdft=pdf.getAbsolutePath().replace(".pdf", ".tei");
	  					 String tei = eng.fullTextToTEI(pdf, GrobidAnalysisConfig.defaultInstance());
	  					 System.out.println(tei);
	  					 Path ppp = Paths.get(pdft);
	  					 Files.write(ppp, tei.getBytes("UTF8"));
	  					 
	  					//String ntei = processTEIString(tei, nid, pp, year);
	  					 //combine_with_citing_csv(pp + "/" + "urlsent" + nid + ".csv", writer,writer2,nid);
	  				     //writer.flush();
	  				     //writer2.flush();
	  				} catch (Exception e) {
	  					//writer.write(pp+"/" + nid + ".tei"); // Save the directory path
						//writer.newLine();
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

/*	public void LoopFileStructure(String dir,Engine eng) {
		File fdir = new File(dir);
		File[] laurs = fdir.listFiles();

		
		//laurs for arxiv would be 0704 dirs
		for (int i = 0; i < laurs.length; i++) {

			File ldir = laurs[i];
			String id = ldir.getName();
			System.out.println(id);
			String p = ldir.getAbsolutePath();
			System.out.println(p);
			File pdfdir= new File(p);
					
			File[] pdfs =pdfdir.listFiles();
			
			//we may need to filter versions
			for (int j = 0; j < pdfs.length; j++) {
				File pdf = pdfs[j];	
				String pdfn = pdf.getName();
				
				if (pdfn.contains("tei")) continue;
				if (!pdfn.contains("pdf")) continue;
				int g = pdfn.lastIndexOf("v");
				
				System.out.println(pdfn);
			try {
				 String pdft=pdf.getAbsolutePath().replace(".pdf", ".tei");
				 String tei = eng.fullTextToTEI(pdf, GrobidAnalysisConfig.defaultInstance());
				 Path pp = Paths.get(pdft);
				 Files.write(pp, tei.getBytes("UTF8"));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
	}
	*/
		  
		  
/*java  -classpath $THE_CLASSPATH  gov.lanl.urlcit.ResourceToTei  -grobidhome 
 *  /home/ludab/grobid/grobid-home/  -sdir /datasets/rassti-dataset/2022-01-20/
 */
	public static void main(String[] margs) {
		args = new InputArgs(margs);		
		String dir = args.get("sdir", "/datasets/rassti-dataset/2022-01-20");		
		String pGrobidHome=args.get("grobidhome","/Users/ludab/Laptop/project2023/grobid/grobid-home/");		
		String hdir = args.get("hdir", "/datasets/rassti-dataset/2022-01-20");
	    String pyear = args.get("year", "2020");				
		Engine eng= init_grobid(pGrobidHome);
		ResourceToTei r = new ResourceToTei();
		// /arxiv_data/pdf/0704/*.pdf
		properties = new Properties();
		r.loadProperties(hdir);
		r.LoopFileStructure(dir,hdir,eng,pyear);
		//r.LoopFileStructure(dir,eng);
		
	}
	
	//use to test at with one file 
	public static String parse_to_tei(String pdfPath) {
		String tei = "";
		try {
			// LibraryLoader.load();
			String pGrobidHome = "/Users/ludab/Laptop/project2023/grobid/grobid-home/";

			// The GrobidHomeFinder can be instantiate without parameters to verify the
			// grobid home in the standard
			// location (classpath, ../grobid-home, ../../grobid-home)

			// If the location is customised:
			GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(Arrays.asList(pGrobidHome));

			// The grobid yaml config file needs to be instantiate using the correct
			// grobidHomeFinder or it will use the default
			// locations
			GrobidProperties.getInstance(grobidHomeFinder);

			System.out.println(">>>>>>>> GROBID_HOME=" + GrobidProperties.getGrobidHome());

			Engine engine = GrobidFactory.getInstance().createEngine();

			// Biblio object for the result
			// BiblioItem resHeader = new BiblioItem();
			// fullTextToTEI(java.io.File inputFile, GrobidAnalysisConfig config)

			/*
			 * @param consolidate the consolidation option allows GROBID to exploit Crossref
			 * web services for improving header information
			 * 
			 * @param htmlFormat if the result has to be formatted to be displayed as html.
			 * 
			 * @param startPage give the starting page to consider in case of segmentation
			 * of the PDF, -1 for the first page (default)
			 * 
			 * @param endPage give the end page to consider in case of segmentation of the
			 * PDF, -1 for the last page (default)
			 * 
			 * @param generateIDs if true, generate random attribute id on the textual
			 * elements of the resulting TEI
			 */
			final boolean consolidate = false;
			GrobidAnalysisConfig config =

					GrobidAnalysisConfig.builder()
							// .consolidateHeader(consolidate)
							// .consolidateCitations(true)
							.startPage(-1).endPage(-1).generateTeiIds(true)
							// .withSentenceSegmentation(true)
							// .generateTeiCoordinates(true)
							.build();
			File f = new File(pdfPath);
			tei = engine.fullTextToTEI(f, GrobidAnalysisConfig.defaultInstance());
			// tei = tei.replace("\n", "");
			// Document doc =
			// engine.fullTextToTEIDoc(f,GrobidAnalysisConfig.defaultInstance());

			// System.out.println(tei);
		} catch (Exception e) {
			// If an exception is generated, print a stack trace
			e.printStackTrace();
		}

		return tei;
	}
	/**
	 * 
	 * Call the Grobid full text extraction service on server.
	 *
	 * @param pdfBinary InputStream of the PDF file to be processed
	 * @return the resulting TEI document as a String or null if the service failed
	 */
	public String runGrobid(File pdfFile) {
		String tei = null;
		HttpURLConnection conn = null;
		String host = args.get("grobidhost", "/datasets/rassti-dataset/2022-01-20");
		String port = args.get("grobidport", "/datasets/rassti-dataset/2022-01-20");
		String baseurl = args.get("baseurl", "/datasets/rassti-dataset/2022-01-20");
		String mname = args.get("grobidmname", "/datasets/rassti-dataset/2022-01-20");
		String sleeptime = args.get("sleeptime", "/datasets/rassti-dataset/2022-01-20");
		System.out.println(host);
		try {
			// URL url = new URL("https://" + host
			// + (port.isEmpty() ? "" : ":" + port) + "/api/" + mname);
			URL url = new URL(baseurl);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxyout.lanl.gov", 8080));

			// conn = (HttpURLConnection) url.openConnection(proxy);
			conn = (HttpURLConnection) url.openConnection();
			// conn.setConnectTimeout(TIMEOUT_VALUE);
			// conn.setReadTimeout(TIMEOUT_VALUE);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			FileBody fileBody = new FileBody(pdfFile);
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
			multipartEntity.addPart("input", fileBody);

			if (start != -1) {
				StringBody contentString = new StringBody("" + start);
				multipartEntity.addPart("start", contentString);
			}
			if (end != -1) {
				StringBody contentString = new StringBody("" + end);
				multipartEntity.addPart("end", contentString);
			}
			// multipartEntity.addPart("consolidateHeader", new StringBody("1"));
			// multipartEntity.addPart("consolidateCitations", new StringBody("1"));
			conn.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
			OutputStream out = conn.getOutputStream();
			try {
				multipartEntity.writeTo(out);
			} finally {
				out.close();
			}

			if (conn.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
				// error 503 corresponds to the case all the treads in the GROBID thread pool
				// are
				// used, we will need to wait a bit and re-send the query
				throw new HttpRetryException("Failed : HTTP error code : " + conn.getResponseCode(),
						conn.getResponseCode());
			}

			if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
				tei = "";
			} else if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " "
						+ IOUtils.toString(conn.getErrorStream(), "UTF-8"));
			} else {
				InputStream in = conn.getInputStream();
				tei = IOUtils.toString(in, "UTF-8");
				System.out.println(tei);
				IOUtils.closeQuietly(in);
			}
		} catch (ConnectException e) {
			// logger.error(e.getMessage(), e.getCause());
			try {
				Thread.sleep(20000);
				runGrobid(pdfFile);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		} catch (HttpRetryException e) {
			// logger.error(e.getMessage(), e.getCause());
			try {
				Thread.sleep(Integer.parseInt(sleeptime));
				runGrobid(pdfFile);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		} catch (SocketTimeoutException e) {
			// throw new GrobidTimeoutException("Grobid processing timed out.");
		} catch (MalformedURLException e) {
			// logger.error(e.getMessage(), e.getCause());
		} catch (IOException e) {
			// logger.error(e.getMessage(), e.getCause());
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return tei;
	}
    

}
