package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class PcautoParser extends AbstractParser {

    public void parse(Site site, String html) {

      // TODO 插入mongo中
      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".icontent > div");
      for (int i = 0; i < es.size(); i++) {
      // div.main
      Element brandNode = es.get(i);
      // 
      String brand = brandNode.child(0).select("p").get(0).text();
      Elements cm = brandNode.child(1).children();
      for (int j = 0; j < cm.size(); j++) {
        Element company = cm.get(j);
        // modA > thA > a
        String com = company.child(0).child(0).text(); // a标签
        Elements models = company.child(1).select("dd");
        for (int k = 0; k < models.size(); k++) {
          // dd > p > a
          String model = models.get(k).child(0).child(0).text();
          saveModel(new SiteModel(site.getName(), brand, com, model));
        }
      }
    }
   }
}
