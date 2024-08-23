#!/bin/bash  
#adjust your own classpath
THE_CLASSPATH=./PDFCitationContext/target/urldiscovery-1.0-SNAPSHOT-jar-with-dependencies.jar
# adjust where your grobid library installed
# sdir path to pdfs
java  -classpath $THE_CLASSPATH  gov.lanl.urlcit.ResourceToTei  -grobidhome  /data/summer2023_cit/grobid/grobid-home/  -sdir   /arxiv_data/pdf/
