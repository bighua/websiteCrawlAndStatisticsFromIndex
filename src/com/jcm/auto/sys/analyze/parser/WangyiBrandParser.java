package com.jcm.auto.sys.analyze.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.analyze.ParserManager;
import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.util.ObjectUtil;

public class WangyiBrandParser extends AbstractParser {

    public void parse(Site site, String html) {

      Document doc = Jsoup.parse(html);
      Elements letters = doc.select(".global_bar dl dd a");
      List<Site> newSites = new ArrayList<Site>();
      for (Element e : letters) {
          Site newSite = new Site();
          try {
            ObjectUtil.beanCopy(site, newSite, false);
          } catch (NoSuchMethodException | SecurityException
                  | IllegalAccessException | IllegalArgumentException
                  | InvocationTargetException e1) {
              e1.printStackTrace();
          }
          newSite.setParseSite(site.getDomain() + e.attr("href"));
          newSite.setParser("com.jcm.auto.sys.analyze.parser.WangyiParser");
          newSites.add(newSite);
      }
      ParserManager.addTask(newSites);
    }
}
