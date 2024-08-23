#!/bin/bash
THE_CLASSPATH=/home/ludab/urldiscovery/target/urldiscovery-1.0-SNAPSHOT-jar-with-dependencies.jar
java      -classpath $THE_CLASSPATH  gov.lanl.urlcit.arxiv.CitParser -hdir /home/ludab/urldiscovery/  -mdir /data/summer2023_cit/models/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin 
   -sdir /arxiv_data/pdf/   -year 2020 
