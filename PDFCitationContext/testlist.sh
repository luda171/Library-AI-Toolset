#!/bin/bash 
# Loop through directories /arxiv_data/pdf/*/                                                                                                                                    
for dir in /arxiv_data/pdf/*/; do
    echo "Directory: $dir"

    # Count the number of *.tei files                                                                                                                                            
    tei_count=$(find "$dir" -maxdepth 1 -type f -name '*.tei' | wc -l)
    echo "Number of *.tei files: $tei_count"

    # Count the number of *.pdf files                                                                                                                                            
    pdf_count=$(find "$dir" -maxdepth 1 -type f -name '*.pdf' | wc -l)
    echo "Number of *.pdf files: $pdf_count"

    # Count the number of url*.jsonl files with size 0                                                                                                                           
    url_jsonl_count=$(find "$dir" -maxdepth 1 -type f -name 'url*.jsonl' -size 0 | wc -l)
    echo "Number of url*.jsonl files with size 0: $url_jsonl_count"
     # Count the number of url*.jsonl files with size 0                                                                                                                         \
                                                                                                                                                                                 


    echo ""
done
