import json
import pandas as pd
import json
import seaborn as sns
import re
import os 

soft_prefixes = [
    'github.com/',
    'sourceforge.net/',
    'bitbucket.org/',
    'gitlab.com/',
    'codeberg.org/',
    'launchpad.net',
    'code.google.com'
    ]
software_array = [r'(https?://github\.com/[^\s]+)',
                r'(https?://sourceforge\.net/[^\s]+)',
                r'(https?://bitbucket\.org/[^\s]+)',
                r'(https?://gitlab\.com/[^\s]+)',
                r'https?://codeberg\.org/[\w\-]+/[\w\-]+',
                r'https?://launchpad\.net/[\w\-]+',
                r'https?://code\.google\.com/[^\s]+)'
                 ]
media_array = [
    r'(?:https?://)?(?:www\.)?youtube\.com/(?:watch\?(?:\S*?&)*v=|v/|embed/|shorts/|playlist\?list=)(\w{11})',
    r'(?:https?:\/\/(?:www\.)?)?vimeo\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?soundcloud\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?twitch\.tv\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?spotify\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?tiktok\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?dailymotion\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?bandcamp\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?mediafire\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?vid\.me\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?8tracks\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?metacafe\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?jwplayer\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?mixcloud\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?viddler\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?veoh\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?brightcove\.com\/[^\s"]*',
    r'(?:https?:\/\/(?:www\.)?)?rumble\.com\/[^\s"]*'
]
datarepo_array = [
    r'https?://archive\.ics\.uci\.edu/ml/.+',
    r'https?://www\.kaggle\.com/.+/datasets/.+',
    r'https?://datasetsearch\.google\.com/dataset/.+',
    r'https?://msropendata\.com/datasets/.+',
    r'https?://www\.data\.gov/dataset/.+',
    r'https?://github\.com/.+/awesome-public-datasets/.+',
    r'https?://registry\.opendata\.aws/.+',
    r'https?://data\.fivethirtyeight\.com/.+',
    r'https?://github\.com/.+/datasets/.+',
    r'https?://data\.worldbank\.org/dataset/.+',
    r'https?://snap\.stanford\.edu/data/.+',
    r'https?://storage\.googleapis\.com/openimages/.+',
    r'https?://www\.ncei\.noaa\.gov/.+',
    r'https?://www\.imdb\.com/interfaces/.+',
    r'https?://ec\.europa\.eu/eurostat/.+',
    r'https?://www\.cs\.cmu\.edu/.+/enron/.+',
    r'https?://labrosa\.ee\.columbia\.edu/millionsong/.+',
    r'https?://www\.who\.int/gho/database/.+'
]

prefixes = [
    'zenodo.org/',
    'doi.org/10.5281/',
    'dx.doi.org/10.5281/',
    'zenodo.org/doi/10.5281/',
    #dryad
    'datadryad.org/stash/dataset/',
    'doi.org/10.5061/',
    'dx.oi.org/10.5061/',
    'doi.org/10.25338/',
    'dx.doi.org/10.25338/',
    #figshare
    'doi.org/10.6084/',
    'dx.doi.org/10.6084/',
    'kcl.figshare.com/',
    'figshare.com/',
    #mendeley
    'doi.org/10.17632/',
    'dx.doi.org/10.17632/',
    'data.mendeley.com/datasets/',
    #DOE BERKELEY
    'doi.org/10.15485',
    'dx.doi.org/10.15485',
    'data.ess-dive.lbl.gov/',
    'doi.org/10.15486',
    'dx.doi.org/10.15486',
    'ngt-data.lbl.gov/dois/',
    #PANGAEA
    'doi.org/10.1594/',
    'dx.doi.org/10.1594/',
    'doi.pangaea.de/10.1594/',
    'doi.org/10.17182/',
    'dx.doi.org/10.17182/',
    'www.hepdata.net/record/',
    'doi.org/10.18141/',
    'dx.doi.org/10.18141/',  
    'edx.netl.doe.gov/dataset',
    'doi.org/10.18434/',
    'dx.doi.org/10.18434/',
    'data.nist.gov/', 
    'doi.org/10.21227/h5e9-m398',
    'dx.doi.org/10.21227/h5e9-m398',
    'ieee-dataport.org/documents/',
    'doi.org/10.2210/',
    'dx.doi.org/10.2210/',
    'www.rcsb.org/structure/',
    'dx.doi.org/10.25505/',
    'doi.org/10.25505/',
    'www.ccdc.cam.ac.uk/structures/',
    'doi.org/10.25919/',
    'dx.doi.org/10.25919/',
    'data.csiro.au/collection/',
    'doi.org/10.26093/',
    'dx.doi.org/10.26093/',
    'cdsarc.cds.unistra.fr/',
    'doi.org/10.48322/', 
    'dx.doi.org/10.48322/', 
    'hpde.io/NASA/NumericalData/RBSP/',
    'doi.org/10.7910/',
    'dx.doi.org/10.7910/',
    'dataverse.harvard.edu/',
    'doi.org/10.7914/',
    'dx.doi.org/10.7914/',
    'www.fdsn.org/networks/'
    
]
# Create the regex pattern
prefix_pattern = '|'.join(re.escape(prefix) for prefix in prefixes)
sprefix_pattern = '|'.join(re.escape(prefix) for prefix in soft_prefixes)
url_pattern = f'(?:https?://(?:{prefix_pattern}))(?:[^\s"]*)'
s_pattern =f'(?:https?://(?:{sprefix_pattern}))(?:[^\s"]*)'
paper_array = [
    r'https?://(?:www\.)?sciencedirect\.com/[^\s"]+',    # sciencedirect.com
    r'https?://(?:www\.)?adsabs\.harvard\.edu/[^\s"]+',  # adsabs.harvard.edu
    r'https?://ui\.adsabs\.harvard\.edu/[^\s"]+',        # ui.adsabs.harvard.edu
    r'https?://link\.aps\.org/[^\s"]+',                  # link.aps.org
    r'https?://stacks\.iop\.org/[^\s"]+',                # stacks.iop.org
    r'https?://(?:www\.)?aclweb\.org/[^\s"]+',           # aclweb.org
    r'https?://journals\.aps\.org/[^\s"]+',              # journals.aps.org
    r'https?://(?:www\.)?nature\.com/[^\s"]+',           # nature.com
    r'https?://link\.springer\.com/[^\s"]+',             # link.springer.com
    r'https?://dl\.acm\.org/[^\s"]+',                    # dl.acm.org
    r'https?://ieeexplore\.ieee\.org/[^\s"]+',           # ieeexplore.ieee.org
    r'https?://(?:www\.)?jstor\.org/[^\s"]+',            # jstor.org
    r'https?://science\.sciencemag\.org/[^\s"]+',        # science.sciencemag.org
    r'https?://papers\.nips\.cc/[^\s"]+',                # papers.nips.cc
    r'https?://onlinelibrary\.wiley\.com/[^\s"]+',       # onlinelibrary.wiley.com
    r'https?://pubs\.acs\.org/[^\s"]+',                  # pubs.acs.org
    r'https?://(?:www\.)?aaai\.org/[^\s"]+',             # aaai.org
    r'https?://refhub\.elsevier\.com/[^\s"]+',           # refhub.elsevier.com
    r'https?://(?:www\.)?imstat\.org/[^\s"]+',           # imstat.org
    r'https?://scitation\.aip\.org/[^\s"]+',             # scitation.aip.org
    r'https?://(?:www\.)?pnas\.org/[^\s"]+',             # pnas.org
    r'https?://aip\.scitation\.org/[^\s"]+',             # aip.scitation.org
    r'https?://proceedings\.neurips\.cc/[^\s"]+',        # proceedings.neurips.cc
    r'https?://linkinghub\.elsevier\.com/[^\s"]+',       # linkinghub.elsevier.com
    r'https?://(?:www\.)?jmlr\.org/[^\s"]+',             # jmlr.org
    r'https?://(?:www\.)?researchgate\.net/[^\s"]+',     # researchgate.net
    r'https?://(?:www\.)?ssrn\.com/[^\s"]+',             # ssrn.com
    r'https?://eprint\.iacr\.org/[^\s"]+',               # eprint.iacr.org
    r'https?://(?:www\.)?tandfonline\.com/[^\s"]+',      # tandfonline.com
    r'https?://archive\.stsci\.edu/[^\s"]+',             # archive.stsci.edu
    r'https?://aclanthology\.org/[^\s"]+',               # aclanthology.org
    r'https?://publish\.aps\.org/[^\s"]+',               # publish.aps.org
    r'https?://(?:www\.)?springerlink\.com/[^\s"]+',     # springerlink.com
    r'https?://dx\.doi\.org/[^\s"]+',                    # dx.doi
    r'https?://(?:www\.)?springer\.com/[^\s"]+',         # springer.com
    r'https?://doi\.wiley\.com/[^\s"]+' ,
    r'https?://doi\.org/[^\s"]+'                    # doi.wiley.com
]

def check_string_match_with_regex(input_string, regex_array):
    for regex in regex_array:
        if re.findall(regex, input_string):
            return 1
    return 0
def check_string_match_pattern(input_string,url_pattern):
    urls = re.findall(url_pattern, input_string)
    if urls:
        return 1
    else:
        return 0  
    
def classifybyregex(text):
    #print(f"Checking: {text}")
    m = check_string_match_with_regex(text, datarepo_array)
    if m == 1:
        return 0
    m = check_string_match_pattern(text, url_pattern)
    if m == 1:
        return 0
    m = check_string_match_pattern(text, s_pattern)
    if m == 1:
        return 1
    m = check_string_match_with_regex(text, media_array)
    if m == 1:
        return 2
    m = check_string_match_with_regex(text, paper_array)
    if m == 1:
        return 3
    else:
        return 4

def read_jsonl_file(input_file):
    with open(input_file, 'r') as f:
        for line in f:
            yield json.loads(line)

def process_data_portions(input_file, output_file, portion_size=1000):
    flattened_data = []
    jsonl_generator = read_jsonl_file(input_file)
    
    for item in jsonl_generator:
        dedup_urls = set(item['urlList'])
        ocount=len(item['urlList'])
        count = len(dedup_urls)  
        
        for url in dedup_urls:
            text = item['text']
            if ocont!=count:
               print(item) 
            tl = len(text)
            title=""
            location="I"
            label = classifybyregex(url)
            #count = len(item['urlList'])
            count = len(dedup_urls)
            url_loc = item.get('urlLoc', {})
            url_title = item.get('urlTitle', {})   
            id = item['sentenceid'].split('_')[0]
            if url in url_loc:  # Check if the URL exists in urlLoc
             location = url_loc[url]  # Get the location value for the URL
            else:
             location = 'I'  # Assign default value if URL not found in urlLoc
            if url in url_title:  # Check if the URL exists in urlLoc
             title = url_title[url]  # Get the location value for the URL
            
        
            data = {
                'document': id,
                'sentenceid': item['sentenceid'],
                'url': url,
                'loc': location,
                'text': item['text'],
                'year': item['fdate'],
                'count': count,
                'txl': tl,
                'title':title,
                'regex_label': label
                
            }
            flattened_data.append(data)

            if len(flattened_data) >= portion_size:
                # Create a DataFrame from the current portion
                df = pd.DataFrame(flattened_data)

                # Append or write the portion to the output file
                mode = 'w' if not os.path.exists(output_file) else 'a'
                df.to_csv(output_file, index=False, mode=mode, header=not os.path.exists(output_file))
                
                # Clear the flattened_data list
                flattened_data = []

    # Create a DataFrame from the remaining data
    df = pd.DataFrame(flattened_data)

    # Append or write the remaining portion to the output file
    mode = 'w' if not os.path.exists(output_file) else 'a'
    df.to_csv(output_file, index=False, mode=mode, header=not os.path.exists(output_file))
def text_converter(value):
    try:
        return str(value).strip().lower()
    except:
        return ''
def data_clean(feature_data):
    feature_data = feature_data.apply(lambda x: text_converter(x))
    #feature_data = feature_data.apply(lambda x: x.lower())
    return feature_data
    
def load_diff_text(df):
    
    different_csv_path = "/Users/ludab/Laptop/project2023/urldiscovery/data/all_3context_urls.csv"
    different_df = pd.read_csv(different_csv_path)
    
    # Update the 'text' column based on the condition that 'sentenceid' is the same
    df['text'] = df.apply(
        lambda row: different_df[different_df['sentenceid'] == row['sentenceid']]['text'].values[0]
        if row['sentenceid'] in different_df['sentenceid'].values else row['text'],
        axis=1
       
    )
    return df 
def remove_urls_c(text):
    # Regular expression to match URLs
    url_pattern = r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+'
    
    # Replace URLs with blanks
    return re.sub(url_pattern, '', text)

def contains_multiple_urls(text):
    urls = re.findall(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', text)
    return len(urls) > 1
    
def redu_labels(): 

    dataset_path = "/Users/ludab/Laptop/project2023/urldiscovery/data/NoV16/all_onecit_urls_dataset2.csv"
    output_file2 = "/Users/ludab/Laptop/project2023/urldiscovery/data/NoV16/all_3context_urls_dataset2.csv"
    data_types = {
            'document' : str,
            'sentenceid': str,
            'url': str,
            'loc' : str,
            'text':  str,  # Replace with the actual column name containing text
            'year': str,
            'count': int,
            'txl':int,
            'label':int,
            'text_without_url':str
            
            #'other_column_name': float,  # Replace with other column names and appropriate data types
            # ...
        }
    
    url_pattern = r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+'
    #df = pd.read_csv(dataset_path,dtype=data_types)
    df = pd.read_csv(dataset_path)
    df["text"] = df["text"].astype(str)
    df["url"] = df["url"].astype(str)
    #df["text"].fillna("", inplace=True)
    print(df.shape)
    print(df.head())  
    #df['text'] = data_clean(df['text'])
    print(df[['text']].head())
    #this change because of change of regex list
    #df['label'] = classifybyregex(df['text'])
    
    ##df['label'] = df['url'].apply(classifybyregex)
    ##df.to_csv(output_file, index=False)  
    
    df['has_U'] = df['text'].str.contains(url_pattern, flags=re.IGNORECASE)
    df = load_diff_text(df)
    groupedb = df.groupby(['regex_label'])
    print(groupedb.size())
   
    filtered_df = df[df['text'].apply(contains_multiple_urls)]
    
    print(filtered_df.shape)
    print(filtered_df.head())  
    grouped = filtered_df.groupby(['regex_label'])
    print(grouped.size())
   
    # Apply remove_url function to 'text' column where 'has_url' is True
    df['text'] = df.apply(lambda row: remove_urls_c(row['text']) if not row['has_U'] else row['text'], axis=1)
    
    df.to_csv(output_file2, index=False)  

if __name__ == '__main__':
    #adjust your own files you want to lebel
    input_file = "/Users/ludab/Laptop/project2023/urldiscovery/data/NoV16/all_onecit_urls.jsonl"
    output_file = "/Users/ludab/Laptop/project2023/urldiscovery/data/NoV16/all_onecit_urls_d.csv"
   
    process_data_portions(input_file, output_file, portion_size=100000)
    #redu_labels() 
