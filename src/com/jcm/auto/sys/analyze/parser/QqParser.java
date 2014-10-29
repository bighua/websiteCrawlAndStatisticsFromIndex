package com.jcm.auto.sys.analyze.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;

public class QqParser extends AbstractParser {

    public void parse(Site site, String html) throws JSONException {

      Document doc = Jsoup.parse(html);
      Elements es = doc.getElementsByTag("script");
      for (Element e : es) {
        String script = e.toString();
        int index = script.indexOf("oBrandSerialData");
        if (index > -1) {
        	script = e.childNode(0).toString();
        	script = script.substring(script.indexOf("var oBrandSerialData = ") + "var oBrandSerialData = ".length());
//        	try {
				JSONObject oBrandSerialData = new JSONObject(script);
				JSONArray flList = oBrandSerialData.getJSONArray("list");
				for (int i = 0; i < flList.length(); i++) {
					JSONObject fl = flList.getJSONObject(i);
					JSONArray brandList = fl.getJSONArray("BrandList");
					for (int j = 0; j < brandList.length(); j++) {
						JSONObject brandNode = brandList.getJSONObject(j);
						// brandName 品牌
						String brand = brandNode.getString("brandName");
						JSONArray manList = brandNode.getJSONArray("manList");
						// manList 厂商
						for (int k = 0; k < manList.length(); k++) {
							JSONObject company = manList.getJSONObject(k);

							// manName 厂商
							String com = company.getString("manName");
							JSONArray serialList = company.getJSONArray("serialList");
							// serialList 
							for (int m = 0; m < serialList.length(); m++) {
								JSONObject modelNode = serialList.getJSONObject(m);
								String model = modelNode.getString("serialName");
						        saveModel(new SiteModel(site.getName(), brand, com, model));
							}
						}
					}
				}
//			} catch (JSONException e2) {
//				e2.printStackTrace();
//			}
        }
      }
    }
}
