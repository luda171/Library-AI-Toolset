
## to compile use maven : mvn clean install

gov.lanl.urlcit.ResourceToTei.java
The processpdf.sh script can be run as follows to convert PDFs to TEI:
nohup ./processpdf.sh >out.txt  &
The TEI file will have the same name as the PDF file, but with the TEI extension. For example, the TEI file for the PDF file 1901.00033v1.pdf would be called 1901.00033v1.tei. 
the LoopFileStructure(String dir) has logic to loop through directoris, adjust for different directory structure.

###gov.lanl.urlcit.CitParser.java

the extracttei.sh script to run CitParser.java to extract sentences from TEI files.
This program creates two CSV files for each TEI file: one with the prefix urlsent and one with the prefix allsent. The urlsent CSV file contains lines with sentences that cite URLs, and the allsent CSV file contains lines with all of the sentences in the TEI file. 
The program iterates through the document's p elements, which represent  paragraphs. For each paragraph program gets text content and then it splits to sentences using nlp model.
the csv file has fields:
Sentenceid, sentenceOrder, murl, sentenceText,sentencenearestsectiontitle, mtit, <how many urls in sentence or urlcount
murl = multiple urls with delimeter "|##|"
mtit = multiple titles of url reference  from bib section with delimeter "|##|".

Example of csv record:
"1901.00001v1_11","11","https://www.tesla.com/autopilot|##|","Despite the availability of the proprietary (like Daimler AG , Tesla  https://www.tesla.com/autopilot , etc.) and open-source dat
asets of traffic road conditions (like Cityscapes , KITTI , CamVid , DUS , etc.), the lack of sample datasets for specific application regions hardens the model tuning stage.","","Autopilot: 
Full Self-Driving Hardware on All Cars|##|","1"



### bulk conversion to TEI  using grobid utility
to test you also can use this tool
go  to /data/summer2023_cit/grobid
run coomand:
java -Xmx1024m -jar grobid-core/build/libs/grobid-core-0.8.0-SNAPSHOT-onejar.jar -gH ./grobid-home/ -gP ./grobid-home/config/grobid.properties -dIn  /arxiv_data/pdf/0704  -dOut ./out -exe processFullText