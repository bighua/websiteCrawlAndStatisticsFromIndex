package com.jcm.auto.sys.analyze.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class BitautoParser extends AbstractParser {

    public void parse(Site site, String html) {

      // TODO 插入mongo中
      Document doc = Jsoup.parse(html);
      Element brandNode = doc.select("[listtype=cartype]").get(0);
      
      // title-con > title-box > h3 > a
      String brand = brandNode.child(0).child(0).child(0).child(1).text();
//      System.out.println("brand:" + brand);
      Elements cm = doc.getElementById("divCsLevel_0").children();
      for (int i = 0; i < cm.size(); i++) {
        Element comOrMod = cm.get(i);
        String company = "";
        if (comOrMod.hasClass("title-con-2")) {
          // h5 > a
          company = comOrMod.child(0).child(0).text().replace(">>", "");
          comOrMod = cm.get(++i);
        } else {
          company = brand;
        }
//        System.out.println("company:" + com);
        // ul > li > a
        Elements models = comOrMod.select("li > a");
        for (int k = 0; k < models.size(); k++) {
//          System.out.println("model:" + models.get(k).attr("title"));
        	String model = models.get(k).attr("title");
        	saveModel(new SiteModel(site.getName(), brand, company, model));
        }
      }
      // 
    }
}
