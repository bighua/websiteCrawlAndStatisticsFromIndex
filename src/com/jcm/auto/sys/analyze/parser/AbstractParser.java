package com.jcm.auto.sys.analyze.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcm.auto.sys.analyze.ParserManager;
import com.jcm.auto.sys.beans.analyze.Site;
import com.jcm.auto.sys.beans.analyze.SiteModel;


public abstract class AbstractParser {
	private List<SiteModel> model2Insert = new ArrayList<SiteModel>();
	private Set<String> keySet = new HashSet<String>();
	
    public abstract void parse(Site site, String html) throws Exception;

    public void parseAndSave(Site site, String html) throws Exception {
    	parse(site, html);
    	if (model2Insert.size() != 0) 
          ParserManager.getAnalyze().saveModels(model2Insert);
    }
    
    protected void saveModel(SiteModel sm) {
    	if (!keySet.contains(sm.getId())) {
    	  model2Insert.add(sm);
    	  keySet.add(sm.getId());
    	}
    }
}
