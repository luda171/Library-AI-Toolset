package gov.lanl.urlcit.arxiv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.grobid.core.engines.Engine;

import gov.lanl.urlcit.InputArgs;

public class MakepdfList {
	static InputArgs args;
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
		  public void LoopFileStructure(String dir,String homedir ) {
	      	//FileWriter writer = null;
	  		//FileWriter writer2 = null;
	  		File fdir = new File(dir);
	  		File[] laurs = fdir.listFiles();
	  		//int startIndex = Integer.parseInt(properties.getProperty("lastProcessedIndex", "0"));
	  		try ( BufferedWriter writer = new BufferedWriter(new FileWriter("tei_dir.txt"))){
	  			//writer = new FileWriter(homedir + "/all_onecit_urls.jsonl");
	  			//writer2 = new FileWriter(homedir + "/all_3context_urls.jsonl");
	  			
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
	  				String pp = pdf.getParent();
	  				try {
	  					String year = 20+nid.substring(0,2);
	  					writer.write(year+","+ pp+"/" + nid + ".pdf"); // Save the directory path
						writer.newLine();
	  					
	  					
	  					 //combine_with_citing_csv(pp + "/" + "urlsent" + nid + ".csv", writer,writer2,nid);
	  				     writer.flush();
	  				     //writer2.flush();
	  				} catch (Exception e) {
	  					
	  					e.printStackTrace();
	  				}
	  				
	  			}
	  		}
	  		} catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		} finally {
	  			
	  			}
	  	}

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
					// eng= init_grobid(pGrobidHome);
				}
				//CitParser r = new CitParser();
				 MakepdfList r = new  MakepdfList ();
				r.LoopFileStructure(dir,hdir);
				// r.LoopFileStructure(dir);

			}
}
