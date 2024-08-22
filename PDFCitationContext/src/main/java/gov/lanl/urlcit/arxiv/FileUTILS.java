package gov.lanl.urlcit.arxiv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FileUTILS {
	private static Properties properties;
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
}
