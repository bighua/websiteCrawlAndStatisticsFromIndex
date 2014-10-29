package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class XgoParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".main_zimu .main_nr");
      for (int i = 0; i < es.size(); i++) {
        Element brandNode = es.get(i);
        // .l > a
        String brand = null;
        // [北汽威旺]字节编码问题
        if (brandNode.child(0).children().size() == 2) {
          brand = brandNode.child(0).child(1).text();
        } else {
          brand = brandNode.child(0).child(0).text();
        }
        // .r 
        Elements cm = brandNode.child(1).children();
        for (int j = 0; j < cm.size(); j++) {
          Element company = cm.get(j++);
          // .r > .car
          String com = brand;
          if (company.children().size() != 0)
            com = company.child(0).text();
          // .r > ul > li > dl > dt > a
          Elements models = cm.get(j).select("dt a");
          for (int k = 0; k < models.size(); k++) {
            String model = models.get(k).text();
            saveModel(new SiteModel(site.getName(), brand, com, model));
          }
        }
      }
    }
}
