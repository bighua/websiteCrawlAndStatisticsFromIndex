package com.jcm.auto.sys.analyze;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.jcm.auto.sys.analyze.parser.AbstractParser;
import com.jcm.auto.sys.beans.analyze.Site;
import com.ztools.conf.Environment;
import com.ztools.xml.XMLReader;

@SuppressWarnings("unchecked")
public class ParserManager {

    private static final String sitePath = "conf/analyze/original-sites.xml";
    private static ConcurrentLinkedQueue<Site> siteQueue = null;
    private static AtomicInteger countDown = null;
    private static IAnalyze analyzeDB = null;
    public static Logger log = Logger.getLogger(ParserManager.class);
    
    static {
      Object sites = XMLReader.xmlStreamToObject(Environment.findInputStreamByResource(sitePath, ParserManager.class), null);
      if (sites instanceof List) {
        countDown = new AtomicInteger(((List<Site>)sites).size());
        siteQueue = new ConcurrentLinkedQueue<Site>((List<Site>)sites);
        analyzeDB = new AnalyzeImpl();
      }
    }
    
    public static IAnalyze getAnalyze() {
    	return analyzeDB;
    }
    
    public static AbstractParser getParser(String parser) {
      try {
        return (AbstractParser)(Class.forName(parser).newInstance());
      } catch (InstantiationException | IllegalAccessException
        | ClassNotFoundException e) {
        e.printStackTrace();
      }
      return null;
    }
    
//    public static ConcurrentLinkedQueue<Site> getSites() {
//      return siteQueue;
//    }
    public static int uncompleteTaskCount() {
    	return countDown.get();
    }
    
    public static boolean hasTask() {
      return countDown.get() > 0;
    }
    
    public static void taskComplete() {
      countDown.getAndDecrement();
    }
    
    public static void addTask(Site s) {
      // 这两步操作不用同步
      siteQueue.add(s);
      countDown.getAndIncrement();
    }
    
    public static void addTask(List<Site> ss) {
      // 这两步操作不用同步
      siteQueue.addAll(ss);
      countDown.getAndAdd(ss.size());
    }
    
    public static int queueSize() {
    	return siteQueue.size();
    }
    
    public static Site getTask() {
      return siteQueue.poll();
    }
    
    public static void main(String[] args) {
    
    }
}
