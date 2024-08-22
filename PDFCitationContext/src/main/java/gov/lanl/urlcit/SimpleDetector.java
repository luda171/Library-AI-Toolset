package gov.lanl.urlcit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVWriter;



public class SimpleDetector {

	static InputArgs args;
	String[] dataset = {"data","dataset", "datasheet","record","spreadsheet"};
	String[] dataset_context = {"results","set", "analysis","archive","analytics",
			"repository","values","numbers","statistics","features","model","points"};
	
	String[] software = {"app ","freeware","network","shareware", "operating system","software",
			"database","code","codebase","source-code","programming","computer","plug-in","file management","algorithm","linux", "windows","C/C++" };
	
	String[] softwarecontext = {"task","process", "batch","script","binary","subroutine","function","procedure",
			"package","driver","platform","framework","browser","server","program","programme","cyber","application","network"};	
	
		public static void main(String[] margs) { 
			args = new InputArgs( margs );
			String dir = args.get("sdir", "/datasets/rassti-dataset/2022-01-20");
			String homedir = args.get("homedir", "/datasets/rassti-dataset/2022-01-20");
			
		    File fdir = new File(dir);
		    File[] laurs = fdir.listFiles();
		
		    SimpleDetector r= new SimpleDetector();  
		    CSVWriter cwr = null;
			File resfile = new File(homedir + "/allsimplurls.csv");
			try {
		    cwr = new CSVWriter(new FileWriter(resfile));
		    
		for (int i=0;i<laurs.length;i++) {
			
			File ldir=laurs[i];
			String id = ldir.getName();
			System.out.println(id);
			String p = ldir.getAbsolutePath();
			System.out.println(p);
			//File f = new File(p+"/"+id+".tei");
			try {
			r.simple_test(id,p+"/"+"urlsent" + id + ".csv" ,  cwr);
			}
		    catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }	
				//String tei= r.readFile(p+"/"+id+".tei", StandardCharsets.UTF_8);
				//String tei=r.runGrobid(f);
				//String ntei = processTEIString(tei,id,p);
			//String tei = tei = eng.fullTextToTEI(f, GrobidAnalysisConfig.defaultInstance());
			// Files.write(Paths.get(p+ "/"+id+".tei" ), tei.getBytes("UTF8"));
			// Files.writeString(Paths.get(p+ "/"+id+".tei" ), tei);
		}
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				try {
					cwr.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
		
		}
		static String readFile(String path, Charset encoding)
				  throws IOException
				{
				  byte[] encoded = Files.readAllBytes(Paths.get(path));
				  return new String(encoded, encoding);
				}
		
		public static String stringContainsItemFromList(String inputStr, String[] items)
		{
			
		    for(int i =0; i < items.length; i++)
		    {
		        if(inputStr.contains(items[i]))
		        {
		            return "Y";
		        }
		    }
		    return "N";
		}
		
		public void simple_test(String id, String Fname, CSVWriter cwr){
			
			CSVParser csvParser = null;
			try {
				FileReader reader = new FileReader(Fname);
				csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT);

				for (CSVRecord record : csvParser) {
			        String sid = record.get(0).trim();
			        String url = record.get(3).trim().toLowerCase();
			        String text = record.get(4).toLowerCase();
			        String head = record.get(5).toLowerCase();
			        String title = record.get(6).toLowerCase();
			        //String c01 =stringContainsItemFromList(url, dataset);
			        //String c02 =stringContainsItemFromList(url, dataset_context);
			        //String c11 = stringContainsItemFromList(text, dataset);
			        //String c12 = stringContainsItemFromList(text, dataset_context);
			        //String c21 = stringContainsItemFromList(head, dataset);
			        //String c22 = stringContainsItemFromList(head, dataset_context);
			        //String c31 = stringContainsItemFromList(title, dataset);
			        //String c32 = stringContainsItemFromList(title, dataset_context);
			        
			       // String p01 =stringContainsItemFromList(url, software);
			        //String p02 =stringContainsItemFromList(url, softwarecontext);
			        //String p11 = stringContainsItemFromList(text, software);
			        //String p12 = stringContainsItemFromList(text, softwarecontext);
			        //String p21 = stringContainsItemFromList(head, software);
			        //String p22 = stringContainsItemFromList(head, softwarecontext);
			        //String p31 = stringContainsItemFromList(title, software);
			        //String p32 = stringContainsItemFromList(title, softwarecontext);
			        String nextLine[] = {id,sid,url,record.get(4),record.get(5),record.get(6) };
	                   
			        //String nextLine[] = {id,sid,url,c01,c02,c11,c12,c21,c22,c31,c32,p01,p02,p11,p12,p21,p22,p31,p32,record.get(4),record.get(5),record.get(6) };
                    cwr.writeNext(nextLine);
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			finally {
				try {
					csvParser.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}                  //0                                     1		                       2        	    3              4             5               6
		  //String nextLine[] = {sent.getSentenceid(), String.valueOf(sent.getOrder()),String.valueOf(sent.getPorder()),sent.getUrl(), sent.getText(), sent.getHead(),sent.getTitle() };

		}
	
public  void dataset_words(){
	String[] dataset = {"data","dataset", "datasheet","record","spreadsheet"};
 }
public  void software_topic_words(){
	String[] dataset = {"program","programme", "app","application",
			"freeware","network","shareware", "operating system","software",
			"database","code","codebase","sourcecode","programming","cyber","computer","plug-in","file management","algorithm"};
 }
public void software_context_words() {
	String[] dataset = {"task","process", "batch","script","binary","subroutine","function","procedure",
			"package","driver","platform","framework","browser","server"};
}

public void dataset_context_words() {
	String[] dataset = {"results","set", "analysis","archive","analytics", "repository","values","numbers","statistics","features","model","points"};
}





}
