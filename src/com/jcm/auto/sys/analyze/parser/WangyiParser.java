package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class WangyiParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements brandNodes = doc.select("#qcdq_brand2 [id^=g]");
      for (Element brandNode : brandNodes) {
        // div > h3 > a
        String brand = brandNode.child(0).child(1).child(0).text();
        String com = "";
        Elements models = brandNode.select(".gbox1 li .autocard");
        for (int i = 0; i < models.size(); i++) {
        	com = brand;
        	Element modelNode = models.get(i);
        	// .cardhd > h5 > a
            String model = modelNode.select(".cardhd a").get(0).text();
            
            if (modelNode.child(1).children().size() == 2) com = modelNode.child(1).child(1).attr("title");
            saveModel(new SiteModel(site.getName(), brand, com, model));
        }
        Elements outsideModels = brandNode.select(".gbox2 .info a");
        if (!outsideModels.isEmpty()) {
        	com = brand;
	        for (int i = 0; i < outsideModels.size(); i++) {
	            String model = outsideModels.get(i).text();
	            saveModel(new SiteModel(site.getName(), brand, com, model));
	        }
        }
      }
    }
}
