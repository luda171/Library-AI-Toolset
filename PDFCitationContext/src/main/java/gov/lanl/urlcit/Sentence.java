package gov.lanl.urlcit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Sentence {
	// order of sentance
	int order;
	String url = "";
	String text = "";
	// if citation has bibliographic title
	String title = "";
	// title of nearest section
	String head = "";
	String sentenceid = "";
	// order of paragraph
	int porder;
	// sentance can have several urls
	Map urltitle = new HashMap();
	// all urls can have bib tiles
	List urllist = new ArrayList();

	String loc = "I";
	String fdate = "";
	Map urllocs = new HashMap();
	
	public void setUrl(String u) {
		this.url = u;
		if (!urllist.contains(u)){
		urllist.add(url);
		}
	    	
	}

	public void setText(String u) {
		this.text = u;
	}

	public void setLoc(String u) {
		this.loc = u;
	}
	public void setDate(String u) {
		this.fdate = u;
	}
	public void setTitle(String title, String url) {
		//this.title = title;
		//this.url = url;
		urltitle.put(url, title);
	}
	public void setLocs(String loc, String url) {
		
		this.urllocs.put(url, loc);
	}
	public void setHead(String title) {
		this.head = title;
	}

	public void setSentenceid(String title) {
		this.sentenceid = title;
	}

	public void setOrder(int i) {
		order = i;
	}

	public void setPorder(int i) {
		porder = i;
	}

	public List getUrlList() {
		return this.urllist;
	}

	public Map getUrlTitle() {
		return this.urltitle;
	}
	public Map getUrlLoc() {
		return this.urllocs;
	}
	public String getUrl() {
		return this.url;
	}

	public String getText() {
		return this.text;
	}

	public String getLoc() {
		return this.loc;
	}
	public String getfdate() {
		return this.fdate;
	}
	public String getTitle() {
		return this.title;
	}

	public String getHead() {
		return this.head;
	}

	public String getSentenceid() {
		return this.sentenceid;
	}

	public int getOrder() {
		return order;
	}

	public int getPorder() {
		return porder;
	}

	// Serialize Sentence object to JSON
	public String toJson() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace(); // Handle the exception according to your needs
			return null;
		}
	}

}
