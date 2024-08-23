# The utilities  for the bulk conversion of pdf to tei and  extraction of sentences with urls.  
## to compile use maven : 
mvn clean install

### Convert from PDF to TEI
The processpdf.sh script can be run as follows to convert PDFs to TEI:
nohup ./processPDFtoTEI.sh > out.txt  &
The TEI file will have the same name as the PDF file, but with the TEI extension. For example, the TEI file for the PDF file 1901.00033v1.pdf would be called 1901.00033v1.tei. 
the LoopFileStructure(String dir) has logic to loop through directoris, adjust for different directory structure.

### Extract Sentences

The extracttei.sh script runs the CitParser.java program to extract sentences from TEI files. This program generates two JSONL files for each TEI file processed: one with the prefix urlsent and the other with the prefix allsent.

* urlsent.jsonl: Contains lines with sentences that include URLs.
* allsent.jsonl: Contains lines with all sentences extracted from the TEI file.

The program iterates through the document's <p> elements, which represent paragraphs in the TEI schema. For each paragraph, it retrieves the text content and splits it into sentences using an NLP model.
the JSONL file serializes Sentence class to json fields, example of JSON line:
```

{"order":39,"url":"https://www.ngdc.noaa.gov/mgg/global/etopo5.HTML","text":"and  implement the Metropolis-adjusted Langevin kernel discussed in  https://www.ngdc.noaa.gov/mgg/global/etopo5.HTML  in an MCMC routine for LGCPs, making use of circulant embedding , which leverages fast Fourier transforms to speed up matrix computations.","title":"National Geophysical Data Center NOAA. Data announcement 88-mgg-02, digital relief of the surface of the earth","head":"","sentenceid":"2111.15670v1_39","porder":6,"loc":"B","fdate":"202
1","urlList":["https://www.ngdc.noaa.gov/mgg/global/etopo5.HTML"],"urlLoc":{"https://www.ngdc.noaa.gov/mgg/global/etopo5.HTML":"B"},"urlTitle":{"https://www.ngdc.noaa.gov/mgg/global/etopo5.HTML":"National Geophysical Data Center NOAA. Data announcement 88-mgg-02, digital relief of the surface of the earth"}}
```
Multiple urls would be presented as list. 
if url has title in bibliography, it will   

### Bulk conversion to TEI  using genue grobid utility
to test you also can use this grobid tool directly.
go  to directory where grobid library installed {}/grobid (see grobid manual)
example of comand:
```
java -Xmx1024m -jar grobid-core/build/libs/grobid-core-0.8.0-SNAPSHOT-onejar.jar -gH ./grobid-home/ -gP ./grobid-home/config/grobid.properties -dIn  /arxiv_data/pdf/0704  -dOut ./out -exe processFullText
```
