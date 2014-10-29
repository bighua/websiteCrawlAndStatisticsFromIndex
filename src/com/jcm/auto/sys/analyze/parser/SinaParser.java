package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class SinaParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".pinpai dl");
      for (int i = 0; i < es.size(); i++) {
        Element brandNode = es.get(i);
        // dt > em > a
        String brand = brandNode.child(0).child(1).child(0).text();
        // dd > div
        Elements cm = brandNode.child(1).children();
        for (int j = 0; j < cm.size(); j++) {
          Element company = cm.get(j);
          // div > h6 > a
          String com = brand;
          Elements models = null;
          // 【北京汽车】乱码问题
          if (company.children().size() == 1) {
              models = company.child(0).select("li strong a[title]");
          } else {
            com = company.child(0).child(0).text();
            // div > ul > li
            models = company.child(1).select("li strong a[title]");
          }
          for (int k = 0; k < models.size(); k++) {
            String model = models.get(k).text();
            saveModel(new SiteModel(site.getName(), brand, com, model));
          }
        }
      }
    }
}
