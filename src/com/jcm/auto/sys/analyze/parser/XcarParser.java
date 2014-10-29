package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class XcarParser extends AbstractParser {

    public void parse(Site site, String html) {

      // TODO 插入mongo中
      Document doc = Jsoup.parse(html);
      Elements es = doc.select(".t0922con_nt .t0922_pinpaitab tr");
      for (int i = 0; i < es.size(); i++) {
      Element brandNode = es.get(i);
      Elements bcm = brandNode.select("td");
      String brand = bcm.get(0).child(2).text();
      Elements cm = bcm.get(1).children();
      int j = 0;
      while (j < cm.size()) {
        Element company = cm.get(j++);
        String com = company.child(0).text(); // a标签
        Elements models = cm.get(j++).select("a");
        for (int k = 0; k < models.size(); k++) {
          String model = models.get(k).text();
          saveModel(new SiteModel(site.getName(), brand, com, model));
        }
      }
    }
   }
}
