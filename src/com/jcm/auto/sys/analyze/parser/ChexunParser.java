package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class ChexunParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".center .ppxzz tr");
      for (int i = 0; i < es.size(); i++) {
        Element brandNode = es.get(i);
        // td > div> a
        String brand = null;
        brand = brandNode.child(0).child(0).child(0).text();
        // td > div
        Elements cm = brandNode.child(1).child(0).children();
        for (int j = 0; j < cm.size(); j++) {
          Element company = cm.get(j++);
          // h4 > a
          String com = company.child(0).text();
          // ul > li > a
          Elements models = cm.get(j++).select("a");
          for (int k = 0; k < models.size(); k++) {
            String model = models.get(k).text();
            saveModel(new SiteModel(site.getName(), brand, com, model));
          }
        }
      }
    }
}
