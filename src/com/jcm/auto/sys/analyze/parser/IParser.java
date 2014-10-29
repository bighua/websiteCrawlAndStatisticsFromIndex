package com.jcm.auto.sys.analyze.parser;

import com.jcm.auto.sys.beans.analyze.Site;


public interface IParser {

    public void parse(Site site, String html);
}
