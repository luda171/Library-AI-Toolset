package gov.lanl.urlcit.arxiv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.lanl.urlcit.InputArgs;
import gov.lanl.urlcit.Sentence;

public class Combine_corpus {
	static InputArgs args;

	
 
	public static String[] createStringArrayFromList(List<String> list) {
		  String[] stringArray = new String[list.size()];

		  for (int i = 0; i < list.size(); i++) {
		    stringArray[i] = list.get(i);
		  }

		  return stringArray;
		}
	public static void test(String[] margs) {
	
		Map <String, Integer> dp  = new HashMap();
		List<String> list = Arrays.asList("1901.03697v1.pdf", "1901.03697v2.pdf",
				  "1901.03697v2.pdf", "1901.03697v3.pdf", "1901.03697v3.tei",
				  "1901.03698v1", "1901.03698v1", "1901.03699v1",
				  "1901.03699v1", "1901.03700v1", "1901.03700v1",
				  "1901.03701v1", "1901.03701v1");
		String[] pdfs = createStringArrayFromList(list);
		for (int j = 0; j < pdfs.length; j++) {
				//File pdf = pdfs[j];
				String pdfn = pdfs[j];
				//if (!pdfn.contains("pdf")) continue;
				String[] f1Parts = pdfn.split("v");
			    //if(!dpdfs.contains(f1Parts[0] )) {
				//dpdfs.add(f1Parts[0]);}
				int v1 = Integer.parseInt(f1Parts[1]);
				if (dp.containsKey(f1Parts[0])) {
					int v = (int) dp.get(f1Parts[0]);
					if (v1>v) dp.put(f1Parts[0],Integer.parseInt(f1Parts[1]));
				}
				else {
					dp.put(f1Parts[0],Integer.parseInt(f1Parts[1]));
				}
				
    	   }
    	   
    	   for (Map.Entry entry : dp.entrySet()) {
    	        System.out.println(entry.getKey() + ":" + entry.getValue());
    	    }
	}
	
          /* public List<String>  filter_latestversion (File[] pdfs) {
        	   
               List dpdfs = new ArrayList();
               Map <String, Integer> dp  = new HashMap();
               
        	   for (int j = 0; j < pdfs.length; j++) {
   				File pdf = pdfs[j];
   				String pdfn = pdf.getName();
   				if (!pdfn.contains(".pdf")) continue;
   				pdfn=pdfn.replace(".pdf", "");
   				String[] f1Parts = pdfn.split("v");
   			    //if(!dpdfs.contains(f1Parts[0] )) {
   				//dpdfs.add(f1Parts[0]);}
   				int v1 = Integer.parseInt(f1Parts[1]);
   				if (dp.containsKey(f1Parts[0])) {
   					int v = (int) dp.get(f1Parts[0]);
   					if (v1>v) dp.put(f1Parts[0],Integer.parseInt(f1Parts[1]));
   				}
   				else {
   					dp.put(f1Parts[0],Integer.parseInt(f1Parts[1]));
   				}
   				
        	   }
        	   
        	   for (Map.Entry entry : dp.entrySet()) {
        	        System.out.println(entry.getKey() + ":" + entry.getValue());
        	        dpdfs.add(entry.getKey()+"v"+entry.getValue());
        	    }
        	   
        	           	   
        	   return dpdfs;
        	   }
        	   
        	 */
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
        	   
           
            
            public void LoopFileStructure(String dir,String homedir ) {
            	FileWriter writer = null;
        		FileWriter writer2 = null;
        		File fdir = new File(dir);
        		File[] laurs = fdir.listFiles();
        		try {
        			writer = new FileWriter(homedir + "/all_onecit_urls.jsonl");
        			writer2 = new FileWriter(homedir + "/all_3context_urls.jsonl");
        			
        		// laurs for arxiv would be 0704 dirs
        		for (int i = 0; i < laurs.length; i++) {

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
        				try {
        					 //String pp = pdf.getAbsolutePath();
        					 String pp = pdf.getParent();
        					//String tei = readFile(pdft, StandardCharsets.UTF_8);
        					//String fid = pdfn.replace(".tei", "");
        					//System.out.println(fid);
        					//String ntei = processTEIString(tei, fid, p);
        					 combine_with_citing(pp + "/" + "urlsent" + nid + ".jsonl", writer,writer2);
        				     writer.flush();
        				     writer2.flush();
        				} catch (Exception e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        			}
        		}
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} finally {
        			try {
        				writer.close();

        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        			try {
        				writer2.close();

        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}}
        	}

	
	public static void main(String[] margs) {
		// TODO Auto-generated method stub
		Combine_corpus r = new Combine_corpus();
		args = new InputArgs(margs);
		String dir = args.get("sdir", "/datasets/rassti-dataset/2022-01-20");
		String homedir = args.get("homedir", "/datasets/rassti-dataset/2022-01-20");

		r.LoopFileStructure(dir, homedir);
		
		
	}
	public static void _main(String[] margs) {
		
		String urlist = "https://github.com/AhmedElkelesh/Genetic-Algorithm-based-Polar-Code-Construction|##|https://www.cosmos.esa.int/web/ gaia/dpac/consortium";
		
		String[] parts = urlist.split("\\|##\\|");	
		for (int i=0; i< parts.length;i++) {
        	String url=parts[i];
        	System.out.println(url);
        }
	}
	/*
	public void combine_with_citing_csv(String filename, FileWriter writer,FileWriter writer2,String nid) {
		// Reading JSON Lines
		//ObjectMapper objectMapper = new ObjectMapper();
	
		String year = 20+nid.substring(0,2);
		
		
		String fid = filename.replace("url", "all");
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				
				CSVParser parse = CSVParser.parse(line, CSVFormat.DEFAULT);
				CSVRecord record = parse.getRecords().get(0);
				Sentence urlsent = new Sentence();
				urlsent.setDate(year);
				//System.out.println("id:"+record.get(0));
				urlsent.setSentenceid(record.get(0));
				
				urlsent.setOrder(Integer.parseInt(record.get(1)));
				//System.out.println("1:"+record.get(1));
				//bug
				//urlsent.setPorder(Integer.parseInt(record.get(2)));
				//System.out.println("2:"+record.get(2));
				
				String urlist = record.get(2);
				
				String[] parts = urlist.split("\\|##\\|");
				String text = record.get(3);
				//System.out.println(text);
				urlsent.setText(text);
				int urlcount = Integer.parseInt(record.get(6));
				String head = record.get(4);
		        String title = record.get(5);
		        String[] tit = title.split("\\|##\\|");
		        for (int i=0; i< urlcount;i++) {
		        	String url=parts[i];
		        	String titl = tit[i];
		        	urlsent.setUrl(url);
		        	urlsent.setTitle(titl, url);
		        }
		       
		        urlsent.setHead(head);
		        
				//Sentence urlsent = objectMapper.readValue(line, Sentence.class);
				int sentorder = urlsent.getOrder();
				//writer.write(line+ "\n");
				  String js = urlsent.toJson();
				  writer.write(js+ "\n");
				  combine_with_leading_trailing(fid,  writer2, sentorder, urlsent);
				//System.out.println(data.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
	public void combine_with_citing(String filename, FileWriter writer,FileWriter writer2) {
		// Reading JSON Lines
		ObjectMapper objectMapper = new ObjectMapper();
		String fid = filename.replace("url", "all");
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				Sentence urlsent = objectMapper.readValue(line, Sentence.class);
				//int targetPorder = urlsent.getPorder();
				int targetPorder = urlsent.getOrder();
				String context = context(fid,  urlsent );
				writer.write(line+ "\n");
				//combine_with_paragraph(fid,  writer2, targetPorder, urlsent);
				combine_with_context(fid,  writer2, targetPorder, urlsent);
				//System.out.println(data.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*public void combine_with_paragraph(String filename, FileWriter writer,  int targetPorder,Sentence sent ) {
		// Reading JSON Lines
		String combinedText = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				Sentence csent = objectMapper.readValue(line, Sentence.class);
				int porder = csent.getPorder();
				
				   if (porder == targetPorder) {
					   String text = csent.getText();
					   combinedText += text + " ";

				   }
				  
				//System.out.println(data.toString());
			}
			  sent.setText(combinedText);
			  String js = sent.toJson();
			  writer.write(js+ "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	public void combine_with_context(String filename, FileWriter writer,  int targetPorder,Sentence sent ) {
		// Reading JSON Lines
		String combinedText = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			int so =sent.getOrder();
			while ((line = reader.readLine()) != null) {
				Sentence csent = objectMapper.readValue(line, Sentence.class);
				int sorder = csent.getOrder();
				   if (so>0) {
				   if (sorder == so-1) {
					   String text = csent.getText();
					   combinedText += text + " ";

				   }}
				   if (sorder == so) {
					   String text = csent.getText();
					   combinedText += text + " ";

				   }
				   if (sorder == so+1) {
					   String text = csent.getText();
					   combinedText += text + " ";

				   }
				//System.out.println(data.toString());
			}
			  sent.setText(combinedText);
			  String js = sent.toJson();
			  writer.write(js+ "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String context(String filename, Sentence sent ) {
		// Reading JSON Lines
		String combinedText = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			int so =sent.getOrder();
			while ((line = reader.readLine()) != null) {
				Sentence csent = objectMapper.readValue(line, Sentence.class);
				int sorder = csent.getOrder();
				   if (so>0) {
				   if (sorder == so-1) {
					   String text = csent.getText();
					   combinedText += text + " ";
				   }}
				   if (sorder == so) {
					   String text = csent.getText();
					   combinedText += text + " ";
				   }
				   if (sorder == so+1) {
					   String text = csent.getText();
					   combinedText += text + " ";
				   }
				//System.out.println(data.toString());
			}
			  //sent.setText(combinedText);
			  //String js = sent.toJson();
			  //writer.write(js+ "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return combinedText;
	}
	/*
	public void combine_with_leading_trailing(String filename, FileWriter writer,  int targetPorder,Sentence sent ) {
		// Reading JSON Lines
		String combinedText = "";
		ObjectMapper objectMapper = new ObjectMapper();
		//FileReader reader = new FileReader(filename);
		
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
		
			String line;
						
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				CSVParser parse = CSVParser.parse(line, CSVFormat.DEFAULT);
				CSVRecord record = parse.getRecords().get(0);
				int a = record.size();
				//System.out.println(a);
				int porder = Integer.parseInt(record.get(1));
				//Sentence csent = objectMapper.readValue(line, Sentence.class);
				//int porder = csent.getPorder();
				
				   if (porder == targetPorder-1) {
					   if (a==8) {
					   String text = record.get(4);
					   combinedText += text + " ";
					   //correction
					   sent.setPorder(Integer.parseInt(record.get(2)));
					   }
					   else {
						   String text = record.get(3);
						   combinedText += text + " ";
						  
					   }
				   }
                   if (porder == targetPorder) {
					   //different index since more fields in all...
                	  
					   String text = record.get(3);
					   combinedText += text + " ";
					   //correction
					   //sent.setPorder(Integer.parseInt(record.get(2)));
				   }
                  if (porder == targetPorder+1) {
                	  if (a==8) {
					   String text = record.get(4);
					   combinedText += text + " ";
                	  }
                	  else {
                		  String text = record.get(3);
						   combinedText += text + " ";
                	  }
				   }
				//System.out.println(data.toString());
			}
			//System.out.println(combinedText);
			  sent.setText(combinedText);
			  String js = sent.toJson();
			  writer.write(js+ "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
}
