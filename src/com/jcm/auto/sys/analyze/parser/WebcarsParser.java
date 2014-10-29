package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class WebcarsParser extends AbstractParser {

    public void parse(Site site, String html) {

      // TODO 插入mongo中
      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".allcent .jeeptd");
      for (int i = 0; i < es.size(); i++) {
        Element brandNode = es.get(i);
        // dl > dd > ... > a
        String brand = brandNode.select("dl dd a").text();
        // .chiList
        Elements cm = brandNode.select(".chiList");
        for (int j = 0; j < cm.size(); j++) {
          Element company = cm.get(j);
          // div > span > a
          String com = company.child(0).child(0).text();
          // ul > li > span > a
          Elements models = cm.get(j).select("li span a");
          for (int k = 0; k < models.size(); k++) {
            String model = models.get(k).text();
            saveModel(new SiteModel(site.getName(), brand, com, model));
          }
        }
      }
    }
}
