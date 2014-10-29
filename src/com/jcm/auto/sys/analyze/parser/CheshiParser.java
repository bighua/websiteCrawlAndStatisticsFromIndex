package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class CheshiParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements es = doc.select("#main .contentCell");
      for (int i = 0; i < es.size(); i++) {
        Element brandNode = es.get(i);
        // .contentCellLeft a
        String brand = brandNode.select(".contentCellLeft a").get(0).text();
        // .contentCellRight dl
        Elements cm = brandNode.select(".contentCellRight dl");
        for (int j = 0; j < cm.size(); j++) {
          Element company = cm.get(j);
          // dt > a
          String com = company.child(0).child(0).text();
          // ul > li > a
          Elements models = company.select("dd");
          for (int k = 0; k < models.size(); k++) {
            // dd > p > a
            String model = models.get(k).child(0).child(0).text();
            saveModel(new SiteModel(site.getName(), brand, com, model));
          }
        }
      }
    }
}
