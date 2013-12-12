package com.jcm.statistics.bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.jcm.statistics.cache.Cache;

public abstract class BaseData {

    protected long total = 0;
    
    protected String dateTime = null;
    
    protected boolean isPolluted = false;

    protected Map<String, Long> dataCount = new HashMap<String, Long>();

    public void setCount(String name, long count) {
        Long originalCount = dataCount.put(name, count);
        if (originalCount == null || count != originalCount) {
            isPolluted = true;
        }
    }
    
    public long getCount(String name) {
        Long count = dataCount.get(name);
        if (count == null) {
            count = 0L;
        }
        return count;
    }
    
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        if (this.total != total) {
            isPolluted = true;
        }
        this.total = total;
    }

    public boolean isPolluted() {
        return isPolluted;
    }
    
    public void setPolluted(boolean polluted) {
        this.isPolluted = polluted;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    
    public String getDateTime() {
        return dateTime;
    }

    public void resetPolluted() {
        isPolluted = false;
    }
    
    public abstract void createData(QueryResponse qr, String tableCol, Cache cache, String dimension, StringBuffer sb) throws IOException;

}
