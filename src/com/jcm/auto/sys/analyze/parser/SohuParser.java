package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class SohuParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".category_main .blk_meta");
      for (int i = 0; i < es.size(); i++) {
        Elements brandNode = es.get(i).children();
        
        // .meta_left > a > p
        String brand = brandNode.get(0).child(0).child(1).text();
        for (int j = 1; j < brandNode.size(); j++) {
          // .meta_con
          Elements cm = brandNode.get(j).children();
          Element company = cm.get(0);
          // div > a
          String com = company.child(0).text();
          // ul > li
          Elements models = cm.get(1).children();
          for (int k = 0; k < models.size(); k++) {
            // li > .name
            String model = models.get(k).select(".name").get(0).text();
            saveModel(new SiteModel(site.getName(), brand, com, model));
          }
        }
      }
    }
}
