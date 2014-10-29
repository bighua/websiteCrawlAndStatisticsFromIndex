package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class IfengParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".lt-list dl");
      for (int i = 0; i < es.size(); i++) {
        Element brandNode = es.get(i);
        // dt > a
        String brand = brandNode.child(0).child(1).text();
        // dd
        Elements cm = brandNode.child(1).children();
        for (int j = 0; j < cm.size(); j++) {
          Element company = cm.get(j++);
          // div > a
          String com = company.child(0).text();
          // ul > li
          Elements models = cm.get(j++).children();
          for (int k = 0; k < models.size(); k++) {
            // li > a
            String model = models.get(k).child(0).text();
            saveModel(new SiteModel(site.getName(), brand, com, model));
          }
        }
      }
    }
}
