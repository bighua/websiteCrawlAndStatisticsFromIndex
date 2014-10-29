package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class AutohomeParser extends AbstractParser {

	@Override
    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements es = doc.select("div.row dl");
      for (int i = 0; i < es.size(); i++) {
        Element bcm = es.get(i);
        // dt > p > a
        String brand = bcm.child(0).child(1).child(0).text();
        // dd
        Elements cm = bcm.child(1).children();
        int j = 0;
        while (j < cm.size()) {
        	Element e = cm.get(j++);
        	String company = brand;
        	// 有厂商
        	if ("h3".equals(e.nodeName())) {
        		// h3
        		company = e.child(0).text();
        	} else {
	        	// ul > li
		        Elements models = e.children();
		        for (int k = 0; k < models.size(); k++) {
		          // li > h4 > a
		        	String model =  models.get(k).child(0).child(0).text();
		        	saveModel(new SiteModel(site.getName(), brand, company, model));
		        }
        	}
        }
      }
    }
}
