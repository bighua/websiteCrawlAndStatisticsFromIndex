package com.jcm.auto.sys.analyze.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jcm.auto.sys.analyze.ParserManager;
import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.util.ObjectUtil;

public class BitautoBrandParser extends AbstractParser {

    public void parse(Site site, String html) {

      List<Site> newSites = null;
      String jsonStr = html.substring("JsonpCallBack(".length(), html.length() - 1);
      try {
        JSONObject brandTree = new JSONObject(jsonStr);
        
        JSONObject brands = brandTree.getJSONObject("brand");
        @SuppressWarnings("unchecked")
        Iterator<String> fl = brands.keys();
        
        newSites = new ArrayList<Site>();
        while (fl.hasNext()) {
          String key = fl.next();
          JSONArray lbrand = brands.getJSONArray(key);
          for (int i = 0; i < lbrand.length(); i++) {
            JSONObject b = lbrand.getJSONObject(i);
            Site newSite = new Site();
            ObjectUtil.beanCopy(site, newSite, false);
            newSite.setParseSite(site.getDomain() + b.getString("url"));
            newSite.setParser("com.jcm.auto.sys.analyze.parser.BitautoParser");
            newSites.add(newSite);
          }
        }
        ParserManager.addTask(newSites);
        ParserManager.log.info(String.format("从【%s】中提取 %s个新任务.", site.getParseSite(), newSites.size()));
      } catch (JSONException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
}
